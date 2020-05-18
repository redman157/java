package com.android.music_player;

import android.content.ComponentName;
import android.media.AudioManager;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.music_player.services.MediaService;

public class BaseActivityExam extends AppCompatActivity {
    public MediaBrowser mMediaBrowser;
    public MediaController mMediaController;

    public MediaBrowserCompat mMediaBrowserCompat;
    public MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback(){
        @Override
        public void onConnected() {
            super.onConnected();
            MediaSessionCompat.Token token = MediaSessionCompat.Token.fromToken(mMediaBrowser.getSessionToken());

            try {
                MediaControllerCompat controllerCompat =
                        new MediaControllerCompat(BaseActivityExam.this, token);

                MediaControllerCompat.setMediaController(BaseActivityExam.this, controllerCompat);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Finish building the UI
//            buildTransportControls();
        }
        @Override
        public void onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        @Override
        public void onConnectionFailed() {
            // The Service has refused our connection
        }
    };

    private MediaControllerCompat mMediaControllerCompat;
    private MediaControllerCompat.Callback mCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMediaBrowserCompat = new MediaBrowserCompat(
                this,
                new ComponentName(this, MediaService.class),
                mConnectionCallback,
                null);
        Log.d("VVV", mMediaBrowserCompat.isConnected() + " ");
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMediaBrowserCompat.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (MediaControllerCompat.getMediaController(BaseActivityExam.this) != null) {
            MediaControllerCompat.getMediaController(BaseActivityExam.this).unregisterCallback(mCallback);
        }
        mMediaBrowserCompat.disconnect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mMediaBrowserCompat.disconnect();
    }


}
