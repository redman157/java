package com.android.music_player.activities;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.QueueManager;
import com.android.music_player.media.BrowserConnectionListener;
import com.android.music_player.media.BrowserHelper;
import com.android.music_player.media.MediaBrowserCallBack;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.BundleHelper;
import com.android.music_player.utils.SharedPrefsUtils;

public abstract class BaseActivity extends ActionBarCastActivity implements BrowserConnectionListener.OnServiceConnect {
    private boolean serviceBound = false;
    private MediaService mediaService;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    public MediaControllerCompat mMediaControllerCompat;
    public BrowserHelper mBrowserHelper;
    private MediaBrowserCallBack mMediaBrowserCallBack;
    public abstract void initManager();
    private static final String TAG = BaseActivity.class.getSimpleName();
    private MediaBrowserCompat mMediaBrowserCompat;
    private QueueManager mQueueManager;
    public MediaManager getMediaManager(){
        return mMediaManager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(this);
        mQueueManager = QueueManager.getInstance(this);
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
    public void onResume() {
        super.onResume();
        Log.d("VVV","BaseActivity --- onResume: Enter");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBrowserHelper.onStop();
        Log.d("VVV","BaseActivity --- onStop: "+getIntent().getStringExtra("hehe"));
    }

    public void setMediaChange(String tag,
                               MediaBrowserCallBack.OnChangeMusicListener onChangeMusicListener){
        if (mMediaBrowserCallBack == null) {
            mMediaBrowserCallBack = new MediaBrowserCallBack();
        }
        mMediaBrowserCallBack.setOnChangeMusicListener(onChangeMusicListener);
        mBrowserHelper.registerCallback(tag, mMediaBrowserCallBack);
    }

    public MediaControllerCompat getControllerActivity() {
        return mMediaControllerCompat;
    }

    public MediaBrowserCompat getMediaBrowserCompat() {
        return mMediaBrowserCompat;
    }

    public void setMediaBrowserCompat(MediaBrowserCompat mediaBrowserCompat) {
        this.mMediaBrowserCompat = mediaBrowserCompat;
    }

    public QueueManager getQueueManager() {
        return mQueueManager;
    }


    @Override
    public void onConnect(final MediaControllerCompat mediaController) {
        // khi connect thành công của media browser
        // thì mới có controller chuyển cho activity sử dụng
        mMediaControllerCompat = mediaController;
        /*VIEW MODEL CHANGE ROOT SERVICE*/
        try {
            String parentId = mQueueManager.getParentId();
            if (parentId.equals(MusicLibrary.MEDIA_ID_ROOT)){
                // GỠ STATE VÀ SET STATE KHÁC
                mMediaManager.getMediaBrowserConnection().unSetSubscribe(MusicLibrary.MEDIA_ID_EMPTY_ROOT,
                        mMediaManager.getMediaBrowserConnection().getCallback());

                mMediaManager.getMediaBrowserConnection().setSubscribe(MusicLibrary.MEDIA_ID_ROOT,
                        mMediaManager.getMediaBrowserConnection().getCallback());
                Log.d("ZZZ",
                        "kích thước: "+parentId);
            }else if (parentId.equals(MusicLibrary.MEDIA_ID_EMPTY_ROOT)){
                mMediaManager.getMediaBrowserConnection().unSetSubscribe(MusicLibrary.MEDIA_ID_ROOT,
                        mMediaManager.getMediaBrowserConnection().getCallback());

                mMediaManager.getMediaBrowserConnection().setSubscribe(MusicLibrary.MEDIA_ID_EMPTY_ROOT,
                        mMediaManager.getMediaBrowserConnection().getCallback());
            }
        }catch (NullPointerException e){
            Log.d("VVV", "onConnect: "+e.getMessage());
        }
    }

}