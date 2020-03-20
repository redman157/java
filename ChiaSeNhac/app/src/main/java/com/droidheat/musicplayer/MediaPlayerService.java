package com.droidheat.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;


import androidx.annotation.Nullable;

import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsUtils;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener{
    // Binder given to clients

    private final IBinder iBinder = new LocalBinder();
    private String tag = "MediaPlayerLog";
    private ArrayList<SongModel> mSongModels;
    //MediaSession
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSession;
    private MediaControllerCompat.TransportControls mTransportControls;
    private SongsUtils mSongsUtils;
    private SharedPrefsManager mSharedPrefsManager;

    //AudioPlayer notification ID
    public static final int NOTIFICATION_ID = 101;

    private Intent iSeekIntent;
    private Intent iButtonIntent;
    private final Handler handler = new Handler();
    private int vMediaPosition;
    private int VMediaMax;
    private boolean isInitAudioError;

    public static boolean isStarted;
    private MediaPlayer mMediaPlayer;
    // check if end of audio list
    public static boolean endOfAudioList;
    private int resumePosition;


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
        mSongsUtils = SongsUtils.getInstance();
        mSongsUtils.setContext(this);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);

        initMediaPlayer();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(tag, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
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

    public void doPushPlay(int id){
        mSongsUtils.setCurrentMusicID(id);
        mMediaPlayer.reset();
        // pause notification và check audio fouce
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }
    }

    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            resumePosition = mMediaPlayer.getCurrentPosition();
        }
    }

    private void stopMedia() {
        if (mMediaPlayer == null) {
            return;
        }
        try{
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            handler.removeCallbacks(sendUpdatesToUI);
        }catch (Exception e){
            Log.d("QQQ", "stopMedia ERROR: " + e.toString());
        }

    }

    private void resumeMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.seekTo(resumePosition);
            mMediaPlayer.start();
        }
    }

    private void skipMedia(){

    }
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
//            LogMediaPosition();
            handler.postDelayed(this, 1000);

        }
    };


    private void setMediaPlayer()

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    /**
    * Broadcaset
    */
    private BroadcastReceiver playNewVideo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            // A PLAY_NEW_AUDIO action received
            // reset mediaPlayer to play the new Audio
            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(GlobalValue.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }
}
