package com.android.music_player;

import android.media.AudioManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.media.MediaBrowserHelper;
import com.android.music_player.media.MediaBrowserListener;

public class BaseActivityExam extends AppCompatActivity {
    private MediaBrowserHelper mMediaBrowserHelper;
    private MediaBrowserListener mBrowserListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mMediaBrowserHelper = new MediaBrowserConnection(this);
        mBrowserListener = new MediaBrowserListener();
        mMediaBrowserHelper.registerCallback(mBrowserListener);
    }



    @Override
    protected void onStart() {
        super.onStart();

        mMediaBrowserHelper.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mMediaBrowserHelper.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
