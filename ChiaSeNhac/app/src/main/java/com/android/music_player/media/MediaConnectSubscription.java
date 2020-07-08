package com.android.music_player.media;

import android.support.v4.media.MediaBrowserCompat;

public class MediaConnectSubscription extends MediaBrowserCompat.ConnectionCallback {
    @Override
    public void onConnected() {
        super.onConnected();
    }

    @Override
    public void onConnectionSuspended() {
        super.onConnectionSuspended();
    }

    @Override
    public void onConnectionFailed() {
        super.onConnectionFailed();
    }
}
