package com.android.music_player.media;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;

import com.android.music_player.utils.Utils;

public class MediaSeekBar extends AppCompatSeekBar {
    private MediaControllerCompat mMediaController;
    private ControllerCallback mControllerCallback;
    private ValueAnimator mProgressAnimator;
    private boolean mIsTracking = false;
    private TextView textLeft, textRight;

    public void setText(TextView textLeft, TextView textRight){
        this.textLeft = textLeft;
        this.textRight = textRight;
    }
    public MediaSeekBar(Context context ) {
        super(context);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

    }

    public MediaSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    }

    public MediaSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

    }

    public void setMediaController(final MediaControllerCompat mediaController) {
        if (mediaController != null) {
            mControllerCallback = new ControllerCallback();
            mediaController.registerCallback(mControllerCallback);
        } else if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
        }
        mMediaController = mediaController;
    }

    public void disconnectController(){
        if (mMediaController != null){
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
            mMediaController = null;
        }
    }

    private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsTracking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mMediaController.getTransportControls().seekTo(getProgress());
            mIsTracking = false;
        }
    };

    @Override
    public final void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        // Prohibit adding seek listeners to this subclass.
        throw new UnsupportedOperationException("Cannot add listeners to a MediaSeekBar");
    }

    private class ControllerCallback extends MediaControllerCompat.Callback implements ValueAnimator.AnimatorUpdateListener{

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            if (mProgressAnimator != null){
                mProgressAnimator.cancel();
                mProgressAnimator = null;
            }

            int progress = state != null ? (int) state.getPosition() : 0;
            setProgress(progress);

            // If the media is playing then the seekbar should follow it, and the easiest
            // way to do that is to create a ValueAnimator to update it so the bar reaches
            // the end of the media the same time as playback gets there (or close enough).

            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING){
                int timeToEnd = (int) ((getMax() - progress) / state.getPlaybackSpeed());

                if (timeToEnd < 0){
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
            }else {
                max = 0;
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
            textLeft.setText(Utils.formatTime(animatedIntValue));
            setProgress(animatedIntValue);
        }
    }
}