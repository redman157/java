package com.android.music_player;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.music_player.interfaces.MediaBrowserProvider;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.SharedPrefsUtils;

public abstract class BaseActivity extends AppCompatActivity implements MediaBrowserProvider {
    private boolean serviceBound = false;
    private MediaService mediaService;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    public MediaBrowserConnection browserConnection;
    public abstract void onStartService();
    public abstract void onStopService();
    public abstract void initService();
    public abstract void initManager();
    public abstract void switchFragment(Fragment fragment);
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @see MediaControllerCompat#getMediaController(Activity)
     */
    public MediaControllerCompat getSupportMediaController() {
        return MediaControllerCompat.getMediaController(this);
    }

    /**
     * @see MediaControllerCompat#setMediaController(Activity, MediaControllerCompat)
     */
    public void setSupportMediaController(MediaControllerCompat mediaController) {
        MediaControllerCompat.setMediaController(this, mediaController);
    }
}