package com.android.music_player.media;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

import com.android.music_player.activities.HomeActivity;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.services.MediaService;

import java.util.List;

/**
 * Customize the connection to our {@link MediaBrowserServiceCompat}
 * and implement our app specific desires.
 */
public class BrowserConnectionListener extends BrowserHelper {
    private String TAG = "JJJ";
    public MediaControllerCompat mMediaController;
    private Context context;
    public MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;
    private MediaManager mMediaManager = MediaManager.getInstance();
    List<MediaBrowserCompat.MediaItem> mediaItems = null;

    private OnServiceConnect onServiceConnect;
    public interface OnServiceConnect {
        void onConnect(MediaControllerCompat mediaController);
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
    protected void onConnected(@NonNull MediaControllerCompat mediaController){
        if (onServiceConnect != null) {
            Log.d(TAG, "BrowserConnectionListener --- onConnected: if enter");
            onServiceConnect.onConnect(mediaController);
        }else {
            Log.d(TAG, "BrowserConnectionListener --- onConnected: else enter");
        }

//        Log.d(TAG, "onConnected: "+mediaController.getPlaybackInfo().getPlaybackType());
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

    @Override
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
        super.onChildrenLoaded(parentId, children);

        mMediaController = getMediaController();
        mMediaController.removeQueueItem(null);
        if (children.size()> 0) {
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                Log.d("ZZZ", mediaItem.getDescription().getMediaId());
                mMediaController.addQueueItem(mediaItem.getDescription());

            }
        }else if(children.size() == 0) {
            List<MediaBrowserCompat.MediaItem> namePlayList =
                    ((HomeActivity)context).getQueueManager().getControllerStyle();
            if (namePlayList != null) {
                for (int i = 0; i< namePlayList.size(); i++){
                    mMediaController.addQueueItem(namePlayList.get(i).getDescription());
                }
            }
        }
    }
}