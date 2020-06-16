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
    private MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;
    private MediaManager mMediaManager = MediaManager.getInstance();

    public OnMediaController onMediaController;
    public interface OnMediaController{
        void onController(MediaControllerCompat mediaController);
    }

    public void setOnMediaController(OnMediaController onMediaController){
        this.onMediaController = onMediaController;
    }

    public void setMediaId(String mediaId, boolean isPlay) {
        this.mediaId = mediaId;
        this.isPlay = isPlay;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;

    }

    public MediaBrowserConnection(Context context) {
        super(context, MediaService.class);
        this.context = context;
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
        mMediaManager.setContext(context);
    }

    @Override
    protected void onConnected(@NonNull MediaControllerCompat mediaController) {
        if (onMediaController != null) {
            onMediaController.onController(mediaController);
        }
        Log.d(TAG, "onConnected: "+mediaController.getPlaybackInfo().getPlaybackType());
        // khi connect mình sẽ set bài hát ở đây
        this.getMediaBrowser().subscribe(MusicLibrary.getRoot(), mMediaBrowserSubscriptionCallback);
//        mSeekBarAudio.setMediaController(mediaController, mTextLeftTime, mTextRightTime);
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
        setMediaId(mediaId, false);
    }

    public void setAutoPlay(String songName, boolean autoPlay){
        if (mMediaController != null) {
            BundleHelper.Builder builder = new BundleHelper.Builder();
            builder.putBoolean(Constants.INTENT.AUTO_PLAY, autoPlay);
            if (songName.equals(mMediaManager.getCurrentMusic())) {
                mMediaController.getTransportControls().stop();
            }
            mMediaController.getTransportControls().prepareFromMediaId(songName,
                    builder.generate().getBundle());
        }
    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return super.getMediaBrowser();
    }
}