package com.android.music_player.activities;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.media.BrowserConnectionListener;
import com.android.music_player.media.BrowserHelper;
import com.android.music_player.media.MediaBrowserListener;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.BundleHelper;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;

public abstract class BaseActivity extends ActionBarCastActivity implements BrowserConnectionListener.OnServiceConnect {
    private boolean serviceBound = false;
    private MediaService mediaService;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    private MediaControllerCompat mMediaControllerCompat;
    public BrowserHelper mBrowserHelper;
    private MediaBrowserListener mMediaBrowserListener;
    public abstract void initManager();
    private static final String TAG = BaseActivity.class.getSimpleName();
    private MediaBrowserCompat mMediaBrowserCompat;
    public SharedPrefsUtils getSharedPrefsUtils(){
        return mSharedPrefsUtils;
    }

    public MediaManager getMediaManager(){
        return mMediaManager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(this);

        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mBrowserHelper = mMediaManager.getMediaBrowserConnection();
        mMediaManager.getMediaBrowserConnection().setOnServiceConnectListener(this);
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

    public void setControllerActivity(MediaControllerCompat controllerCompat) {
        this.mMediaControllerCompat = controllerCompat;
    }

    public MediaBrowserCompat getMediaBrowserCompat() {
        return mMediaBrowserCompat;
    }

    public void setMediaBrowserCompat(MediaBrowserCompat mediaBrowserCompat) {
        this.mMediaBrowserCompat = mediaBrowserCompat;
    }

    @Override
    public void onConnect(final MediaBrowserCompat mediaBrowserCompat,
                          final MediaControllerCompat mediaController) {
        // khi connect thành công của media browser
        // thì mới có controller chuyển cho activity sử dụng

        setMediaBrowserCompat(mediaBrowserCompat);
        setControllerActivity(mediaController);

        /*VIEW MODEL CHANGE ROOT SERVICE*/
        mMediaManager.getStateViewModel().getParentId().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String parentId) {
                Log.d("ZZZ", "onchange: "+parentId);
                if (parentId.equals(MusicLibrary.MEDIA_ID_ROOT)){

                    // GỠ STATE VÀ SET STATE KHÁC
                    mediaBrowserCompat.unsubscribe(MusicLibrary.MEDIA_ID_EMPTY_ROOT,
                            getMediaManager().getMediaBrowserConnection().getCallback());

                    mediaBrowserCompat.subscribe(MusicLibrary.MEDIA_ID_ROOT,
                            getMediaManager().getMediaBrowserConnection().getCallback());

                }else if (parentId.equals(MusicLibrary.MEDIA_ID_EMPTY_ROOT)){
                    mediaBrowserCompat.unsubscribe(MusicLibrary.MEDIA_ID_ROOT,
                            getMediaManager().getMediaBrowserConnection().getCallback());

                    mediaBrowserCompat.subscribe(MusicLibrary.MEDIA_ID_EMPTY_ROOT,
                        getMediaManager().getMediaBrowserConnection().getCallback());
                }
            }
        });
    }

    public void setAutoPlay(String mediaID, boolean autoPlay){
        if (mMediaControllerCompat != null) {
            BundleHelper.Builder builder = new BundleHelper.Builder();
            builder.putBoolean(Constants.INTENT.AUTO_PLAY, autoPlay);
            if (mediaID.equals(mMediaManager.getCurrentMusic())) {
                mMediaControllerCompat.getTransportControls().stop();
            }
            mMediaControllerCompat.getTransportControls().prepareFromMediaId(mediaID,
                    builder.generate().getBundle());
        }
    }
}