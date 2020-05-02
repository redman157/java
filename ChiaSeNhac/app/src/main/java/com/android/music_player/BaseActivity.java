package com.android.music_player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.music_player.managers.SongManager;
import com.android.music_player.services.MediaPlayerService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;

public abstract class BaseActivity extends AppCompatActivity  {
    private boolean serviceBound = false;
    private MediaPlayerService mediaPlayerService;
    private SharedPrefsUtils mSharedPrefsUtils;
    private SongManager mSongManager;

    @Override
    protected void onStart() {
        super.onStart();
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        mSongManager = SongManager.getInstance();

        mSongManager.setContext(this);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent playerIntent = new Intent(getApplicationContext(), MediaPlayerService.class);
//        startService(playerIntent);
        if (!serviceBound) {
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //Binding this Client to the AudioPlayer Service
    public ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and getData LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mediaPlayerService = binder.getService();
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG,0);
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            serviceBound = false;
        }
    };
}