package com.android.music_player.media;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.BundleHelper;
import com.android.music_player.utils.Constants;

import java.util.List;

/**
 * Customize the connection to our {@link MediaBrowserServiceCompat}
 * and implement our app specific desires.
 */
public class MediaBrowserConnection extends MediaBrowserHelper {
    private MediaSeekBar mSeekBarAudio;
    private String TAG = "JJJ";
    private String mediaId;
    public MediaControllerCompat mMediaController;
    private Context context;
    private TextView mTextLeftTime,mTextRightTime;
    private boolean isPlay;
    public MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;
    private MediaManager mMediaManager = MediaManager.getInstance();

    public OnMediaController onMediaController;
    public interface OnMediaController{
        void onController(MediaControllerCompat mediaController);
    }

    public MediaBrowserConnection(Context context) {
        super(context, MediaService.class);
        this.context = context;
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
        mMediaManager.setContext(context);
    }

    @Override
    protected void onConnected(@NonNull MediaControllerCompat mediaController,
                               MediaBrowserCompat mediaBrowser) {
        if (onMediaController != null) {
            onMediaController.onController(mediaController);
        }
        Log.d(TAG, "onConnected: "+mediaController.getPlaybackInfo().getPlaybackType());
        // khi connect mình sẽ set bài hát ở đây
        Log.d("MMM","onConnected: "+mediaBrowser.getRoot() );

        mediaBrowser.subscribe(MusicLibrary.MEDIA_ID_ROOT, mMediaBrowserSubscriptionCallback);
    }

    @Override
    public void setSubscribe(String parentID, MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback) {
        super.setSubscribe(parentID, mediaBrowserSubscriptionCallback);
    }

    @Override
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
        super.onChildrenLoaded(parentId, children);

        mMediaController = getMediaController();

        // Queue up all media items for this simple sample.
        for (final MediaBrowserCompat.MediaItem mediaItem : children) {
            mMediaController.addQueueItem(mediaItem.getDescription());
        }

    }

    public void setAutoPlay(String mediaID, boolean autoPlay){
        Log.d("VVV", "SetAutoPlay: "+mediaID);
        if (mMediaController != null) {
            BundleHelper.Builder builder = new BundleHelper.Builder();
            builder.putBoolean(Constants.INTENT.AUTO_PLAY, autoPlay);
            if (mediaID.equals(mMediaManager.getCurrentMusic())) {
                mMediaController.getTransportControls().stop();
            }
            mMediaController.getTransportControls().prepareFromMediaId(mediaID,
                    builder.generate().getBundle());
        }
    }

}