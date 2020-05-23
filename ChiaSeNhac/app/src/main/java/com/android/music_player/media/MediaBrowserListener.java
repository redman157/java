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
    private OnMedia onMedia;
    public interface OnMedia {
        void onCheck(boolean isPlay);
        void onChange(String songName);
    }

    public void setOnMedia(OnMedia onMedia){
        this.onMedia = onMedia;
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
        if (playbackState!= null && onMedia != null) {
            switch (playbackState.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    onMedia.onCheck(true);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                case PlaybackStateCompat.STATE_STOPPED:
                    onMedia.onCheck(false);
                case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                    onMedia.onCheck(false);
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
        if (onMedia != null) {
            onMedia.onChange(mediaId);
        }
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