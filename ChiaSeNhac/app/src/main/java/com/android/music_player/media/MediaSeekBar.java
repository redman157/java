package com.android.music_player.media;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.android.music_player.utils.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MediaSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener{
    private MediaControllerCompat mMediaController;
    private ControllerCallback mControllerCallback;
    private ValueAnimator mProgressAnimator;
    private boolean mIsTracking = false;
    private TextView textLeft, textRight;
    private long stateCompat;
    private MediaBrowserListener.OnChangeMusicListener onChangeMusicListener;
    public void setOnChangeMusicListener(MediaBrowserListener.OnChangeMusicListener onChangeMusicListener){
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

    public void setMediaController(final MediaControllerCompat mediaController,
                                   TextView textLeft, TextView textRight) {
        this.textLeft = textLeft;
        this.textRight = textRight;
        if (mediaController != null) {
            Log.d("ZZZ", "setMediaController: enter");
            mControllerCallback = new ControllerCallback();
            mediaController.registerCallback(mControllerCallback);
        } else if (mMediaController != null) {
            Log.d("ZZZ", "setMediaController: enter");
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
        }
        mMediaController = mediaController;

    }

    public void disconnectController() {
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
            mMediaController = null;
        }
    }


    @Override
    public final void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        // Prohibit adding seek listeners to this subclass.
        throw new UnsupportedOperationException("Cannot add listeners to a MediaSeekBar");
    }

    public void setUpdateState(long state){
        stateCompat = state;
//        scheduleSeekbarUpdate(state);
    }

    public void setUpdateMedia(MediaMetadataCompat mediaMetadataCompat){
//        long duration = mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
//
//        ValueAnimator animator = ValueAnimator.ofFloat(stateCompat, duration);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                setProgress((Integer) animation.getAnimatedValue());
//                textLeft.setText(Utils.formatTime((Integer) animation.getAnimatedValue()));
//            }
//        });
//
//        animator.start();

//        int max;
//        if (mediaMetadataCompat != null){
//
//            max = (int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
//            textRight.setText(Utils.formatTime(max));
//            Log.d("ZZZ",
//                    "MediaSeekBar --- setMediaController --- onMetadataChanged: "+(int) mediaMetadataCompat.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
//        }else {
//            max = 0;
//            Log.d("ZZZ",
//                    "MediaSeekBar --- setMediaController --- onMetadataChanged: null");
//        }
//        setProgress(0);
//        setMax(max);
    }

    private final ScheduledExecutorService mExecutorService =
            Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> mScheduleFuture;
    private final Handler mHandler = new Handler();



    private void updateProgress(long stateCompat) {
//        long currentPosition = stateCompat;
//        long timeDelta = SystemClock.elapsedRealtime() -
//                stateCompat;
//        currentPosition += (int) timeDelta * stateCompat;
//        Log.d("XXX","updateProgress: "+stateCompat+" ---- currentPosition: "+currentPosition);
//        if (stateCompat.getState() == PlaybackStateCompat.STATE_PLAYING) {
//            // Calculate the elapsed time between the last position update and now and unless
//            // paused, we can assume (delta * speed) + current position is approximately the
//            // latest position. This ensure that we do not repeatedly call the getPlaybackState()
//            // on MediaControllerCompat.
//
//        }
//        textLeft.setText(Utils.formatTime((int) currentPosition));
//        setProgress((int) currentPosition);
    }

    private void scheduleSeekbarUpdate(final long stateCompat) {
        stopSeekbarUpdate();
        if (!mExecutorService.isShutdown()) {
            mScheduleFuture = mExecutorService.scheduleAtFixedRate(
                    new Runnable() {
                        @Override
                        public void run() {
                            mHandler.post( new Runnable() {
                                @Override
                                public void run() {
                                    updateProgress(stateCompat);
                                }
                            });
                        }
                    }, 100,
                    1000, TimeUnit.MILLISECONDS);
        }
    }

    private void stopSeekbarUpdate() {
        if (mScheduleFuture != null) {
            mScheduleFuture.cancel(false);
        }
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
        Log.d("ZZZ", "onStopTrackingTouch: "+getProgress());
        mMediaController.getTransportControls().seekTo(getProgress());
        mIsTracking = false;
//        scheduleSeekbarUpdate(stateCompat);
    }



    private class ControllerCallback extends MediaControllerCompat.Callback implements ValueAnimator.AnimatorUpdateListener{
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            Log.d("ZZZ",
                    "MediaSeekBar --- ControllerCallback --- onPlaybackStateChanged: "+ state.getPosition());

         /*   if (mProgressAnimator != null){
                mProgressAnimator.cancel();
                mProgressAnimator = null;
            }*/

            int progress;
            if (state != null){
                progress = (int) state.getPosition();
            }else {
                progress = 0;
            }
            setProgress(progress);


            // If the media is playing then the seekbar should follow it, and the easiest
            // way to do that is to create a ValueAnimator to update it so the bar reaches
            // the end of the media the same time as playback gets there (or close enough).

            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING){
                int timeToEnd = (int) ((getMax() - progress) / state.getPlaybackSpeed());

                if (timeToEnd < 0){
                    Log.d("CCC","MediaSeekBar --- onPlaybackStateChanged --- timeToEnd : enter");
                    timeToEnd = 0;
                }
                mProgressAnimator = ValueAnimator.ofInt(progress, getMax())
                        .setDuration(timeToEnd);
                mProgressAnimator.setInterpolator(new LinearInterpolator());
                mProgressAnimator.addUpdateListener(this);
                mProgressAnimator.start();
            }
        }


        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            int max;
            if (metadata != null){

                max = (int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                textRight.setText(Utils.formatTime(max));
                Log.d("ZZZ",
                        "MediaSeekBar --- setMediaController --- onMetadataChanged: "+(int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            }else {
                max = 0;
                Log.d("ZZZ",
                        "MediaSeekBar --- setMediaController --- onMetadataChanged: null");
            }
            setProgress(0);
            setMax(max);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (mIsTracking) {
                animation.cancel();
                return;
            }
            final int animatedIntValue = (int) animation.getAnimatedValue();
            Log.d("ZZZ",
                    "MediaSeekBar --- setMediaController --- animatedIntValue: "+animatedIntValue);
            textLeft.setText(Utils.formatTime(animatedIntValue));
            setProgress(animatedIntValue);
        }
    }
}
