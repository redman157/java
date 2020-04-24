package com.android.music_player.services;

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
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
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

import com.android.music_player.R;
import com.android.music_player.Status;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.android.music_player.Status.COMPLETE;
import static com.android.music_player.Status.NEXT;
import static com.android.music_player.Status.PAUSED;
import static com.android.music_player.Status.PAUSING;
import static com.android.music_player.Status.PLAYED;
import static com.android.music_player.Status.PLAYING;
import static com.android.music_player.Status.PREVIOUS;
import static com.android.music_player.Status.STOPPED;
import static com.android.music_player.Status.STOPPING;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener{
    // Binder given to clients


    private final IBinder iBinder = new LocalBinder();
    private String tag = "BBB";
    private Status status = PAUSED;
    //MediaSession
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSessionCompat;
    private MediaControllerCompat.TransportControls mMediaTransportControls;
    private SongManager mSongManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private AudioManager mAudioManager;
    //AudioPlayer notification ID

    private final Handler handler = new Handler();
    private boolean isInitAudioError;
    /**
     * Static*/
    public static final int NOTIFICATION_ID = 101;
    public static boolean isStarted;
    public static MediaPlayer mMediaPlayer;
    public static ArrayList<SongModel> mSongs, mSongShuffle;
    // check if end of audio list
    private boolean endOfAudioList;

    private boolean isPlayActivity;
    private int resumePosition;
    private int position;

    private Intent iIntentSeekBar, iPlayNewMusic, iCheckPlayActivity, iPlayPauseActivity;
    private Intent iPrevToActivity, iNextToActivity;
    private androidx.core.app.NotificationCompat.Builder notificationBuilder = null;
    private boolean isRepeat, isShuffle;
    private String type;

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

        mSongManager = SongManager.getInstance();
        mSongManager.setContext(this);
        mSharedPrefsUtils = new SharedPrefsUtils(this);

        // khởi tạo media và chờ start
        // cần tao 1 broad cast để play
        initMediaPlayer();
        initMediaSession();
        registerReceiver(brResetMusic,  new IntentFilter(Constants.ACTION.BROADCAST_RESET_AUDIO));
        registerReceiver(brStopMusic, new IntentFilter(Constants.ACTION.BROADCAST_STOP_AUDIO));
//        registerReceiver(brPlayPauseActivity, new IntentFilter(Constants.ACTION.BROADCAST_PLAY_PAUSE));
        registerReceiver(brCloseNotification, new IntentFilter(Constants.ACTION.CLOSE_NOTIFICATION));
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isPlayActivity = intent.getBooleanExtra(Constants.INTENT.IS_PLAY_ACTIVITY, true);

        position = SongManager.getInstance().getCurrentMusic();


        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
        }
        if (intent.getAction() != null ) {
            //Handle Intent action from MediaSession.TransportControls
            handleUIActions(intent);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            SongManager.getInstance().setCurrentMusic(position);
            stopMedia();
            mMediaPlayer = null;
        }
        if (mAudioManager != null) {
            removeAudioFocus();
        }
        removeNotification();

        unregisterReceiver(brResetMusic);
        unregisterReceiver(brStopMusic);
        unregisterReceiver(brCloseNotification);
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
                try {
                    super.onPlay();
                    if (!successfullyRetrievedAudioFocus()) {
                        return;
                    }

                    Log.d("BBB", "Service --- onPlay:" + position);
                    playMedia(mSongs.get(position).getPath());
                    initNotification(Constants.NOTIFICATION.PLAY, position);
                } finally {

                }
            }

            @Override
            public void onPause() {
                try {
                    super.onPause();
                    pauseMedia();
                    Log.d("MMM", "Service --- onPause position:  "+ position);
                    initNotification(Constants.NOTIFICATION.PAUSE, position);
                } finally {
                    status = Status.PAUSED;
                    // báo len UI là pause
                }
            }

            @Override
            public void onSkipToNext() {
                try {
                    super.onSkipToNext();
                    Log.d("BBB", "Service --- onSkipToNext: "+isPlayActivity );
                    if (isPlayActivity) {
                        if (!isRepeat) {
                            SongManager.getInstance().setCurrentMusic(position + 1);
                            processEndOfList(SongManager.getInstance().getCurrentMusic());
                            resumePosition = -1;
                            skipToNext();
                        }

                        initNotification(Constants.NOTIFICATION.PAUSE,
                                SongManager.getInstance().getCurrentMusic());

                    }else {
                        SongManager.getInstance().setCurrentMusic(position + 1);
                        processEndOfList(SongManager.getInstance().getCurrentMusic());
                        if (mMediaPlayer.isPlaying()) {
                            stopMedia();
                        }

                        initNotification(Constants.NOTIFICATION.PAUSE,
                                SongManager.getInstance().getCurrentMusic());
                    }
                }finally {
                    status = PAUSED;
                }

            }

            @Override
            public void onSkipToPrevious() {
                try {
                    super.onSkipToPrevious();
                    Log.d("BBB", "Service --- onSkipToPrevious: " + isPlayActivity);
                    if (isPlayActivity) {
                        if (!isRepeat) {
                            SongManager.getInstance().setCurrentMusic(position - 1);
                            processEndOfList(SongManager.getInstance().getCurrentMusic());
                            resumePosition = -1;
                            skipToPrevious();
                        }
                        Log.d("BBB", "Service --- onSkipToPrevious: " + SongManager.getInstance().getCurrentMusic());

                        initNotification(Constants.NOTIFICATION.PAUSE,
                                SongManager.getInstance().getCurrentMusic());
                    } else {
                        SongManager.getInstance().setCurrentMusic(position - 1);
                        processEndOfList(SongManager.getInstance().getCurrentMusic());
                        if (mMediaPlayer.isPlaying()) {
                            stopMedia();
                        }

                        initNotification(Constants.NOTIFICATION.PAUSE,
                                SongManager.getInstance().getCurrentMusic());
                    }
                }finally {
                    status = PAUSED;
                }
            }
            @Override
            public void onStop() {
                super.onStop();
                try {
                    status = STOPPING;
                    removeNotification();
                    //Stop the service
                    stopMedia();
                }finally {
                    status = STOPPED;
                }
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
                if (status == PLAYED) {
                    handler.removeCallbacks(sendUpdatesToUI);
                    mMediaPlayer.seekTo((int) position);
                    mMediaPlayer.start();
                }else {

                    mMediaPlayer.seekTo((int) position);
                }
               /* if (mMediaPlayer.isPlaying()) {
                    Log.d("BBB", "Service --- onSeekTo: True");
                    mMediaPlayer.seekTo((int) (long) position);

                }else {
                    Log.d("BBB", "Service --- onSeekTo: False");
                    resumePosition = (int) position;
                }
*/
            }
        });
    }

    /**
     * ACTION INTENT
     */
    private void handleUIActions(Intent iAction){
        if (iAction == null || iAction.getAction() == null) {
            return;
        }
        String action = iAction.getAction();

        if (action != null) {
            switch (action) {
                case Constants.ACTION.CHANGE_SONG:
                    isShuffle = iAction.getBooleanExtra(Constants.INTENT.IS_SHUFFLE, false);
                    if (isShuffle) {
                        mSongShuffle = (ArrayList<SongModel>) iAction.getSerializableExtra(Constants.INTENT.CHANGE_MUSIC);
                    } else {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_MAIN, position);
                        mSongs = (ArrayList<SongModel>) iAction.getSerializableExtra(Constants.INTENT.CHANGE_MUSIC);
                        processEndOfList(position);
                        mSongShuffle = null;
                    }
                    break;
                case Constants.ACTION.PLAY:
                    if (status == STOPPED) {
                        initMediaPlayer();

                    }
                    if (status == PAUSED) {
                        status = PLAYING;
                        mMediaTransportControls.play();
                    } else {
                        Log.d(tag, "Service --- handleUIActions: " + status.toString());
                    }
                    break;
                case Constants.ACTION.PAUSE:
                    if (status == PLAYED) {
                        status = PAUSING;
                        mMediaTransportControls.pause();
                    } else {
                        Log.d(tag, "Service --- handleUIActions: " + status.toString());
                    }
                    break;
                case Constants.ACTION.NEXT:
                    if (status == PLAYED || status == PAUSED) {
                        status = NEXT;
                        mMediaTransportControls.skipToNext();
                        this.iNextToActivity = iAction;
                        Log.d("BBB",
                                "Service --- Action NExt: " + iAction.getStringExtra(Constants.INTENT.NEXT_TO_SERVICE));
                    } else {
                        Log.d(tag, "Service --- handleUIActions: " + status.toString());
                    }
                    break;
                case Constants.ACTION.PREVIOUS:
                    if (status == PLAYED || status == PAUSED) {
                        status = PREVIOUS;
                        mMediaTransportControls.skipToPrevious();
                        this.iPrevToActivity = iAction;
                    } else {
                        Log.d(tag, "Service --- handleUIActions: " + status.toString());
                    }
                    break;
                case Constants.ACTION.STOP:

                    mMediaTransportControls.stop();
                    break;
                case Constants.ACTION.SEEK:

                    int pos = iAction.getIntExtra(Constants.INTENT.POSITION_SONG, 0);
                    if (pos != 0) {
                        mMediaTransportControls.seekTo(pos);
                    }

                    break;
                case Constants.ACTION.IS_PLAY:
                    mSongs = (ArrayList<SongModel>) iAction.getSerializableExtra(Constants.INTENT.CHANGE_MUSIC);

                    if (mSongs != null && mSongs.size() > 0) {

                        if (mMediaPlayer.isPlaying()) {
                            iCheckPlayActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, true);
                            sendBroadcast(iCheckPlayActivity);
                        } else {
                            iCheckPlayActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, false);
                            sendBroadcast(iCheckPlayActivity);
                        }
                    }
                    break;
                case Constants.ACTION.REPEAT:
                    this.isRepeat = iAction.getBooleanExtra(Constants.INTENT.IS_REPEAT, false);
                    break;
            default:
                status = Status.PAUSED;
                initNotification(Constants.NOTIFICATION.PAUSE, position);
                break;
            }
        }
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
     * NAME_PLAYLIST, artist, album, albumid: are all fields of SongModel()
     *
     */

    private void LogMediaPosition(){

        try{
            int mediaPosition = mMediaPlayer.getCurrentPosition();

            int mediaMax = mSongs.get(SongManager.getInstance().getCurrentMusic()).getTime();
           /* Log.d("MMM", "LogMediaPosition : Enter ==== MediaPosition: "+mediaPosition +" ==== " +
                    "MediaMax: "+mediaMax +" ==== Song Title: "+ mSongs.getData(SongManager.getInstance().getCurrentMusic()).getSongName());*/
            iIntentSeekBar.putExtra("current_pos", mediaPosition);
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG, mediaPosition);
            Log.d("BBB",
                    "Service --- LogMediaPosition --- mSharedPrefsUtils: "+ (mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION_SONG, -1)));
            Log.d("BBB",
                    "Service --- LogMediaPosition --- mediaPosition: "+ (mediaPosition));
            iIntentSeekBar.putExtra("media_max", mediaMax);
            iIntentSeekBar.putExtra("song_title",
                    mSongs.get(SongManager.getInstance().getCurrentMusic()).getSongName());
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
        Log.d("ZZZ", "Service --- setMediaPlayer: "+ resumePosition);
        if (mMediaPlayer != null){
            mMediaPlayer.reset();
        }

        File file = new File(path);
        if (file.exists()) {
            try {
                addVoteToTrack(path);

                mMediaPlayer.setDataSource(path);
                try {
                    mMediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(tag, "Error prepare: "+e.getMessage());
                }
            } catch (IOException e) {
                skipToNext();
                e.printStackTrace();
            }

        } else {
            skipToNext();
            Log.d(tag, "Error finding file so we skipped to next.");
            Log.d(tag, "Error finding music file");

        }
    }
    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            resumePosition = mMediaPlayer.getCurrentPosition();
            Log.d(tag, "Service ---- pauseMedia: "+ resumePosition);
            handler.removeCallbacks(sendUpdatesToUI);
            mMediaPlayer.pause();

        }
    }

    private void stopMedia() {
        if (mMediaPlayer != null) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    Log.d(tag, "stopMedia: Enter");
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                handler.removeCallbacks(sendUpdatesToUI);
            } catch (Exception e) {
                Log.d(tag, "stopMedia ERROR: " + e.getMessage());
            }
        }
    }

    private void resumeMedia(int resumePosition) {


    }

    private void skipToPrevious(){
        Log.d(tag, "skipToPrevious: Enter");
        Log.d("BBB", "Service --- skipToPrevious: "+SongManager.getInstance().getCurrentMusic());

        if (iPrevToActivity != null) {
            Bundle bdPrev = iPrevToActivity.getExtras();
            if (bdPrev != null) {
                String prevToActivity = bdPrev.getString(Constants.INTENT.PREVIOUS_TO_SERVICE);

                iPlayNewMusic.putExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY,
                        prevToActivity);
                iPlayNewMusic.putExtra(Constants.INTENT.POSITION,
                        SongManager.getInstance().getCurrentMusic());
                sendBroadcast(iPlayNewMusic);
            }else {
                Log.d("BBB", "Service --- skipToPrevious: bdPrev is null");
            }

        }
        stopMedia();
        //reset mediaPlayer
        mMediaPlayer.reset();
    }

    private void skipToNext(){
        if (iNextToActivity != null) {
            Log.d(tag, "Service --- skipToNext: "+SongManager.getInstance().getCurrentMusic());
            Bundle bdNext = iNextToActivity.getExtras();
            if (bdNext != null) {
                // do intent này intent nhiều lần vào service, nên bundle có thể lúc null có giá trị
                // phụ thuộc vào biến String để xử lý
                // truyền noti, action lên activity
                // tương tự với previous
                String nextToActivity = bdNext.getString(Constants.INTENT.NEXT_TO_SERVICE);
                Log.d("BBB", "Service --- skipToNext: bdNext: "+nextToActivity);
                iPlayNewMusic.putExtra(Constants.INTENT.NOTI_SERVICE_TO_ACTIVITY, nextToActivity);
                iPlayNewMusic.putExtra(Constants.INTENT.POSITION,
                        SongManager.getInstance().getCurrentMusic());
                sendBroadcast(iPlayNewMusic);

            } else {

                Log.d("BBB", "Service --- SkipToNext: dbNext is null");

            }
        }else {
            Log.d(tag, "Service --- skipToNext: else iNextToActivity == null");
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
        if (SongManager.getInstance().getCurrentMusic() != -1){
            mMediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mSongs.get(SongManager.getInstance().getCurrentMusic()).getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mSongs.get(SongManager.getInstance().getCurrentMusic()).getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songModel.getSongName())
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

    private void initNotification(String type, int position) {

        int icon_Action = 0;

        PendingIntent playPauseIntent = null;
        PendingIntent nextIntent;
        PendingIntent prevIntent;

        // GỬI INTENT TỪ NOTI VỀ SERVICE
        Intent iNext = new Intent(this, MediaPlayerService.class);
        Intent iPrev = new Intent(this, MediaPlayerService.class);
        if (!type.equals(Constants.NOTIFICATION.REPEAT)){
            iNext.setAction(Constants.ACTION.NEXT);
            iPrev.setAction(Constants.ACTION.PREVIOUS);
        }else {
            iNext.setAction(null);
            iPrev.setAction(null);
        }
        // tại đây khi tác động noti xuống service -> bundle ở onSkipNext khác null
        Bundle bdPrevious = new Bundle();
        bdPrevious.putString(Constants.INTENT.PREVIOUS_TO_SERVICE, "PreviousToService");
        iPrev.putExtras(bdPrevious);

        Bundle bdNext = new Bundle();
        bdNext.putString(Constants.INTENT.NEXT_TO_SERVICE, "NextToService");
        iNext.putExtras(bdNext);


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

        // intent truyền xuống service
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
                .setAutoCancel(false)
                .setShowWhen(false)

                // Set the Notification color
                .setColorized(true).setColor(getResources().getColor(R.color.white))
//                .setLargeIcon()
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setLargeIcon(bitmap)
                .setSubText(mSongs.get(position).getFileName())

                .setContentTitle(mSongs.get(position).getSongName())
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
                .setAutoCancel(false)
                .setShowWhen(false)

                // Set the Notification color
                .setColorized(true).setColor(getResources().getColor(R.color.white))
//                .setLargeIcon()
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setLargeIcon(bitmap)
                .setSubText(mSongs.get(position).getFileName())

                .setContentTitle(mSongs.get(position).getSongName())
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
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        Log.d(tag,  "MediaPlayer.OnInfoListener: " + what);
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
        /*
         * Setting Equalizer
         */
        try {
            setEqualizer();

            /*
             * Setting metaData
             */
            setGraphics();
            Log.d("BBB", "Service --- onPrepared: " + position);
        /*if (resumePosition != -1){
            resumeMedia(resumePosition);
//            resumePosition = -1;
        }else {


        }*/
            int pos = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION_SONG,0);
            Log.d("BBB", "Service --- onPrepared: "+pos);
            if (pos != 0){
                mediaPlayer.seekTo(pos);
            }
            mediaPlayer.start();
        }finally {
            status = PLAYED;
            setupHandler();
        }

    }
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d("MMM", "Service --- onSeekComplete Enter");
        handler.removeCallbacks(sendUpdatesToUI);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            status = COMPLETE;
            stopMedia();
            Log.d(tag, "onCompletion");
            if (mSongs != null) {
                if (!endOfAudioList && !isRepeat) {
                    if (isShuffle) {
                        mSongs = mSongShuffle;

                    } else {
                        Log.d("BBB", "Service --- onCompletion: Enter");
                        if (!isPlayActivity && position < mSongs.size() - 1) {
                            SongManager.getInstance().setCurrentMusic(position + 1);

                            initNotification(Constants.NOTIFICATION.PAUSE,
                                    SongManager.getInstance().getCurrentMusic());
                        }
                    }
                }
            }
        } finally {
            status = PAUSED;
            // báo lên ui quyết định pause

        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mSongs!= null) {
            Log.d(tag, "initMediaPlayer ERROR " + what + " - Extra" + extra + " - " + mSongs.get(SongManager.getInstance().getCurrentMusic()).getSongName());
        }
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
    /**
    * Broadcaset
    */

    private PendingIntent dismissNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.BROADCAST_STOP_AUDIO);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, 0);

    }

    private PendingIntent closeNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.CLOSE_NOTIFICATION);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private BroadcastReceiver brCloseNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            stopMedia();
            stopForeground(false);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

            System.exit(0);

        }
    };

    private BroadcastReceiver brStopMusic =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
                SongManager.getInstance().setCurrentMusic(0);
            }

        }
    };



  /*  private BroadcastReceiver brCheckPlayService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // activity gửi broadcast xuống service
            boolean isPlayingMedia = intent.getBooleanExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, false);

            if (isPlayingMedia) {

                mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                isPlaying = true;

                Utils.PauseMediaService(PlayActivity.this,true);
            } else {

                mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                isPlaying = false;
                if (continous ){
                    int curr = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION_SONG, 0);
                    if (curr != 0){
                        Utils.ContinueMediaService(PlayActivity.this, true, curr);
                    }
                }else {
                    Utils.PlayMediaService(PlayActivity.this,true);
                }

            }
        }
    };*/

    private BroadcastReceiver brResetMusic = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // reset mediaPlayer to play the new Audio
            mMediaPlayer.reset();
//            initMediaPlayer();
            updateMetaData(mSongs.get(SongManager.getInstance().getCurrentMusic()));
//            buildNotification(Constants.NOTIFICATION.PLAY);
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    private void processEndOfList(int position){
        int size = mSongs.size() ;
        if (position >= size){
            SongManager.getInstance().setCurrentMusic(0);

        }else if (position < 0){
            SongManager.getInstance().setCurrentMusic(size - 1);
        }else {
            SongManager.getInstance().setCurrentMusic(position);
        }

    }

    void addVoteToTrack(String path) {
      /*  path = path.trim();
        try {
            mSongManager.getCategorySongsDB()

            if (mSongManager.getCategorySongsDB().searchCategory(path)) {
                mCategorySongs.updateRow(path);
            } else {
                mCategorySongs.addCategory(1, mSongs.getData(SongManager.getInstance().getCurrentMusic()));
            }
            Log.d(tag, mCategorySongs.checkRow(path) +"" );
            mCategorySongs.closeDatabase();
        } catch (Exception e) {

            *//*Log.d(tag, "addVoteToTrack crashed.");
            Log.d(tag,"==============");*//*
        }*/
    }
    Equalizer eq;
    BassBoost bassBoost;
    Virtualizer virtualizer;
    private void setEqualizer(){
        boolean isEqInSettings =
                mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.turnEqualizer, false);
        int currentEqProfile =
                mSharedPrefsUtils.getInteger(Constants.PREFERENCES.currentEqProfile, 0);
        try {
            eq = new Equalizer(0, mMediaPlayer.getAudioSessionId());
            eq.setEnabled(isEqInSettings);
            for (int i = 0; i < eq.getNumberOfBands(); i++) {
                eq.setBandLevel((short) i, (short) mSharedPrefsUtils.getInteger(
                        "profile" + currentEqProfile + "Band" + i, 0));
            }
            Log.d("CCC", "Equalizer successfully initiated with profile " + currentEqProfile);
        } catch (Exception e) {
            Log.d(tag, "Unable to run Equalizer");

        }
        try {
            bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
            bassBoost.setEnabled(isEqInSettings);
            bassBoost.setStrength((short)  mSharedPrefsUtils.getInteger(Constants.PREFERENCES.bassLevel + currentEqProfile,
                    0));
        } catch (Exception ignored) {}
        try {
            virtualizer = new Virtualizer(0, mMediaPlayer.getAudioSessionId());
            virtualizer.setEnabled(isEqInSettings);
            virtualizer.setStrength((short) mSharedPrefsUtils.getInteger(Constants.PREFERENCES.vzLevel + currentEqProfile, 0));
        } catch (Exception ignored) {}
    }
    private void setGraphics(){
        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.audio_session_id,
                mMediaPlayer.getAudioSessionId());
    }
}
