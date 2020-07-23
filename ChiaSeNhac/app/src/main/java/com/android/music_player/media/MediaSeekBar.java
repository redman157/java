package com.android.music_player.media;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.android.music_player.utils.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MediaSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener{
    private boolean mIsTracking = false;
    private TextView mTextStartTime, mTextEndTime;
    private MediaControllerCompat mControllerCompat;
    private PlaybackStateCompat mStateCompat;
    private MediaMetadataCompat mMediaMetadataCompat;
    private MediaBrowserCallBack.OnChangeMusicListener onChangeMusicListener;
    public void setOnChangeMusicListener(MediaBrowserCallBack.OnChangeMusicListener onChangeMusicListener){
        this.onChangeMusicListener = onChangeMusicListener;
    }
    public MediaSeekBar(Context context ) {
        super(context);
        super.setOnSeekBarChangeListener(this);

    }

    public MediaSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnSeekBarChangeListener(this);
    }

    public MediaSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnSeekBarChangeListener(this);

    }

    @Override
    public final void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        // Prohibit adding seek listeners to this subclass.
        throw new UnsupportedOperationException("Cannot add listeners to a MediaSeekBar");
    }

    public void setMediaController(MediaControllerCompat controller, TextView textStartTime,
                                   TextView textEndTime){

        mControllerCompat = controller;
        mTextStartTime = textStartTime;
        mTextEndTime = textEndTime;
    }

    public void setUpdateState(PlaybackStateCompat state, boolean isPlay){
        mStateCompat = state;
        if (isPlay &&
                (state.getState() == PlaybackStateCompat.STATE_PLAYING || state.getState() == PlaybackStateCompat.STATE_BUFFERING)) {
            scheduleSeekBarUpdate();
        }
    }

    public void setUpdateMedia(MediaMetadataCompat mediaMetadataCompat){
        updateDuration(mediaMetadataCompat);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsTracking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mControllerCompat.getTransportControls().seekTo(getProgress());
    }

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();
    private static final long PROGRESS_UPDATE_INTERNAL = 1000;
    private static final long PROGRESS_UPDATE_INITIAL_INTERVAL = 100;
    private ScheduledFuture<?> mScheduleFuture;
    private PlaybackStateCompat mLastPlaybackState;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateProgressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private void scheduleSeekBarUpdate() {
        stopSeekBarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post(mUpdateProgressTask);
                        }
                    }, PROGRESS_UPDATE_INITIAL_INTERVAL,
                    PROGRESS_UPDATE_INTERNAL, TimeUnit.MILLISECONDS);
        }
    }

    public void stopSeekBarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
    }

    private void updateProgress() {
        if (mLastPlaybackState == null) {
            return;
        }

        long currentPosition = mLastPlaybackState.getPosition();
        if (currentPosition > getMax()) {
            return;
        }

        if (mLastPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING
                ||mLastPlaybackState.getState() == PlaybackStateCompat.STATE_BUFFERING) {
            // Calculate the elapsed time between the last position update and now and unless
            // paused, we can assume (delta * speed) + current position is approximately the
            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
            // on MediaControllerCompat.
            long timeDelta = SystemClock.elapsedRealtime() -
                    mLastPlaybackState.getLastPositionUpdateTime();
            currentPosition += (int) timeDelta * mLastPlaybackState.getPlaybackSpeed();
        }

        mTextStartTime.setText(Utils.formatTime((int) currentPosition));
        setProgress((int) currentPosition);
    }
    private void updateDuration(MediaMetadataCompat metadata) {
        if (metadata == null) {
            return;
        }
        int duration = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        setMax(duration);
        mTextEndTime.setText(Utils.formatTime(duration));
    }
}
