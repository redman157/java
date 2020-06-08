package com.android.music_player.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.music_player.activities.HomeActivity;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;

import java.io.File;

public class MediaPlayerAdapter extends PlayerAdapter {
    private final Context mContext;
    private MediaPlayer mMediaPlayer;
    private String mFilename;
    private PlaybackInfoListener mPlaybackInfoListener;
    private MediaMetadataCompat mCurrentMedia;
    private MusicManager mMusicManager;
    private int mState;
    private boolean isRepeat= false;
    private boolean mCurrentMediaPlayedToCompletion;

    // Work-around for a MediaPlayer bug related to the behavior of MediaPlayer.seekTo()
    // while not playing.
    private int mSeekWhileNotPlaying = -1;

    public MediaPlayerAdapter(@NonNull Context context, PlaybackInfoListener listener) {
        super(context);
        this.mContext = context.getApplicationContext();
        mPlaybackInfoListener = listener;
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(context);
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
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mPlaybackInfoListener.onPlaybackCompleted(true);

                    // Set the state to "paused" because it most closely matches the state
                    // in MediaPlayer with regards to available state transitions compared
                    // to "stop".
                    // Paused allows: seekTo(), start(), pause(), stop()
                    // Stop allows: stop()
                    if (isRepeat){

                    }else {
                        setNewState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT);
                    }
                }
            });
        }
    }
    // This is the main reducer for the player state machine.
    private void setNewState(@PlaybackStateCompat.State int newPlayerState){

        mState = newPlayerState;

        Log.d("BBB","MediaPlayerAdapter --- setNewState: "+newPlayerState );
        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is assignData to true.

        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true;
        }



        // Work around for MediaPlayer.getCurrentPosition() when it changes while not playing.
        final long reportPosition;
        if (mSeekWhileNotPlaying >= 0){
            Log.d("TTT", "MediaPlayerAdapter --- mSeekWhileNotPlaying > 0: ");
            reportPosition = mSeekWhileNotPlaying;

            if (mState == PlaybackStateCompat.STATE_PLAYING) {
                mSeekWhileNotPlaying = -1;
            }
        }else {
            Log.d("TTT", "MediaPlayerAdapter --- mSeekWhileNotPlaying > 0: ");
            reportPosition = (mMediaPlayer == null)?
                    0 : mMediaPlayer.getCurrentPosition();
        }
        Log.d("TTT", "MediaPlayerAdapter --- setNewState: "+reportPosition);
        updatePlaybackState(mState ,reportPosition);
    }

    private void updatePlaybackState(int state,long reportPosition) {
        if (mPlaybackInfoListener == null){
            return;
        }

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(state, reportPosition, 1.0f, SystemClock.elapsedRealtime());

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
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                PlaybackStateCompat.ACTION_SET_REPEAT_MODE|
                PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE|
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
            Log.d("CCC", mFilename +"\n ---- "+new File(mFilename).exists());
            File file = new File(mFilename);
            if (file.exists()) {
                mMediaPlayer.setDataSource(file.getPath());
            }
        }catch (Exception e){
            Log.d("CCC", String.valueOf(e.getClass()));
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
                setNewState(PlaybackStateCompat.STATE_PLAYING);
            }
        } finally {
            // start được save bài hiện đang play và tăng điểm play lên
            mMusicManager.setCurrentMusic(mFilename);
         /*   mMusicManager.getStatistic().increase(
                    Constants.VALUE.MOST_SONG, Utils.getKeyByValue(MusicLibrary.fileName,
                            mFilename));*/
        }
    }

    @Override
    protected void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    @Override
    protected void onStop() {
        // Regardless of whether or not the MediaPlayer has been created / started, the state must
        // be updated, so that MediaNotificationManager can take down the notification.
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
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
            setNewState(mState);
        }
    }

    @Override
    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

}
