package com.android.music_player;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.music_player.managers.MusicManager;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.SharedPrefsUtils;

public abstract class BaseActivity extends AppCompatActivity  {
    private boolean serviceBound = false;
    private MediaService mediaService;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MusicManager mMusicManager;

    @Override
    protected void onStart() {
        super.onStart();
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mMusicManager = MusicManager.getInstance();

        mMusicManager.setContext(this);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
//        if (serviceBound) {
//            unbindService(serviceConnection);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Intent playerIntent = new Intent(getApplicationContext(), MediaService.class);
////        startService(playerIntent);
//        if (!serviceBound) {
//            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Binding this Client to the AudioPlayer Service
    /*public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and getData LocalService instance
            MediaService.LocalBinder binder = (MediaService.LocalBinder) service;
            mediaService = binder.getService();
//            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG,0);
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            serviceBound = false;
        }
    };*/
}