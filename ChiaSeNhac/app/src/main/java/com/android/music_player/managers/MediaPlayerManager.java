package com.android.music_player.managers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.music_player.activities.HomeActivity;
import com.android.music_player.media.PlaybackInfoListener;
import com.android.music_player.media.PlayerAdapter;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.Utils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MediaPlayerManager extends PlayerAdapter implements MediaPlayer.OnCompletionListener
        , MediaPlayer.OnPreparedListener{
    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mFilename;
    private PlaybackInfoListener mPlaybackInfoListener;
    private MediaMetadataCompat mCurrentMedia;
    private QueueManager mQueueManager;
    private MediaManager mMediaManager;
    private int mState;
    private boolean isRepeat= false;
    private boolean mCurrentMediaPlayedToCompletion;
    private MediaSession.Callback mCallBack;
    private MediaSession.QueueItem mQueueItem;
    // Work-around for a MediaPlayer bug related to the behavior of MediaPlayer.seekTo()
    // while not playing.
    private int mSeekWhileNotPlaying = -1;

    public MediaPlayerManager(@NonNull Context context, PlaybackInfoListener listener) {
        super(context);
        this.mContext = context.getApplicationContext();
        mPlaybackInfoListener = listener;
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(context);
        mQueueManager = QueueManager.getInstance(context);
    }
    /**
     * Once the {@link MediaPlayer} is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the {@link HomeActivity} the {@link MediaPlayer} is
     * released. Then in the onStart() of the {@link HomeActivity} a new {@link MediaPlayer}
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setWakeMode(mContext.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnCompletionListener(this);
        }
    }
    // This is the main reducer for the player state machine.
    private void setNewState(@PlaybackStateCompat.State int newPlayerState){
        mState = newPlayerState;

        Log.d("BBB","MediaPlayerManager --- setNewState: "+newPlayerState );
        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is assignData to true.

        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true;
        }
        // Work around for MediaPlayer.getCurrentPosition() when it changes while not playing.
        final long reportPosition;
        if (mSeekWhileNotPlaying >= 0){

            reportPosition = mSeekWhileNotPlaying;
            Log.d("BBB", "MediaPlayerManager --- mSeekWhileNotPlaying > 0: "+reportPosition );
            if (mState == PlaybackStateCompat.STATE_BUFFERING) {
                mSeekWhileNotPlaying = -1;
            }
        }else {
            reportPosition = (mMediaPlayer == null)?
                    0 : mMediaPlayer.getCurrentPosition();
            Log.d("BBB", "MediaPlayerManager --- mSeekWhileNotPlaying < 0: "+reportPosition);
        }
        Log.d("TTT", "MediaPlayerManager --- setNewState: "+reportPosition);
        updatePlaybackState(mState ,reportPosition);
    }

    private void updatePlaybackState(int state,long reportPosition) {
        if (mPlaybackInfoListener == null){
            return;
        }

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(state, reportPosition, 1.0f, SystemClock.elapsedRealtime());

        if (isPlaying()){
            Bundle bundle = new Bundle();
            bundle.putBoolean("isPlay", true);
            stateBuilder.setExtras(bundle);
        }else {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isPlay", false);
            stateBuilder.setExtras(bundle);
        }

        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());
    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.STATE_BUFFERING|
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE|
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE|
                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT|
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mState){
            // rơi vào state bất kỳ thì sẽ chuyển sang trạng thái tương ứng
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:

                actions |= PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_STOP|
                        PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:

                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    // Implements PlaybackControl.
    @Override
    public void playFromMedia(MediaMetadataCompat metadata) {
        mCurrentMedia = metadata;
        final String mediaId = metadata.getDescription().getMediaId();
        playFile(MusicLibrary.getMusicFilename(mediaId));
    }

    @Override
    public MediaMetadataCompat getCurrentMedia() {
        return mCurrentMedia;
    }

    private void playFile(String filename){
        boolean mediaChanged = (mFilename == null || !filename.equals(mFilename));
        if (mCurrentMediaPlayedToCompletion){
            // Last audio file was played to completion, the resourceId hasn't changed, but the
            // player was released, so force a reload of the media file for playback.
            mediaChanged = true;
            mCurrentMediaPlayedToCompletion = false;
        }
        if (!mediaChanged){

            if (!isPlaying()){
                play();
            }
            return;
        } else {
            release();
        }

        mFilename = filename;
        initializeMediaPlayer();

        try {
            File file = new File(mFilename);
            if (file.exists()) {
                mMediaPlayer.setDataSource(file.getPath());
            }
        }catch (Exception e){
            throw new RuntimeException("Failed to open file: " + mFilename, e);
        }

        try {
            mMediaPlayer.prepare();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open file: " + mFilename, e);

        }

        play();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    protected void onPlay() {
        try {
            if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
                this.startFadeIn();
            }
        } finally {
            // start được save bài hiện đang play và tăng điểm play lên
            setNewState(PlaybackStateCompat.STATE_PLAYING);
            String mediaID = Utils.getKeyByValue(MusicLibrary.fileName, mFilename);
            mQueueManager.setCurrentMediaMetadata(MusicLibrary.music.get(mediaID));
            mMediaManager.increase(Constants.VALUE.MOST_MUSIC,mediaID);
        }
    }

    @Override
    protected void onPause() {
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();

            }
        }finally {
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    @Override
    protected void onStop() {
        // Regardless of whether or not the MediaPlayer has been created / started, the state must
        // be updated, so that MediaNotificationManager can take down the notification.
        try {
            release();
        }finally {
            setNewState(PlaybackStateCompat.STATE_STOPPED);
        }
    }

    @Override
    public void onNext(MediaMetadataCompat metadataCompat) {
        playFromMedia(metadataCompat);
    }

    @Override
    public void onPrevious(MediaMetadataCompat metadataCompat) {

    }

    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void seekTo(long position) {

        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mSeekWhileNotPlaying = (int) position;
            }
            mMediaPlayer.seekTo((int) position);

            // Set the state (to the current state) because the position changed and should
            // be reported to clients.
            setNewState(PlaybackStateCompat.STATE_BUFFERING);
        }
    }
    float volume = 1;
    float speed = 0.05f;

    public void FadeOut(float deltaTime) {
        mMediaPlayer.setVolume(volume, volume);
        volume -= speed* deltaTime;

    }
    public void FadeIn(float deltaTime)
    {
        mMediaPlayer.setVolume(volume, volume);
        volume += speed* deltaTime;

    }
    @Override
    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public void startFadeIn(){
        final int FADE_DURATION = 3000; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;
        final int MAX_VOLUME = 1; //The volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeInStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume>=1f){
                    timer.cancel();
                    timer.purge();
                }
            }

        };

        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL);

    }

    public void startFadeOut(){
        final int FADE_DURATION = 3000; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;
        final int MAX_VOLUME = 1; //The volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeOutStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if(volume>=1f){
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    public void fadeInStep(float deltaVolume) {
        try {
            mMediaPlayer.setVolume(volume, volume);
            volume += deltaVolume;
        }catch (IllegalStateException e){

        }
    }

    public void fadeOutStep(float deltaVolume) {
        try {
            mMediaPlayer.setVolume(volume, volume);
            volume -= deltaVolume;
        }catch (IllegalStateException e){

        }
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeat){
            mPlaybackInfoListener.onPlaybackCompleted(false);
        }else {
            mPlaybackInfoListener.onPlaybackCompleted(true);
        }
        // Set the state to "paused" because it most closely matches the state
        // in MediaPlayer with regards to available state transitions compared
        // to "stop".
        // Paused allows: seekTo(), start(), pause(), stop()
        // Stop allows: stop()
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */
    private ScheduledExecutorService mExecutor;
    private Runnable mSeekBarPositionUpdateTask;
    private void startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }

        mExecutor.scheduleAtFixedRate(
                mSeekBarPositionUpdateTask,
                0,
                1000,
                TimeUnit.MILLISECONDS
        );
    }

    // Reports media playback position to mPlaybackProgressCallback.
    private void stopUpdatingCallbackWithPosition() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
            mSeekBarPositionUpdateTask = null;
        }
    }

    private void updateProgressCallbackTask() {
        if (mMediaPlayer!= null && mMediaPlayer.isPlaying()) {
            int currentPosition = mMediaPlayer.getCurrentPosition();
            if (mPlaybackInfoListener != null) {
                mPlaybackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }


}
