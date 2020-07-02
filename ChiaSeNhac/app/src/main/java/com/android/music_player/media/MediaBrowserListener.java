package com.android.music_player.media;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import java.util.List;

/**
 * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
 * <p>
 * Here would also be where one could override
 * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
 * are added or removed from the queue. We don't do this here in order to keep the UI
 * simple.
 */
public class MediaBrowserListener extends MediaControllerCompat.Callback {
    private OnChangeMusicListener onChangeMusicListener;
    private String TAG = "JJJ";

    public interface OnChangeMusicListener {
        void onStateChange(boolean isPlay, PlaybackStateCompat state);
        void onComplete(boolean isNext);
        void onMediaMetadata(MediaMetadataCompat mediaMetadata);
    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {
        super.onRepeatModeChanged(repeatMode);
        Log.d("LLL", "MediaBrowserListener --- onRepeatModeChanged"+repeatMode);
    }

    public void setOnChangeMusicListener(OnChangeMusicListener onChangeMusicListener){
        this.onChangeMusicListener = onChangeMusicListener;
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (state!= null && onChangeMusicListener != null) {
            Log.d(TAG,
                    "MediaBrowserListener --- onPlaybackStateChanged: "+state.getState()+" --- " +
                            "current pos:" +
                            " "+state.getPosition());
            boolean isPlay = state.getExtras().getBoolean("isPlay");
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    Log.d(TAG,"MediaBrowserListener --- STATE_PLAYING: "+state.getPosition());

                    onChangeMusicListener.onStateChange(isPlay, state);
                    break;
                case PlaybackStateCompat.STATE_BUFFERING:
                    Log.d(TAG,"MediaBrowserListener --- STATE_BUFFERING: "+state.getPosition() +
                            " --- "+state.getExtras().getBoolean("isPlay"));
                    onChangeMusicListener.onStateChange(isPlay,
                            state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:

                case PlaybackStateCompat.STATE_STOPPED:
                    onChangeMusicListener.onStateChange(isPlay,state);
                    break;
            }
        }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
        if (mediaMetadata == null) {
            return;
        }
        // assignData up việc chuyển page
        if (onChangeMusicListener != null){
            Log.d("CCC",
                    "MediaBrowserListener --- onMetadataChanged: "+ (mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
            onChangeMusicListener.onMediaMetadata(mediaMetadata);
        }
    }

    @Override
    public void onSessionDestroyed() {
        super.onSessionDestroyed();
    }

    @Override
    public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
        super.onQueueChanged(queue);
    }
}