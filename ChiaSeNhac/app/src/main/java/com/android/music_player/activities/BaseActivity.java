package com.android.music_player.activities;

import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.music_player.managers.MediaManager;
import com.android.music_player.media.BrowserConnectionListener;
import com.android.music_player.media.BrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.SharedPrefsUtils;

public abstract class BaseActivity extends ActionBarCastActivity implements BrowserConnectionListener.OnMediaController {
    private boolean serviceBound = false;
    private MediaService mediaService;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    private MediaControllerCompat mMediaControllerCompat;
    public BrowserHelper mBrowserHelper;
    private MediaBrowserListener mMediaBrowserListener;
    public abstract void initManager();
    public abstract void switchFragment(Fragment fragment);
    private static final String TAG = BaseActivity.class.getSimpleName();

    public abstract void getMediaManager(MediaManager mediaManager);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(this);

        mBrowserHelper = mMediaManager.getMediaBrowserConnection();
        mMediaManager.getMediaBrowserConnection().setOnMediaController(this);
        getMediaManager(mMediaManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBrowserHelper.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBrowserHelper.onStop();
    }

    public void setMediaChange(String tag,
            MediaBrowserListener.OnChangeMusicListener onChangeMusicListener){
        if (mMediaBrowserListener == null) {
            mMediaBrowserListener = new MediaBrowserListener();
        }
        mMediaBrowserListener.setOnChangeMusicListener(onChangeMusicListener);
        mBrowserHelper.registerCallback(tag, mMediaBrowserListener);
    }

    public MediaControllerCompat getControllerActivity() {
        return mMediaControllerCompat;
    }

    public void setControllerActivity(MediaControllerCompat mMediaControllerCompat) {
        this.mMediaControllerCompat = mMediaControllerCompat;
    }

    @Override
    public void onController(MediaControllerCompat mediaController) {
        // khi connect thành công của media browser
        // thì mới có controller chuyển cho activity sử dụng
        setControllerActivity(mediaController);
    }
}