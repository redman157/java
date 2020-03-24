package com.droidheat.musicplayer;

import android.app.Notification;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.droidheat.musicplayer.database.CategorySongs;
import com.droidheat.musicplayer.manager.CommonUtils;
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
    private ArrayList<SongModel> mSongMusics;
    //MediaSession
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSessionCompat;
    private MediaControllerCompat.TransportControls mMediaTransportControls;
    private SongsManager mSongsManager;
    private SharedPrefsManager mSharedPrefsManager;
    private AudioManager mAudioManager;
    //AudioPlayer notification ID
    public static final int NOTIFICATION_ID = 101;

    private final Handler handler = new Handler();
    private int vMediaPosition;
    private int VMediaMax;
    private boolean isInitAudioError;

    public static boolean isStarted;
    public static MediaPlayer mMediaPlayer;
    // check if end of audio list
    private boolean endOfAudioList;
    private int resumePosition;
    private int getCurrent;
    private Intent iIntentSeekBar;
    private Intent iIntentPlayPause;
    private String path;
    private String actionValue;
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


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "Start service" );
        iIntentPlayPause = new Intent(Constants.ACTION.BROADCAST_BUTTON);
        // ---Set up intent for seek bar broadcast ---
        iIntentSeekBar = new Intent(Constants.ACTION.BROADCAST_SEEK_BAR);

        mSongsManager = SongsManager.getInstance();
        mSongsManager.setContext(this);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        mSongMusics = mSongsManager.queue();


        // khởi tạo media và chờ start
        // cần tao 1 broad cast để play
//        initMediaPlayer();
//        initMediaSession();
        
        register_NoisyReceiver();
        register_PlayNewMusic();
        register_ResetMusic();
        register_StopMusic();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        registerReceiver(brButtonPlay, new IntentFilter(Constants.ACTION.BROADCAST_BUTTON));
        registerReceiver(brSeekBar, new IntentFilter(Constants.ACTION.BROADCAST_SEEK_BAR));


        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
            Log.d(tag, "requestAudioFocus: ENTER");
        }

        if (mMediaSessionManager == null){
            Log.d("BBB", "mMediaSessionManager: Enter");
            initMediaSession();
            initMediaPlayer();
            buildNotification(Constants.NOTIFICATION.PLAY);
        }



        Log.d(tag, "onStartCommand handleIncomingActions: "+ intent.getAction());
        if (intent.getAction() != null) {

            handleIncomingActions(intent);
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAudioManager != null) {
            removeAudioFocus();
        }
        removeNotification();
        unregisterReceiver(brSeekBar);
        unregisterReceiver(brResetMusic);
        unregisterReceiver(brPlayNewVideo);
        unregisterReceiver(brStopMusic);
        if (mSongMusics != null){
            unregisterReceiver(brButtonPlay);
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

        updateActionUI();

        // Attach Callback to receive MediaSession updates
        mMediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
//                playMedia();
//                buildNotification(Constants.NOTIFICATION.PLAY);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
//                buildNotification(Constants.NOTIFICATION.PAUSE);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateActionUI();
//                buildNotification(Constants.NOTIFICATION.PLAY);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateActionUI();
//                buildNotification(Constants.NOTIFICATION.PLAY);
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
            }
        });
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


        /*mMediaPlayer.reset();
        mMediaPlayer.stop();
        Log.d(tag, "initMediaPlayer: Enter");
        try {
            Log.d("BBB", "playMedia path: " + mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getPath());
            mMediaPlayer.setDataSource(mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getPath());

//                mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(tag, "playMedia: "+e.getMessage());
        }
        mMediaPlayer.prepareAsync();*/
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
        int musicID = mSongsManager.getCurrentMusicID();
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

    /*public void doPushPlay(int id){
        mSongsManager.setCurrentMusicID(id);
        mMediaPlayer.reset();
        // pause notification và check audio fouce
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }
    }*/
// Update seek position from Activity
    public void updateSeekPos(Intent intent) {
        int seekPos = intent.getIntExtra("seekpos", 0);
        resumePosition = seekPos;
        mMediaPlayer.seekTo(seekPos);
        if (mMediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
//            mediaPlayer.seekTo(seekPos);
            setupHandler();
        }

    }

    private void LogMediaPosition(){
        try{

            int mediaPosition = mMediaPlayer.getCurrentPosition();

            int mediaMax = mMediaPlayer.getDuration();
            iIntentSeekBar.putExtra("current_pos", mediaPosition);
            iIntentSeekBar.putExtra("media_max", mediaMax);
            iIntentSeekBar.putExtra("song_title", mSongMusics.get(getCurrent).getTitle());
            sendBroadcast(iIntentSeekBar);

        }catch (Exception e){
            Log.e(tag, "ERROR: " + e.toString());

        }
    }


    private void playMedia(String path){
        if (!mMediaPlayer.isPlaying()) {
            setMediaPlayer(path);
            isInitAudioError = false;
//            sendBroadcast(new Intent(Constants.NOTIFICATION.));
        }
    }
    private void playMedia(){

        if (!mMediaPlayer.isPlaying()) {

            mMediaPlayer.start();

            isInitAudioError = false;
//            sendBroadcast(new Intent(Constants.NOTIFICATION.IS_PLAYING));

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
            } catch (IOException ignored) {
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
//            sendBroadcast(new Intent(Constants.NOTIFICATION.PAUSE));

        }
    }

    private void stopMedia() {
        if (mMediaPlayer == null) {
            return;
        }
        try{
            if (mMediaPlayer.isPlaying()) {
                Log.d(tag, "stopMedia: Enter");
                SongsManager.getInstance().setCurrentMusicID(SongsManager.getInstance().getCurrentMusicID());
                mMediaPlayer.stop();
            }
            handler.removeCallbacks(sendUpdatesToUI);
        }catch (Exception e){
            Log.d(tag, "stopMedia ERROR: " + e.getMessage());
        }

    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(resumePosition);
            mMediaPlayer.start();
            Log.d(tag, "resumeMedia: Enter");
        }
    }

    private void skipToPrevious(){
        Log.d(tag, "skipToPrevious: Enter");
        if (SongsManager.getInstance().getCurrentMusicID() == 0){
            if (mSongMusics != null) {
                SongsManager.getInstance().setCurrentMusicID(mSongMusics.size());
            }else {
                Log.d(tag, "danh sach nhạc = null");
            }

        }else {
            SongsManager.getInstance().setCurrentMusicID(
                    SongsManager.getInstance().getCurrentMusicID() -1);
        }
        Log.d(tag, "skipToPrevious");
        stopMedia();
        //reset mediaPlayer
        mMediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToNext(){
        Log.d(tag, "skipToNext: Enter");
        if (SongsManager.getInstance().getCurrentMusicID() == mSongMusics.size()){

            stopMedia();
            stopSelf();

            Intent pauseAction = new Intent(this, MediaPlayerService.class);
            pauseAction.setAction(Constants.ACTION.PAUSE);
            handleIncomingActions(pauseAction);

            endOfAudioList = true;
        }else {
            SongsManager.getInstance().setCurrentMusicID(SongsManager.getInstance().getCurrentMusicID() + 1);
            stopMedia();

            mMediaPlayer.reset();
            initMediaPlayer();
            updateActionUI();
//            buildNotification(Constants.NOTIFICATION.PLAY);

        }

    }
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
//            LogMediaPosition();
            handler.postDelayed(this, 1000);

        }
    };
    // ---Send seek bar info to activity----
    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1);
    }

    private void buildNotification(String action) {
        Log.d(tag, "buildNotification: "+action);
/*
         * Notification actions -> ()
         *  0 -> PlayplaybackAction
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track*/

        int icAction = android.R.drawable.ic_media_pause;
        PendingIntent pAction = null;

        if (action.equalsIgnoreCase(Constants.NOTIFICATION.PLAY)) {
            icAction = android.R.drawable.ic_media_pause;

            //create the play action
            pAction = playbackAction(Constants.REQUEST_CODE.PLAY);

        } else if (action.equalsIgnoreCase(Constants.NOTIFICATION.PAUSE)) {
            icAction = android.R.drawable.ic_media_play;
            //create the play action
            pAction = playbackAction(Constants.REQUEST_CODE.PAUSE);
        }

        iIntentPlayPause.putExtra(Constants.NOTIFICATION.IS_PLAYING_STATUS,
                action.equals(Constants.NOTIFICATION.PLAY));
        sendBroadcast(iIntentPlayPause);

        // create build notification
        androidx.core.app.NotificationCompat.Builder notificationBuilder = null;

//        Bitmap largeIcon = mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getBitmap();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setChannelId(getString(R.string.default_notification_channel_id))
                    .setAutoCancel(true)
                    .setShowWhen(false)
                    // set style notification
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            // Attach our MediaSession token
                            .setMediaSession(mMediaSessionCompat.getSessionToken())
                            // Show our playback controls in the compact notification view.
                            .setShowActionsInCompactView(0, 1, 2))
                    // Set the Notification color
                    .setColor(getResources().getColor(R.color.blackGrey))
                    // Set the large and small icons
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                    // Set Notification content information
                    .setContentTitle((CharSequence) mSongMusics
                            .get(SongsManager.getInstance().getCurrentMusicID())
                            .getTitle())
                    .setContentText(mSongMusics
                            .get(SongsManager.getInstance().getCurrentMusicID()).getArtist())
                    // Add playback actions
                    .addAction(icAction, "pause", pAction)
                    .addAction(SongsManager.getInstance().getCurrentMusicID() != 0 ?
                                    R.drawable.ic_previous_gray : R.drawable.ic_previous_black,
                            Constants.NOTIFICATION.PREVIOUS,
                            playbackAction(Constants.REQUEST_CODE.PREVIOUS))

                    .addAction(SongsManager.getInstance().getCurrentMusicID() < mSongMusics.size() ?
                                    R.drawable.ic_next_gray : R.drawable.ic_next_black,
                            Constants.NOTIFICATION.NEXT,
                            playbackAction(Constants.REQUEST_CODE.NEXT));


            notificationBuilder.setDeleteIntent(dismissNotification());
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
        }else {
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setAutoCancel(true)
                    .setShowWhen(false)
                    // set style notification
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            // Attach our MediaSession token
                            .setMediaSession(mMediaSessionCompat.getSessionToken())
                            // Show our playback controls in the compact notification view.
                            .setShowActionsInCompactView(0, 1, 2))
                    // Set the Notification color
                    .setColor(getResources().getColor(R.color.blackGrey))
                    // Set the large and small icons
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                    // Set Notification content information
                    .setContentTitle(mSongMusics
                            .get(SongsManager.getInstance().getCurrentMusicID())
                            .getTitle())
                    .setContentText(mSongMusics
                            .get(SongsManager.getInstance().getCurrentMusicID()).getArtist())
                    // Add playback actions
                    .addAction(icAction, "pause", pAction)
                    .addAction(SongsManager.getInstance().getCurrentMusicID() != 0 ?
                                    R.drawable.ic_previous_gray : R.drawable.ic_previous_black,
                            Constants.NOTIFICATION.PREVIOUS,
                            playbackAction(Constants.REQUEST_CODE.PREVIOUS))
                    .addAction(SongsManager.getInstance().getCurrentMusicID() < mSongMusics.size()  ?
                                    R.drawable.ic_next_gray : R.drawable.ic_next_black,
                            Constants.NOTIFICATION.NEXT,
                            playbackAction(Constants.REQUEST_CODE.NEXT));


        }

        notificationBuilder.setDeleteIntent(dismissNotification());
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    // set trạng thái action của notification -> service
    public void setStatusNoti(boolean isPlaying){
        Log.d(tag,"setStatusNoti: "+isPlaying );
        Intent iAction = new Intent(this, MediaPlayerService.class);
        iAction.setAction(isPlaying ?
                Constants.ACTION.PLAY : Constants.ACTION.PAUSE);

        Log.d(tag, "iAction: "+iAction.getAction());
        handleIncomingActions(iAction);
        updateActionUI();

        if (isPlaying){
            Log.d(tag,"setStatusNoti: playmedia" );
            resumeMedia();

//            playMedia();

//            buildNotification(Constants.NOTIFICATION.PLAY);
        }else {
            Log.d(tag,"setStatusNoti: pauseMedia" );
            pauseMedia();

//            buildNotification(Constants.NOTIFICATION.PAUSE);
        }
    }

    private void updateActionUI(){
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_music_note_black_24dp);
        // Update the current metadata
        if (SongsManager.getInstance().getCurrentMusicID() != -1){
            mMediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getTitle())
                    .build());
        }
    }
    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        stopMedia();
    }

    private PendingIntent playbackAction(int action_request){
        Intent iAction = new Intent(this, MediaPlayerService.class);
        Log.d("BBB", "playbackAction: "+action_request);
        switch (action_request){
            case Constants.REQUEST_CODE.PLAY:
                // Play
                iAction.setAction(Constants.ACTION.PLAY);
                return PendingIntent.getService(this, action_request, iAction, 0);

            case Constants.REQUEST_CODE.PAUSE:
                iAction.setAction(Constants.ACTION.PAUSE);
                return PendingIntent.getService(this, action_request, iAction, 0);

            case Constants.REQUEST_CODE.NEXT:
                if (SongsManager.getInstance().getCurrentMusicID() < mSongMusics.size()) {
                    iAction.setAction(Constants.ACTION.NEXT);
                    return PendingIntent.getService(this, action_request, iAction, 0);
                }
                break;
            case Constants.REQUEST_CODE.PREVIOUS:
                if (SongsManager.getInstance().getCurrentMusicID() != 0) {
                    iAction.setAction(Constants.ACTION.PREVIOUS);
                    return PendingIntent.getService(this, action_request, iAction, 0);
                }
                break;

                default:
                    break;
        }
        return null;
    }
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
        Log.d(tag, "initMediaPlayer ERROR " + what + " - " + mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getTitle());
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

//        playMedia();

        mediaPlayer.start();
      /*  Intent playbackAction = new Intent(this, MediaPlayerService.class);
        playbackAction.setAction(Constants.ACTION.PLAY);
        handleIncomingActions(playbackAction);*/
        setupHandler();


    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    /**
     * ACTION INTENT
     */
    private void handleIncomingActions(Intent iAction){
        if (iAction == null || iAction.getAction() == null) {
            return;
        }


        String action = iAction.getAction();
        Log.d("BBB", "handleIncomingActions: "+iAction.getAction());
        switch (action){
            case Constants.ACTION.PLAY:
                playMedia(mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()).getPath());
                mMediaTransportControls.play();
//                playMedia();
                break;
            case Constants.ACTION.PAUSE:
                mMediaTransportControls.pause();

                break;
            case Constants.ACTION.NEXT:
                mMediaTransportControls.skipToNext();
                break;
            case Constants.ACTION.PREVIOUS:
                mMediaTransportControls.skipToPrevious();
                break;
            case Constants.ACTION.STOP:
                mMediaTransportControls.stop();
                break;
        }
        /*if (action.equals(Constants.ACTION.PLAY)) {
            if (SongsManager.getInstance().getCurrentMusicID() == mSongMusics.size()) {

                Intent audioIntent = new Intent(Constants.ACTION.BROADCAST_RESET_AUDIO);
                sendBroadcast(audioIntent);
            }
        }*/

    }

    /**
    * Broadcaset
    */


    private BroadcastReceiver brStopMusic =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
                SongsManager.getInstance().setCurrentMusicID(0);
            }
        }
    };

    private void register_StopMusic() {
        IntentFilter filter = new IntentFilter(Constants.ACTION.Broadcast_STOP_AUDIO);
        registerReceiver(brStopMusic, filter);
    }


    private PendingIntent dismissNotification() {


        Intent dismissIntent = new Intent(Constants.ACTION.Broadcast_STOP_AUDIO);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, 0);
    }
    private BroadcastReceiver brButtonPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra(Constants.NOTIFICATION.IS_PLAYING, true);
            setStatusNoti(isPlaying);
        }
    };

    private BroadcastReceiver brPlayNewVideo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            // A PLAY_NEW_AUDIO action received
            // reset mediaPlayer to play the new Audio
            stopMedia();
            mMediaPlayer.reset();
            initMediaPlayer();
            updateActionUI();
//            buildNotification(Constants.NOTIFICATION.PLAY);
        }
    };


    private void register_PlayNewMusic() {
        //Register playNewMedia receiver
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO);
        registerReceiver(brPlayNewVideo, intentFilter);
    }

    private BroadcastReceiver brResetMusic = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // reset mediaPlayer to play the new Audio
            mMediaPlayer.reset();
            initMediaPlayer();
            updateActionUI();
//            buildNotification(Constants.NOTIFICATION.PLAY);
        }
    };

    private void register_ResetMusic() {
        //Register brResetMusic receiver
        IntentFilter filter = new IntentFilter(Constants.ACTION.BROADCAST_RESET_AUDIO);
        registerReceiver(brResetMusic, filter);
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
//            buildNotification(Constants.NOTIFICATION.PAUSE);
        }
    };

    private void register_NoisyReceiver(){
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, filter);
    }

    private BroadcastReceiver brSeekBar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateSeekPos(intent);
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
                mCategorySongs.addRow(1, mSongMusics.get(SongsManager.getInstance().getCurrentMusicID()));
            }
            Log.d(tag, mCategorySongs.checkRow(path) +"" );
            mCategorySongs.close();
        } catch (Exception e) {
            Log.d(tag, "addVoteToTrack crashed.");
        }
    }
}
