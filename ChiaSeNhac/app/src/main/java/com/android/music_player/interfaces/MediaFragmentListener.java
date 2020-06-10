package com.android.music_player.interfaces;

import android.support.v4.media.MediaBrowserCompat;

public interface MediaFragmentListener extends MediaBrowserProvider {
    void onMediaItemSelected(MediaBrowserCompat.MediaItem item);
    void setToolbarTitle(CharSequence title);
}