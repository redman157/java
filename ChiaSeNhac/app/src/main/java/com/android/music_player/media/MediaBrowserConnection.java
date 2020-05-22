package com.android.music_player.media;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

import com.android.music_player.services.MediaService;

import java.util.List;

/**
 * Customize the connection to our {@link MediaBrowserServiceCompat}
 * and implement our app specific desires.
 */
public class MediaBrowserConnection extends MediaBrowserHelper {
    private MediaSeekBar mSeekBarAudio;
    public interface OnMediaItem{
        void onMedia(List<MediaBrowserCompat.MediaItem> children);
    }
    private OnMediaItem onMediaItem;
    public void setOnMedia(OnMediaItem item){
        item = onMediaItem;
    }
    public MediaSeekBar getSeekBarAudio() {
        return mSeekBarAudio;
    }

    public void setSeekBarAudio(MediaSeekBar mSeekBarAudio) {
        this.mSeekBarAudio = mSeekBarAudio;
    }

    public MediaBrowserConnection(Context context) {
        super(context, MediaService.class);
    }

    @Override
    protected void onConnected(@NonNull MediaControllerCompat mediaController) {
        if (mSeekBarAudio != null) {
            mSeekBarAudio.setMediaController(mediaController);
        }
    }

    @Override
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
        super.onChildrenLoaded(parentId, children);

        final MediaControllerCompat mediaController = getMediaController();

        // Queue up all media items for this simple sample.
        for (final MediaBrowserCompat.MediaItem mediaItem : children) {

            mediaController.addQueueItem(mediaItem.getDescription());
        }

        // Call prepare now so pressing play just works.
        mediaController.getTransportControls().prepare();
    }
}