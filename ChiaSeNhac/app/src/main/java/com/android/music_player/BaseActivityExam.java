package com.android.music_player;

import android.content.ComponentName;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.music_player.services.MediaService;

public class BaseActivityExam extends AppCompatActivity {
    private MediaBrowser mMediaBrowser;
    private MediaController mMediaController;

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback(){
        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(BaseActivityExam.this, mMediaBrowserCompat.getSessionToken());
                mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                MediaControllerCompat.setMediaController(BaseActivityExam.this, mMediaControllerCompat);


            } catch( RemoteException e ) {

            }
        }
    };

    private MediaControllerCompat mMediaControllerCompat;
    private MediaControllerCompat.Callback mMediaControllerCompatCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaBrowserCompat = new MediaBrowserCompat(
                this,
                new ComponentName(this, MediaService.class),
                mMediaBrowserCompatConnectionCallback,
                getIntent().getExtras());
        if (mMediaBrowserCompat.isConnected()){
            mMediaBrowserCompat.disconnect();
        }else {
            mMediaBrowserCompat.connect();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaBrowserCompat.isConnected()){
            mMediaBrowserCompat.disconnect();
        }
    }


}
