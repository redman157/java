package com.droidheat.musicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.services.MediaPlayerService;

public abstract class BaseActivity extends AppCompatActivity {
    private boolean serviceBound = false;
    private MediaPlayerService mediaPlayerService;
    private SharedPrefsManager mSharedPrefsManager;
    private SongManager mSongManager;

    @Override
    protected void onStart() {
        super.onStart();
        mSharedPrefsManager = new SharedPrefsManager();
        mSongManager = SongManager.getInstance();

        mSharedPrefsManager.setContext(this);
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
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mediaPlayerService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
}