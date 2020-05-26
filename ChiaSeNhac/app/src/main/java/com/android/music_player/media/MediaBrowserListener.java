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
    private String TAG = "JJJ";
    public interface OnPlayPause {
        void onCheck(boolean isPlay, PlaybackStateCompat state);
        void onMediaMetadata(MediaMetadataCompat mediaMetadata);
    }

    public void setOnPlayPause(OnPlayPause onPlayPause){
        this.onPlayPause = onPlayPause;
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (state!= null && onPlayPause != null) {
            Log.d(TAG,
                    "MediaBrowserListener --- onPlaybackStateChanged: "+state.getState()+" --- " +
                            "current pos:" +
                            " "+state.getPosition());
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    Log.d(TAG,"MediaBrowserListener --- STATE_PLAYING: "+state.getPosition());

                    onPlayPause.onCheck(true, state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                case PlaybackStateCompat.STATE_STOPPED:
                    onPlayPause.onCheck(false,state);
                    break;
                case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                    break;
                case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                    onPlayPause.onCheck(false,state);
                    break;
            }
        }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
        if (mediaMetadata == null) {
            return;
        }
        // set up việc chuyển page
        if (onPlayPause!= null){
//            Log.d("VVV", Log.getStackTraceString(new Exception()));

            Log.d("VVV",
                    "MediaBrowserListener --- onMetadataChanged: "+ (mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)));
            onPlayPause.onMediaMetadata(mediaMetadata);
        }
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