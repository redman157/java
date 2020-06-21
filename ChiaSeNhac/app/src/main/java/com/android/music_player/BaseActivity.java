package com.android.music_player;

import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.music_player.managers.MediaManager;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.SharedPrefsUtils;

public abstract class BaseActivity extends ActionBarCastActivity {
    private boolean serviceBound = false;
    private MediaService mediaService;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager = MediaManager.getInstance();

    public MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mMediaBrowserListener;
    public abstract void initManager();
    public abstract void switchFragment(Fragment fragment);
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaManager.setContext(this);
        mMediaBrowserHelper = mMediaManager.getMediaBrowserConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowserHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaBrowserHelper.onStop();
    }


    public void setMediaChange(String tag,
            MediaBrowserListener.OnChangeMusicListener onChangeMusicListener){
        if (mMediaBrowserListener == null) {
            mMediaBrowserListener = new MediaBrowserListener();
        }
        mMediaBrowserListener.setOnChangeMusicListener(onChangeMusicListener);
        mMediaBrowserHelper.registerCallback(tag, mMediaBrowserListener);
    }

    public MediaControllerCompat getController() {
        return mMediaManager.getMediaBrowserConnection().getMediaController();
    }
}