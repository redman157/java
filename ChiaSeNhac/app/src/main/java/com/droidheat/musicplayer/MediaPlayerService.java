package com.droidheat.musicplayer;

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
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsUtils;
import com.droidheat.musicplayer.models.SongModel;

import java.io.IOException;
import java.util.ArrayList;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener{
    // Binder given to clients

    private final IBinder iBinder = new LocalBinder();
    private String tag = "MediaPlayerLog";
    private ArrayList<SongModel> mSongMusics;
    //MediaSession
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat.TransportControls mTransportControls;
    private SongsUtils mSongsUtils;
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
    private Intent seekIntent;
    private Intent buttonIntent;

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
        if (mMediaSession != null) {
            mMediaSession.release();
        }
        removeNotification();
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CCC", "Start service" );
        buttonIntent = new Intent(Constants.ACTION.BROADCAST_BUTTON);
        // ---Set up intent for seek bar broadcast ---
        seekIntent = new Intent(Constants.ACTION.BROADCAST_SEEK_BAR);

        mSongsUtils = SongsUtils.getInstance();
        mSongsUtils.setContext(this);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        mSongMusics = mSongsUtils.queue();
//        getCurrent = mSongsUtils.getCurrentMusicID();

        // khởi tạo media và chờ start
        // cần tao 1 broad cast để play
    /*    initMediaPlayer();
        initMediaSession();*/
        registerNoisyReceiver();
        register_playNewAudio();
        register_resetAudio();
        registerStopAudio();
    }

    private BroadcastReceiver stopAudio =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
                SongsUtils.getInstance().setCurrentMusicID(0);
            }
        }
    };

    private void registerStopAudio() {
        IntentFilter filter = new IntentFilter(Constants.ACTION.Broadcast_STOP_AUDIO);
        registerReceiver(stopAudio, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(tag, "onStartCommand");
        registerReceiver(bsButtonPlay, new IntentFilter(Constants.ACTION.BROADCAST_BUTTON));
        registerReceiver(seekBarReceiver, new IntentFilter(Constants.ACTION.BROADCAST_SEEK_BAR));


        if (mMediaSession == null){
            initMediaSession();
            initMediaPlayer();
            Log.d("BBB", "ENTER");
            buildNotification(Constants.ACTION.PLAYING);

        }

        if (!requestAudioFocus()) {
            //Could not gain focus
            stopSelf();
        }
        if (intent != null){
            setIntentActions(intent);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mMediaPlayer != null) {
            stopMedia();
            mMediaPlayer.release();
        }
        if (mAudioManager != null) {
            removeAudioFocus();
        }
        removeNotification();
        unregisterReceiver(seekBarReceiver);
        unregisterReceiver(resetAudio);
        unregisterReceiver(playNewVideo);
        unregisterReceiver(stopAudio);
        if (mSongMusics != null){
            unregisterReceiver(bsButtonPlay);
            unregisterReceiver(becomingNoisyReceiver);
        }
    }
    private void initMediaSession(){
        if (mMediaSessionManager != null) {
            return; //mediaSessionManager exists
        }
        mMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mMediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        mTransportControls = mMediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mMediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        updateActionUI();

        // Attach Callback to receive MediaSession updates
        mMediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia();
                buildNotification(Constants.ACTION.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia();
                buildNotification(Constants.ACTION.PAUSE);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                skipToNext();
                updateActionUI();
                buildNotification(Constants.ACTION.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious();
                updateActionUI();
                buildNotification(Constants.ACTION.PLAYING);
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
    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);

        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mSongMusics.get(SongsUtils.getInstance().getCurrentMusicID()).getPath());

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("BBB","init media player: "+e.getMessage());
        }
        mMediaPlayer.prepareAsync();
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
        int musicID = mSongsUtils.getCurrentMusicID();
        mSharedPrefsManager.setInteger(Constants.PREFERENCES.audio_session_id,
                    mMediaPlayer.getAudioSessionId());
        try {


            mSharedPrefsManager.setInteger(Constants.PREFERENCES.MUSIC_ID ,
                    musicID);

            mSharedPrefsManager.setString(Constants.PREFERENCES.TITLE ,
                    mSongsUtils.queue().get(musicID).getTitle());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ARTIST ,
                    mSongsUtils.queue().get(musicID).getArtist());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ALBUM ,
                    mSongsUtils.queue().get(musicID).getAlbum());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ALBUMID ,
                    mSongsUtils.queue().get(musicID).getAlbumID());

            mSharedPrefsManager.setString(Constants.PREFERENCES.RAW_PATH ,
                    mSongsUtils.queue().get(musicID).getPath());

            mSharedPrefsManager.setString(Constants.PREFERENCES.DURATION ,
                    mSongsUtils.queue().get(musicID).getDuration());

            mSharedPrefsManager.setInteger(Constants.PREFERENCES.DURATION_IN_MS ,
                    mMediaPlayer.getDuration());
        }catch (Exception e ){
            Log.d(tag, "Unable to save song info in persistent storage. MusicID " + musicID);
        }
    }

    /*public void doPushPlay(int id){
        mSongsUtils.setCurrentMusicID(id);
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
            seekIntent.putExtra("current_pos", mediaPosition);
            seekIntent.putExtra("media_max", mediaMax);
            seekIntent.putExtra("song_title", mSongMusics.get(getCurrent).getTitle());
            sendBroadcast(seekIntent);

        }catch (Exception e){
            Log.e("BBB", "ERROR: " + e.toString());

        }
    }
    private void playMedia(){
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            isInitAudioError = false;
            Log.d("BBB", "playMedia");
        }
    }

    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            resumePosition = mMediaPlayer.getCurrentPosition();
            SongsUtils.getInstance().setCurrentMusicID(resumePosition);
            Log.d("BBB", "pauseMedia");
        }
    }

    private void stopMedia() {
        if (mMediaPlayer == null) {
            return;
        }
        try{
            if (mMediaPlayer.isPlaying()) {
                SongsUtils.getInstance().setCurrentMusicID(resumePosition);
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            handler.removeCallbacks(sendUpdatesToUI);
        }catch (Exception e){
            Log.d("BBB", "stopMedia ERROR: " + e.getMessage());
        }
        Log.d("BBB", "stopMedia");
    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(resumePosition);
            mMediaPlayer.start();
            Log.d("BBB", "resumeMedia");
        }
    }

    private void skipToPrevious(){
        if (SongsUtils.getInstance().getCurrentMusicID() == 0){
            if (mSongMusics != null) {
                SongsUtils.getInstance().setCurrentMusicID(mSongMusics.size());
            }else {
                Log.d("BBB", "danh sach nhạc = null");
            }

        }else {
            SongsUtils.getInstance().setCurrentMusicID(
                    SongsUtils.getInstance().getCurrentMusicID() -1);
        }
        Log.d("BBB", "skipToPrevious");
        stopMedia();
        //reset mediaPlayer
        mMediaPlayer.reset();
        initMediaPlayer();
    }

    private void skipToNext(){
        if (SongsUtils.getInstance().getCurrentMusicID() == mSongMusics.size()){
            stopMedia();
            stopMedia();
            Intent playbackAction = new Intent(this, MediaPlayerService.class);
            playbackAction.setAction(Constants.ACTION.PAUSE);
            setIntentActions(playbackAction);

            endOfAudioList = true;
        }else {
            SongsUtils.getInstance().setCurrentMusicID(SongsUtils.getInstance().getCurrentMusicID() + 1);
            stopMedia();

            mMediaPlayer.reset();
            initMediaPlayer();
            updateActionUI();
            buildNotification(Constants.ACTION.PLAYING);

        }
        Log.d("BBB", "skipToNext");
    }
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            LogMediaPosition();
            handler.postDelayed(this, 1000);

        }
    };
    // ---Send seek bar info to activity----
    private void setupHandler() {
//        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1);
    }

    private void buildNotification(String typeAction) {
        Log.d("BBB", "buildNotification: "+typeAction);
        /*
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         */
        int notificationAction = android.R.drawable.ic_media_pause;
        PendingIntent action = null;
        if (typeAction.equals(Constants.MUSIC.PLAY)) {
            notificationAction = android.R.drawable.ic_media_play;
            action = playbackAction(Constants.REQUEST_CODE.PLAY);
        } else if (typeAction.equals(Constants.MUSIC.PAUSE)) {
            notificationAction = android.R.drawable.ic_media_pause;
            action = playbackAction(Constants.REQUEST_CODE.PAUSE);
        }

        buttonIntent.putExtra(Constants.VALUE.IS_PLAYING_STATUS,typeAction == Constants.ACTION.PLAYING );
        sendBroadcast(buttonIntent);

        // create build notification
        androidx.core.app.NotificationCompat.Builder notificationBuilder = null;

//        Bitmap largeIcon = mSongMusics.get(SongsUtils.getInstance().getCurrentMusicID()).getBitmap();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setChannelId(getString(R.string.default_notification_channel_id))
                    .setAutoCancel(true)
                    .setShowWhen(false)
                    // set style notification
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            // Attach our MediaSession token
                            .setMediaSession(mMediaSession.getSessionToken())
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
                            .get(SongsUtils.getInstance().getCurrentMusicID())
                            .getTitle())
                    .setContentText(mSongMusics
                            .get(SongsUtils.getInstance().getCurrentMusicID()).getArtist())
                    // Add playback actions
                    .addAction(notificationAction, "pause", action)
                    .addAction(SongsUtils.getInstance().getCurrentMusicID() != 0 ?
                                    R.drawable.ic_previous_gray : R.drawable.ic_previous_black,
                            Constants.MUSIC.PREVIOUS,
                            playbackAction(Constants.REQUEST_CODE.PREVIOUS))
                    .addAction(SongsUtils.getInstance().getCurrentMusicID() < mSongMusics.size() ?
                                    R.drawable.ic_next_gray : R.drawable.ic_next_black,
                            Constants.MUSIC.NEXT,
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
                            .setMediaSession(mMediaSession.getSessionToken())
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
                            .get(SongsUtils.getInstance().getCurrentMusicID())
                            .getTitle())
                    .setContentText(mSongMusics
                            .get(SongsUtils.getInstance().getCurrentMusicID()).getArtist())
                    // Add playback actions
                    .addAction(notificationAction, "pause", action)
                    .addAction(SongsUtils.getInstance().getCurrentMusicID() != 0 ?
                                    R.drawable.ic_previous_gray : R.drawable.ic_previous_black,
                            Constants.MUSIC.PREVIOUS,
                            playbackAction(Constants.REQUEST_CODE.PREVIOUS))
                    .addAction(SongsUtils.getInstance().getCurrentMusicID() < mSongMusics.size()  ?
                                    R.drawable.ic_next_gray : R.drawable.ic_next_black,
                            Constants.MUSIC.NEXT,
                            playbackAction(Constants.REQUEST_CODE.NEXT));


            notificationBuilder.setDeleteIntent(dismissNotification());
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    // set trạng thái action của notification -> service
    public void setStatusNoti(boolean isPlaying){
        Log.d("BBB","setStatusNoti: "+isPlaying );
        Intent iAction = new Intent(this, MediaPlayerService.class);
        iAction.setAction(isPlaying ? Constants.ACTION.PLAYING : Constants.ACTION.PAUSE);
        setIntentActions(iAction);
        updateActionUI();

        if (isPlaying){
            Log.d("BBB","setStatusNoti: resumeMedia" );
            resumeMedia();
            buildNotification(Constants.ACTION.PLAYING);
        }else {
            Log.d("BBB","setStatusNoti: pauseMedia" );
            pauseMedia();
            buildNotification(Constants.ACTION.PAUSE);
        }
    }

    private void updateActionUI(){
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_music_note_black_24dp);
        // Update the current metadata
        if (SongsUtils.getInstance().getCurrentMusicID() != -1){
            mMediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mSongMusics.get(SongsUtils.getInstance().getCurrentMusicID()).getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mSongMusics.get(SongsUtils.getInstance().getCurrentMusicID()).getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, mSongMusics.get(SongsUtils.getInstance().getCurrentMusicID()).getTitle())
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
        switch (action_request){
            case Constants.REQUEST_CODE.NEXT:
                if (SongsUtils.getInstance().getCurrentMusicID() < mSongMusics.size() -1) {
                    iAction.setAction(Constants.ACTION.NEXT);
                    return PendingIntent.getService(this, action_request, iAction, 0);
                }
                break;
            case Constants.REQUEST_CODE.PREVIOUS:
                if (SongsUtils.getInstance().getCurrentMusicID() != 0) {
                    iAction.setAction(Constants.ACTION.PREVIOUS);
                    return PendingIntent.getService(this, action_request, iAction, 0);
                }
                break;
            case Constants.REQUEST_CODE.PLAY:
                // Play
                iAction.setAction(Constants.ACTION.PLAYING);
                return PendingIntent.getService(this, action_request, iAction, 0);

            case Constants.REQUEST_CODE.PAUSE:
                iAction.setAction(Constants.ACTION.PAUSE);
                return PendingIntent.getService(this, action_request, iAction, 0);
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
        Log.d("BBB", "onCompletion");

        if (!isInitAudioError) {
            skipToNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("BBB", "initMediaPlayer ERROR " + what + " - " + mSongMusics.get(SongsUtils.getInstance().getCurrentMusicID()).getTitle());
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
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
        setupHandler();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    /**
     * ACTION INTENT
     */
    private void setIntentActions(Intent iAction){
        if (iAction == null || iAction.getAction() == null) return;
        String action = iAction.getAction();
        switch (action){
            case Constants.ACTION.PLAYING:
                mTransportControls.play();
                break;
            case Constants.ACTION.PAUSE:
                mTransportControls.pause();
                break;
            case Constants.ACTION.NEXT:
                mTransportControls.skipToNext();
                break;
            case Constants.ACTION.PREVIOUS:
                mTransportControls.skipToPrevious();
                break;
            case Constants.ACTION.STOP:
                mTransportControls.stop();
                break;
        }
        if (action.equals(Constants.ACTION.PLAYING)) {
            if (SongsUtils.getInstance().getCurrentMusicID() == mSongMusics.size()) {

                Intent audioIntent = new Intent(Constants.ACTION.BROADCAST_RESET_AUDIO);
                sendBroadcast(audioIntent);
            }
        }

    }

    /**
    * Broadcaset
    */
    private PendingIntent dismissNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.BROADCAST_BUTTON);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, 0);
    }
    private BroadcastReceiver bsButtonPlay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isPlaying = intent.getBooleanExtra(Constants.MUSIC.isPlaying, true);
            setStatusNoti(isPlaying);
        }
    };

    private BroadcastReceiver playNewVideo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            // A PLAY_NEW_AUDIO action received
            // reset mediaPlayer to play the new Audio
            stopMedia();
            mMediaPlayer.reset();
            initMediaPlayer();
            updateActionUI();
            buildNotification(Constants.ACTION.PLAYING);
        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO);
        registerReceiver(playNewVideo, filter);
    }

    private BroadcastReceiver resetAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // reset mediaPlayer to play the new Audio
            mMediaPlayer.reset();
            initMediaPlayer();
            updateActionUI();
            buildNotification(Constants.ACTION.PLAYING);
        }
    };

    private void register_resetAudio() {
        //Register resetAudio receiver
        IntentFilter filter = new IntentFilter(Constants.ACTION.BROADCAST_RESET_AUDIO);
        registerReceiver(resetAudio, filter);
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            buildNotification(Constants.ACTION.PAUSE);
        }
    };

    private void registerNoisyReceiver(){
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, filter);
    }

    private BroadcastReceiver seekBarReceiver = new BroadcastReceiver() {
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
}
