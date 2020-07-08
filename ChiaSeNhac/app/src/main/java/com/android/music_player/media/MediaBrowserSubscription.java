package com.android.music_player.media;

import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

public class MediaBrowserSubscription extends MediaBrowserCompat.SubscriptionCallback {
    // tu service gọi lên từ onChildrenLoaded
    private BrowserHelper browserHelper;
    @Override
    public void onChildrenLoaded(@NonNull String parentId,
                                 @NonNull List<MediaBrowserCompat.MediaItem> children) {
        Log.d("WWW","MediaBrowserSubscriptionCallback --- onChildrenLoaded: "+parentId);

        browserHelper.onChildrenLoaded(parentId, children);
    }

    @Override
    public void onError(@NonNull String parentId) {
        Log.d("JJJ", "BrowserHelper --- MediaBrowserSubscriptionCallback: "+parentId);
    }
}
