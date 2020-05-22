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
    private OnPlayPause onPlayPause;
    public interface OnPlayPause {
        void onCheck(boolean isPlay);
    }

    public void setOnPlayPause(OnPlayPause onPlayPause){
        this.onPlayPause = onPlayPause;
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        super.onRepeatModeChanged(repeatMode);

    }

    @Override
    public void onShuffleModeChanged(int shuffleMode) {
        super.onShuffleModeChanged(shuffleMode);

    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
        if (playbackState!= null && onPlayPause != null) {
            switch (playbackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    onPlayPause.onCheck(true);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                case PlaybackStateCompat.STATE_STOPPED:
                    onPlayPause.onCheck(false);
                case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                    onPlayPause.onCheck(false);
                    break;
            }
        }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
        if (mediaMetadata == null) {
            return;
        }
        String mediaId = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);

        // set up việc chuyển page
        Log.d("SSS","onMetadataChanged: "+mediaId);

    }

    @Override
    public void onSessionDestroyed() {
        super.onSessionDestroyed();
    }

    @Override
    public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {

        super.onQueueChanged(queue);
        Log.d("SSS","onQueueChanged: "+queue.get(0));

    }
}