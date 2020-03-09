package com.droidheat.musicplayer.services;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.utils.SharedPrefsUtils;
import com.droidheat.musicplayer.utils.SongsUtils;

import java.util.List;
import java.util.Objects;

public class MusicPlayback extends MediaBrowserServiceCompat implements
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener{

    // Available PlayBackStates
    //  STATE_NONE = 0;
    //  STATE_STOPPED = 1;
    //  STATE_PAUSED = 2;
    //  STATE_PLAYING = 3;


    /******* ---------------------------------------------------------------
     Private
     ----------------------------------------------------------------*******/

    private final String TAG = "PlaybackServiceConsole";

    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;
    private SharedPrefsUtils sharedPrefsUtils;
    private SongsUtils songsUtils;

    private MediaPlayer mMediaPlayer2;
    private int currentMediaPlayer = 0; // 0 - mMediaPlayer; 1 - mMediaPlayer2
    private int crossfadeDuration = 3000; // 3 seconds
    private Handler mHandler;

    private final int NOTIFICATION_ID = 34213134;

    /******* ---------------------------------------------------------------
     Service Methods and Intents
     ----------------------------------------------------------------*******/
    public MusicPlayback(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefsUtils = new SharedPrefsUtils(this);
        songsUtils = SongsUtils.getInstance();
        songsUtils.setContext(this);
        mHandler = new Handler();

         /*
         Initialize
         */
        checkErrorInPrefs();
        initMediaPlayer();
        initMediaSession();
        initNoisyReceiver();

        /*
         * Calling startForeground() under 5 seconds to avoid ANR
         */
        initNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            /*
             * Analyze and Acting on the intent received by Service
             * Every request to Service should be with one of the intents in our switch
             */
            MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);

            Log.d(TAG, "Intent received.");
            switch (Objects.requireNonNull(intent.getAction())){
                case Constants.ACTION.ACTION_PLAY:
                    resetMediaPlayerPosition();
                    processPlayRequest();
                    break;

                case Constants.ACTION.ACTION_PLAY_PAUSE:
                    resetMediaPlayerPosition();
                    processPlayPause();
                    break;
                case Constants.ACTION.ACTION_TRACK_PREV:
                    processPrevRequest();
                    break;
                case Constants.ACTION.ACTION_TRACK_NEXT:
                    processNextRequest();
                    break;
                case Constants.ACTION.ACTION_REPEAT:
                    musicWidgetsReset();
                    break;
                case Constants.ACTION.ACTION_CLOSE:
                    if (!sharedPrefsUtils.readSharedPrefsBoolean("persistentNotificationPref", false)) {
                        processCloseRequest();
                    } else {
                        processPauseRequest();
                    }
                    break;
                case Constants.ACTION.ACTION_PERSISTENT_NOTIFICATION:
                    if (mPlaybackStateBuilder.build().getState() != PlaybackStateCompat.STATE_PLAYING) {
                        showPausedNotification();
                    }
                    break;
                default: {
                    initNotification();
                }
            }
        }
        return START_STICKY;
    }
    /*
     * Getting and Switching MediaPlayers for Cross-fade
     */
    private MediaPlayer getCurrentMediaPlayer() {
        return (currentMediaPlayer == 0) ? mMediaPlayer : mMediaPlayer2;
    }

    private void checkErrorInPrefs() {
        if (songsUtils.getCurrentMusicID() > songsUtils.queue().size() - 1) {
            songsUtils.setCurrentMusicID(0);
        }
    }

    private void switchMediaPlayer() {
        if (currentMediaPlayer == 0) {
            currentMediaPlayer++; // output 1
        } else if (currentMediaPlayer == 1) {
            currentMediaPlayer--; // output 0
        } else {
            currentMediaPlayer = 0;
        }
    }

    private void crossfade() {
        mHandler.post(detectCrossFadeRunnable);
    }

    private void cancelCrossfade() {
        mHandler.removeCallbacks(detectCrossFadeRunnable);
    }

    // Detects and starts cross-fading
    public Runnable detectCrossFadeRunnable = new Runnable() {
        @Override
        public void run() {
            //Check if we're in the last part of the current song.
            try {
                if (getCurrentMediaPlayer().isPlaying()) {
                    if (getCurrentMediaPlayer().getCurrentPosition() >=
                            (getCurrentMediaPlayer().getDuration() - crossfadeDuration)) {
                        // Start cross-fading the songs
                        switchMediaPlayer();
                        mHandler.postDelayed(crossFadeRunnable, 100);
                    } else {
                        mHandler.postDelayed(detectCrossFadeRunnable, 1000);
                    }
                } else {
                    mHandler.postDelayed(detectCrossFadeRunnable, 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    // Volume
    private float mFadeOutVolume = 1.0f;
    private float mFadeInVolume = 0.0f;

    public Runnable crossFadeRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                int crossFadeStep = 10000; // 10 seconds before finishing and into next song

                if (currentMediaPlayer == 0) {
                    // mMediaPlayer

                    mMediaPlayer.setVolume(mFadeInVolume, mFadeInVolume);
                    mMediaPlayer2.setVolume(mFadeOutVolume, mFadeOutVolume);

                    // Start next track and run Crossfade
                    mMediaPlayer.seekTo(crossFadeStep);
                    mMediaPlayer.start();
                } else {
                    // mMediaPlayer2

                    mMediaPlayer2.setVolume(mFadeInVolume, mFadeInVolume);
                    mMediaPlayer.setVolume(mFadeOutVolume, mFadeOutVolume);

                    // Start next track and run Crossfade
                    mMediaPlayer2.seekTo(crossFadeStep);
                    mMediaPlayer2.start();
                }
                mFadeInVolume = mFadeInVolume + (1.0f / (((float) crossfadeDuration / 1000) * 10.0f));
                mFadeOutVolume = mFadeOutVolume - (1.0f / (((float) crossfadeDuration / 1000) * 10.0f));

                mHandler.postDelayed(crossFadeRunnable, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*
     * saveData() writes current song parameters to sharedPrefs which can be retrieved in
     * other activities or fragments as well as when we start app next time
     * musicID: is id of current item in queue
     * title, artist, album, albumid: are all fields of SongModel()
     *
     */

    private void saveData(){
        int musicID = songsUtils.getCurrentMusicID();
        sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.AUDIO_SESSION_ID, getCurrentMediaPlayer().getAudioSessionId());
        try {
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.MUSIC_ID , musicID);
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.TITLE , songsUtils.queue().get(musicID).getTitle());
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.ARTIST , songsUtils.queue().get(musicID).getArtist());
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.ALBUM , songsUtils.queue().get(musicID).getAlbum());
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.ALBUMID , songsUtils.queue().get(musicID).getAlbumID());
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.RAW_PATH , songsUtils.queue().get(musicID).getPath());
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.DURATION , songsUtils.queue().get(musicID).getDuration());
            sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.DURATION_IN_MS , getCurrentMediaPlayer().getDuration());
        }catch (Exception e ){
            Log.d(TAG, "Unable to save song info in persistent storage. MusicID " + musicID);
        }
    }

    private void doPushPlay(int id) {
        songsUtils.setCurrentMusicID(id);
        getCurrentMediaPlayer().reset();
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }
        setMediaPlayer(songsUtils.queue().get(id).getPath());
    }


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
