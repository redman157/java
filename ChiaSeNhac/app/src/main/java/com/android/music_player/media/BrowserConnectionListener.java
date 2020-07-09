package com.android.music_player.media;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
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
public class BrowserConnectionListener extends BrowserHelper {
    private MediaSeekBar mSeekBarAudio;
    private String TAG = "JJJ";
    private String mediaId;
    public MediaControllerCompat mMediaController;
    private Context context;
    private TextView mTextLeftTime,mTextRightTime;
    private boolean isPlay;
    public MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;
    private MediaManager mMediaManager = MediaManager.getInstance();

    public OnServiceConnect onServiceConnect;
    public interface OnServiceConnect {
        void onConnect(MediaBrowserCompat mediaBrowserCompat, MediaControllerCompat mediaController);
    }

    public void setOnServiceConnectListener(OnServiceConnect onServiceConnect) {
        this.onServiceConnect = onServiceConnect;

    }

    public BrowserConnectionListener(Context context) {
        super(context, MediaService.class);
        this.context = context;
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
        mMediaManager.setContext(context);
    }

    @Override
    protected void onConnected(@NonNull MediaControllerCompat mediaController,
                               MediaBrowserCompat mediaBrowserCompat) {
        if (onServiceConnect != null) {
            onServiceConnect.onConnect(mediaBrowserCompat ,mediaController);
        }
        Log.d(TAG, "onConnected: "+mediaController.getPlaybackInfo().getPlaybackType());
        // khi connect mình sẽ set bài hát ở đây
    }

    public MediaBrowserSubscriptionCallback getCallback() {
        return mMediaBrowserSubscriptionCallback;
    }

    @Override
    public void unSetSubscribe(String parentID,
                              MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback) {
        super.unSetSubscribe(parentID, mediaBrowserSubscriptionCallback);
    }
    @Override
    public void setSubscribe(String parentID, MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback) {
        super.setSubscribe(parentID, mediaBrowserSubscriptionCallback);
    }
    List<MediaBrowserCompat.MediaItem> mediaItems = null;
    @Override
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
        super.onChildrenLoaded(parentId, children);

        mMediaController = getMediaController();
        for ( MediaBrowserCompat.MediaItem mediaItem : children) {
            Log.d("ZZZ", "onChildrenLoaded --- for: "+mediaItem.getMediaId() + "");
        }
        mMediaController.removeQueueItem(null);
        if (children.size()> 0) {
            Log.d("ZZZ", "onChildrenLoaded --- enter if");
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mMediaController.addQueueItem(mediaItem.getDescription());

            }
//            for ( MediaBrowserCompat.MediaItem mediaItem : children) {
//                Log.d("ZZZ", mediaItem.getMediaId() + "");
//                mMediaController.removeQueueItem(mediaItem.getDescription());
//            }
        }else if(children.size() == 0) {

            mMediaManager.getStateViewModel().getNamePlayList().observe((LifecycleOwner) context, new Observer<String>() {
                @Override
                public void onChanged(String titlePlayList) {
                    Log.d("ZZZ", "onChildrenLoaded --- enter else if: "+titlePlayList);

                    if (mMediaManager.getAllMusicOfPlayList(titlePlayList) != null) {
                        mediaItems = MusicLibrary.getAlbumItems(mMediaManager.getAllMusicOfPlayList(titlePlayList));
                        Log.d("ZZZ",
                                "onChildrenLoaded --- enter else if size: " + mediaItems.size());
                    }

                }
            });
            if (mediaItems != null) {
                for (int i = 0; i< mediaItems.size(); i++){
                    Log.d("ZZZ", "Số lần: "+i+ " --- "+mediaItems.get(i).getDescription().getMediaId());
                    mMediaController.addQueueItem(mediaItems.get(i).getDescription());
                }
            }
        }
    }

    public void setAutoPlay(String mediaID, boolean autoPlay){
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