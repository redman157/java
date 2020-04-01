package com.droidheat.musicplayer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.database.CategorySongs;
import com.droidheat.musicplayer.manager.CommonUtils;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener{
    // Binder given to clients


    private final IBinder iBinder = new LocalBinder();
    private String tag = "BBB";

    //MediaSession
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSessionCompat;
    private MediaControllerCompat.TransportControls mMediaTransportControls;
    private SongsManager mSongsManager;
    private SharedPrefsManager mSharedPrefsManager;
    private AudioManager mAudioManager;
    //AudioPlayer notification ID

    private final Handler handler = new Handler();
    private boolean isInitAudioError;
    /**
     * Static*/
    public static final int NOTIFICATION_ID = 101;
    public static boolean isStarted;
    public static MediaPlayer mMediaPlayer;
    public static ArrayList<SongModel> mSongs;
    // check if end of audio list
    private boolean endOfAudioList;
    private int resumePosition;
    private int newPosition;
    private int position;
    private Intent iIntentSeekBar, iPlayNewMusic, iCheckPlayActivity, iPlayPauseActivity;
    private Intent iPosPrev, iPosNext, iPosPlay, iPosPause;
    private String title, fileName, path, albumId, album, artists;
    private Intent iPrevToActivity, iNextToActivity;
    private androidx.core.app.NotificationCompat.Builder notificationBuilder = null;
    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods

            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mMediaSessionCompat != null) {
            mMediaSessionCompat.release();
        }
        removeNotification();
        return super.onUnbind(intent);
    }

    /**
    *  Init Service
    * */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "Start service" );

        // ---Set up intent for seek bar broadcast ---
        iPlayPauseActivity = new Intent(Constants.ACTION.BROADCAST_PLAY_PAUSE);
        iIntentSeekBar = new Intent(Constants.ACTION.BROADCAST_SEEK_BAR);
        iCheckPlayActivity = new Intent(Constants.ACTION.IS_PLAY);
        iPlayNewMusic = new Intent(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO);



        mSongsManager = SongsManager.getInstance();
        mSongsManager.setContext(this);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        mSongs = mSongsManager.queue();





        // khởi tạo media và chờ start
        // cần tao 1 broad cast để play
        initMediaPlayer();
        initMediaSession();

        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        registerReceiver(brResetMusic,  new IntentFilter(Constants.ACTION.BROADCAST_RESET_AUDIO));
        registerReceiver(brStopMusic, new IntentFilter(Constants.ACTION.BROADCAST_STOP_AUDIO));
        registerReceiver(brSeekBar, new IntentFilter(Constants.ACTION.BROADCAST_SEEK_BAR));
        registerReceiver(brCloseNotification, new IntentFilter(Constants.ACTION.CLOSE_NOTIFICATION));


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d(tag,"Service --- onStartCommand:  Enter ===== Action: "+intent.getAction());

        position = mSharedPrefsManager.getInteger(Constants.PREFERENCES.POSITION, -1);

        title = mSongs.get(position).getTitle();
        path = mSongs.get(position).getPath();
        fileName = mSongs.get(position).getFileName();
        artists = mSongs.get(position).getArtist();
        albumId = mSongs.get(position).getAlbum();
        album = mSongs.get(position).getAlbum();

        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
            Log.d(tag, "requestAudioFocus: ENTER");
        }
        if (intent.getAction() != null) {
            //Handle Intent action from MediaSession.TransportControls
            handleIncomingActions(intent);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            SongsManager.getInstance().setCurrentMusic(position);
            stopMedia();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioManager != null) {
            removeAudioFocus();
        }
        removeNotification();

        unregisterReceiver(brResetMusic);
//        unregisterReceiver(brPlayNewVideo);
        unregisterReceiver(brStopMusic);
        unregisterReceiver(brCloseNotification);

        if (mSongs != null){
//            unregisterReceiver(brPlayPause);
            unregisterReceiver(brSeekBar);
            unregisterReceiver(becomingNoisyReceiver);
        }
    }

    private void initMediaSession(){
        if (mMediaSessionManager != null) {
            return; //mediaSessionManager exists
        }
        mMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        mMediaTransportControls = mMediaSessionCompat.getController().getTransportControls();

        //set MediaSession -> ready to receive media commands
        mMediaSessionCompat.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Attach Callback to receive MediaSession updates
        // MediaTransportControls -> mMediaSessionCompat.setCallBack -> thao tác cho mediaplayer
        mMediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                if( !successfullyRetrievedAudioFocus() ) {
                    return;
                }
//                int position = SongsManager.getInstance().getCurrentMusic();
                Log.d("BBB","Service --- onPlay:"+position);
                playMedia(mSongs.get(position).getPath());
                initNotification(Constants.NOTIFICATION.PLAY, position);

            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();

                initNotification(Constants.NOTIFICATION.PAUSE, position);

            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                SongsManager.getInstance().setCurrentMusic(position + 1);
                skipToNext();
                initNotification(Constants.NOTIFICATION.PLAY, position );

            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                SongsManager.getInstance().setCurrentMusic(position - 1);
                skipToPrevious();
                initNotification(Constants.NOTIFICATION.PLAY, position);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.seekTo((int) (long) position);
                }else {
                    newPosition = (int) position;
                }

            }
        });
    }

    /**
     * ACTION INTENT
     */
    private void handleIncomingActions(Intent iAction){
        if (iAction == null || iAction.getAction() == null) {
            return;
        }
        String action = iAction.getAction();

        if (action != null) {
            switch (action) {
                case Constants.ACTION.PLAY:
                    mMediaTransportControls.play();
//                    this.iPosPlay = iAction;
                    break;
                case Constants.ACTION.PAUSE:
                    mMediaTransportControls.pause();

                    break;
                case Constants.ACTION.NEXT:
                    mMediaTransportControls.skipToNext();
                    this.iNextToActivity = iAction;

                    break;
                case Constants.ACTION.PREVIOUS:

                    mMediaTransportControls.skipToPrevious();
                    this.iPrevToActivity = iAction;
//                    this.iPosPrev = iAction;

                    break;
                case Constants.ACTION.STOP:
                    mMediaTransportControls.stop();
                    break;
                case Constants.ACTION.SEEK:
                    int pos = iAction.getIntExtra(Constants.PREFERENCES.POSITION_SONG, 0);

                    if (pos != 0) {
                        mMediaTransportControls.seekTo(pos);
                    }
                    break;
                case Constants.ACTION.IS_PLAY:

                    if (mMediaPlayer.isPlaying()){

                        iCheckPlayActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, true);
                        sendBroadcast(iCheckPlayActivity);
                    }else {

                        iCheckPlayActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, false);
                        sendBroadcast(iCheckPlayActivity);
                    }

                    break;
                case Constants.ACTION.REPEAT:

                    break;
            default:
                initNotification(Constants.NOTIFICATION.PAUSE, position);
                break;
            }
        }
        /*if (action.equals(Constants.ACTION.PLAY)) {
            if (SongsManager.getInstance().getCurrentMusic() == mSongs.size()) {

                Intent audioIntent = new Intent(Constants.ACTION.BROADCAST_RESET_AUDIO);
                sendBroadcast(audioIntent);
            }
        }*/

    }
    private void initMediaPlayer()  {
        mMediaPlayer = new MediaPlayer();

        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);

        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);


    }

    private boolean requestAudioFocus() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.abandonAudioFocus(this);
    }


    /*
     * saveData() writes current song parameters to sharedPrefs which can be retrieved in
     * other activities or fragments as well as when we start app next time
     * musicID: is id of current item in queue
     * title, artist, album, albumid: are all fields of SongModel()
     *
     */

    private void saveData(){
        int musicID = mSongsManager.getCurrentMusic();
        mSharedPrefsManager.setInteger(Constants.PREFERENCES.audio_session_id,
                    mMediaPlayer.getAudioSessionId());
        try {


            mSharedPrefsManager.setInteger(Constants.PREFERENCES.MUSIC_ID ,
                    musicID);

            mSharedPrefsManager.setString(Constants.PREFERENCES.TITLE ,
                    mSongsManager.queue().get(musicID).getTitle());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ARTIST ,
                    mSongsManager.queue().get(musicID).getArtist());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ALBUM ,
                    mSongsManager.queue().get(musicID).getAlbum());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ALBUMID ,
                    mSongsManager.queue().get(musicID).getAlbumID());

            mSharedPrefsManager.setString(Constants.PREFERENCES.RAW_PATH ,
                    mSongsManager.queue().get(musicID).getPath());

            mSharedPrefsManager.setString(Constants.PREFERENCES.DURATION ,
                    mSongsManager.queue().get(musicID).getDuration());

            mSharedPrefsManager.setInteger(Constants.PREFERENCES.DURATION_IN_MS ,
                    mMediaPlayer.getDuration());
        }catch (Exception e ){
            Log.d(tag, "Unable to save song info in persistent storage. MusicID " + musicID);
        }
    }



    private void LogMediaPosition(){

        try{
            int mediaPosition = mMediaPlayer.getCurrentPosition();

            int mediaMax = mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTime();
           /* Log.d("MMM", "LogMediaPosition : Enter ==== MediaPosition: "+mediaPosition +" ==== " +
                    "MediaMax: "+mediaMax +" ==== Song Title: "+ mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTitle());
*/

            iIntentSeekBar.putExtra("current_pos", mediaPosition);
            iIntentSeekBar.putExtra("media_max", mediaMax);
            iIntentSeekBar.putExtra("song_title",
                    mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTitle());
            sendBroadcast(iIntentSeekBar);

        }catch (Exception e){
            Log.e(tag, "ERROR: " + e.toString());

        }
    }


    private void playMedia(String path){

        if (!mMediaPlayer.isPlaying()) {
            setMediaPlayer(path);
            isInitAudioError = false;

        }
    }
    private void playMedia(){

        if (!mMediaPlayer.isPlaying() ) {
            mMediaPlayer.start();
            isInitAudioError = false;
        }

    }
    private void setMediaPlayer(String path) {
        if (mMediaPlayer != null){
            mMediaPlayer.reset();
        }

        File file = new File(path);
        if (file.exists()) {
            try {
                addVoteToTrack(path);
                mMediaPlayer.setDataSource(path);
            } catch (IOException e) {
                skipToNext();
                e.printStackTrace();
            }

            try {
                mMediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(tag, "Error prepare: "+e.getMessage());
            }
        } else {
            skipToNext();
            Log.d(tag, "Error finding file so we skipped to next.");
            (new CommonUtils(this)).showTheToast("Error finding music file");
        }
    }
    private void pauseMedia() {

        if (mMediaPlayer.isPlaying()) {
            Log.d(tag, "pauseMedia: Enter");
            mMediaPlayer.pause();
            resumePosition = mMediaPlayer.getCurrentPosition();

        }
    }

    private void stopMedia() {
        if (mMediaPlayer != null) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    Log.d(tag, "stopMedia: Enter");

                    mMediaPlayer.stop();
                }
                handler.removeCallbacks(sendUpdatesToUI);
            } catch (Exception e) {
                Log.d(tag, "stopMedia ERROR: " + e.getMessage());
            }
        }
    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
//            mMediaPlayer.seekTo(resumePosition);
            mMediaPlayer.seekTo(newPosition);
            mMediaPlayer.start();
            newPosition = -1;
//            Log.d(tag, "resumeMedia: Enter");
        }
    }


    private void skipToPrevious(){
        Log.d(tag, "skipToPrevious: Enter");


        Log.d("BBB", "Service --- skipToPrevious: "+position);
        Bundle bdPrev = iPrevToActivity.getExtras();

        if (bdPrev != null) {
            String prevToActivity = bdPrev.getString(Constants.INTENT.PREVIOUS_TO_SERVICE);

            iPlayNewMusic.putExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY,
                    prevToActivity);
            sendBroadcast(iPlayNewMusic);
        }
        // set up dù có trường hợp là <0 vẫn xảy ra


        stopMedia();
        //reset mediaPlayer
        mMediaPlayer.reset();

    }


    private void skipToNext(){

        Log.d("BBB", "Service --- skipToNext: "+position);
        if (iNextToActivity != null) {
            Bundle bdNext = iNextToActivity.getExtras();
            if (bdNext != null) {
                String nextToActivity = bdNext.getString(Constants.INTENT.NEXT_TO_SERVICE);
                iPlayNewMusic.putExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY, nextToActivity);
                sendBroadcast(iPlayNewMusic);
            } else {
                Log.d("KKK", "Service --- SkipToNext: dbNext is null");
            }
        }
        stopMedia();
        mMediaPlayer.reset();



    }
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 1000);

        }
    };
    // ---Send seek bar info to activity----
    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1);
    }
    /******* ---------------------------------------------------------------
     Notifications
     ----------------------------------------------------------------*******/
    private void updateMetaData(SongModel songModel){
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_music_note_black_24dp);
        // Update the current metadata
        if (SongsManager.getInstance().getCurrentMusic() != -1){
            mMediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mSongs.get(SongsManager.getInstance().getCurrentMusic()).getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mSongs.get(SongsManager.getInstance().getCurrentMusic()).getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songModel.getTitle())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, songModel.getTime())
                    .build());
        }
    }
    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        stopMedia();
    }
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager
                    mNotificationManager =
                    (NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String id = "channel_music_playback";
            // The user-visible name of the channel.
            CharSequence name = "Media Playback";
            // The user-visible description of the channel.
            String description = "Media playback controls";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.setShowBadge(false);
            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    private void initNotification(String type, int position){
        int icon_Action = 0;
//        int position = SongsManager.getInstance().getCurrentMusic();


        PendingIntent playPauseIntent = null;
        PendingIntent nextIntent;
        PendingIntent prevIntent ;
        // GỬI INTENT TỪ NOTI VỀ SERVICE
        Intent iNext = new Intent(this, MediaPlayerService.class);
        iNext.setAction(Constants.ACTION.NEXT);
        Bundle bdNext = new Bundle();
        bdNext.putString(Constants.INTENT.NEXT_TO_SERVICE, "NextToService");
        iNext.putExtras(bdNext);

        // GỬI INTENT TỪ NOTI VỀ SERVICE
        Intent iPrev = new Intent(this, MediaPlayerService.class);
        iPrev.setAction(Constants.ACTION.PREVIOUS);
        Bundle bdPrevious = new Bundle();
        bdPrevious.putString(Constants.INTENT.PREVIOUS_TO_SERVICE, "PreviousToService");
        iPrev.putExtras(bdPrevious);

        if (type.equals(Constants.NOTIFICATION.PLAY)){

            icon_Action = R.drawable.ic_pause_black;
            playPauseIntent = PendingIntent.getService(this, 0,
                    (new Intent(this, MediaPlayerService.class))
                            .setAction(Constants.ACTION.PAUSE),
                    0);
            iPlayPauseActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_NOTIFICATION, false);
            sendBroadcast(iPlayPauseActivity);
        }
        else if (type.equals(Constants.NOTIFICATION.PAUSE)){
            icon_Action = R.drawable.ic_play_button_black;
            playPauseIntent = PendingIntent.getService(this, 0,
                    (new Intent(this, MediaPlayerService.class)).setAction(Constants.ACTION.PLAY),
                    0);
            iPlayPauseActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_NOTIFICATION, true);
            sendBroadcast(iPlayPauseActivity);
        }
        nextIntent = PendingIntent.getService(this, 0,
            iNext, 0);


        prevIntent = PendingIntent.getService(this, 0,
            iPrev, 0);


        createChannel();

        Bitmap bitmap = ImageUtils.getInstance(this).getBitmapIntoPicasso(mSongs.get(position).getAlbumID());



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new androidx.core.app.NotificationCompat
                .Builder(this, getString(R.string.default_notification_channel_id))
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setAutoCancel(true)
                .setShowWhen(false)

                // Set the Notification color
                .setColorized(true).setColor(getResources().getColor(R.color.white))
//                .setLargeIcon()
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setLargeIcon(bitmap)
                .setSubText(mSongs.get(position).getFileName())

                .setContentTitle(mSongs.get(position).getTitle())
                .setContentText(mSongs.get(position).getArtist())
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // click notification intent to home activity
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0))
                // Add playback actions
                .addAction(position == 0 ?
                                R.drawable.ic_previous_black : R.drawable.ic_previous_white,
                        Constants.NOTIFICATION.PREVIOUS,
                        prevIntent)
                .addAction(icon_Action, Constants.NOTIFICATION.PAUSE, playPauseIntent)

                .addAction(position == mSongs.size() ?
                                R.drawable.ic_next_black : R.drawable.ic_next_white,
                        Constants.NOTIFICATION.NEXT,
                        nextIntent)
                .addAction(R.drawable.ic_close_black_24dp, "Stop", closeNotification())

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()

                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2)
                        // Attach our MediaSession token
                        .setMediaSession(mMediaSessionCompat.getSessionToken()));

        }
        else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setChannelId(getString(R.string.default_notification_channel_id))
                    .setAutoCancel(true)
                    .setShowWhen(false)

                    // Set the Notification color
                    .setColorized(true).setColor(getResources().getColor(R.color.white))
//                .setLargeIcon()
                    .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                    .setLargeIcon(bitmap)
                    .setSubText(mSongs.get(position).getFileName())

                    .setContentTitle(mSongs.get(position).getTitle())
                    .setContentText(mSongs.get(position).getArtist())
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    // click notification intent to home activity
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0))
                    // Add playback actions
                    .addAction(
                            R.drawable.ic_previous_white ,
                            Constants.NOTIFICATION.PREVIOUS,
                            prevIntent)
                    .addAction(icon_Action, Constants.NOTIFICATION.PAUSE, playPauseIntent)

                    .addAction(
                            R.drawable.ic_next_white ,
                            Constants.NOTIFICATION.NEXT,
                            nextIntent)
                    .addAction(R.drawable.ic_close_black_24dp, "Stop", closeNotification())

                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()

                            // Show our playback controls in the compact notification view.
                            .setShowActionsInCompactView(0, 1, 2)
                            // Attach our MediaSession token
                            .setMediaSession(mMediaSessionCompat.getSessionToken()));



        }
        notificationBuilder.setDeleteIntent(dismissNotification());
        startForeground(NOTIFICATION_ID,
                notificationBuilder.build());
    }

    // set trạng thái action của notification -> service
    public void setStatusNoti(boolean isPlaying){
        Log.d(tag,"Service --- SetStatusNoti: "+isPlaying );

        Intent iAction = new Intent(this, MediaPlayerService.class);
        updateMetaData(mSongs.get(SongsManager.getInstance().getCurrentMusic()));
        if (isPlaying){

            iAction.setAction(Constants.ACTION.PLAY);

        }else {
            iAction.setAction(Constants.ACTION.PAUSE);
        }

        Log.d(tag, "iAction: "+iAction.getAction());
        handleIncomingActions(iAction);
    }

    /*
    Audio Manager Focus Change
    * */
    @Override
    public void onAudioFocusChange(int state) {
        switch (state) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) {
                    initMediaPlayer();
                }
                else if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }
    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
        Log.d(tag, "onCompletion");

        if (!isInitAudioError) {
            skipToNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(tag, "initMediaPlayer ERROR " + what + " - " + mSongs.get(SongsManager.getInstance().getCurrentMusic()).getTitle());
        isInitAudioError = true;
        //Invoked when there has been an error during an asynchronous operation.
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {

        return false;

    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        /*
         * Getting to saved location of song if playback state is none i.e. first instance of music playback
         * , and if we are playing same track we were playing before, if track is new then we won't seek to last
         * remembered location
         * We reset this location to zero when we start playing a new song
         */

        if (newPosition != -1){
            resumeMedia();

        }else {
            playMedia();
        }
        setupHandler();

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    /**
    * Broadcaset
    */

    private BroadcastReceiver brCloseNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            stopMedia();
            stopForeground(false);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);

            System.exit(0);

        }
    };

    private BroadcastReceiver brStopMusic =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
                SongsManager.getInstance().setCurrentMusic(0);
            }

        }
    };

    private PendingIntent dismissNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.BROADCAST_STOP_AUDIO);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, 0);

    }

    private PendingIntent closeNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.CLOSE_NOTIFICATION);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

   /* private BroadcastReceiver brPlayPause = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra(Constants.NOTIFICATION.IS_PLAYING_STATUS_NOTI, true);

            Log.d(tag,"Service --- onReceive brPlayPause : "+ isPlaying);
            setStatusNoti(isPlaying);
        }
    };*/

    private BroadcastReceiver brPlayNewVideo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // A PLAY_NEW_AUDIO action received
            // reset mediaPlayer to play the new Audio
            if (position == -1 &&  position < mSongs.size()){
                endOfAudioList = false;
                SongsManager.getInstance().setCurrentMusic(0);
            }else {
                stopSelf();
            }
            stopMedia();
            mMediaPlayer.reset();
            initMediaPlayer();

//            initNotification(Constants.NOTIFICATION.PLAY);
        }
    };

    private BroadcastReceiver brResetMusic = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // reset mediaPlayer to play the new Audio
            mMediaPlayer.reset();
//            initMediaPlayer();
            updateMetaData(mSongs.get(SongsManager.getInstance().getCurrentMusic()));
//            buildNotification(Constants.NOTIFICATION.PLAY);
        }
    };



    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            pauseMedia();
//            initNotification(Constants.ACTION.PAUSE);
           /* buildNotification(Constants.NOTIFICATION.PAUSE,
                    mSongs.get(SongsManager.getInstance().getCurrentMusic()));*/
        }
    };

    private BroadcastReceiver brSeekBar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

           /* int seekPos = intent.getIntExtra("seekbar_service", 0);

            if (seekPos != 0) {
                newPosition = seekPos;

                if (mMediaPlayer.isPlaying()) {
                    handler.removeCallbacks(sendUpdatesToUI);
                    mMediaTransportControls.seekTo(seekPos);
                    setupHandler();
                }else {
                    resumePosition = seekPos;
                    mMediaTransportControls.seekTo(newPosition);
                }
            }*/
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }


    void addVoteToTrack(String path) {
        path = path.trim();
        try {
            CategorySongs mCategorySongs = CategorySongs.getInstance();
            mCategorySongs.newRenderDB(this);
            mCategorySongs.open();
            if (mCategorySongs.checkRow(path)) {
                mCategorySongs.updateRow(path);
            } else {
                mCategorySongs.addRow(1, mSongs.get(SongsManager.getInstance().getCurrentMusic()));
            }
            Log.d(tag, mCategorySongs.checkRow(path) +"" );
            mCategorySongs.close();
        } catch (Exception e) {

            /*Log.d(tag, "addVoteToTrack crashed.");
            Log.d(tag,"==============");*/
        }
    }

}
