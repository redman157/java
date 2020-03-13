package com.droidheat.musicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.droidheat.musicplayer.services.MusicPlayback;

public class PlayMusic {
    public interface CallBackListener{
        void getState(PlaybackStateCompat stateCompat);
        void getMetadataCompat(MediaMetadataCompat compat);
    }
    private CallBackListener callBackListener;
    public void setCallBack(CallBackListener callBackListener){
        this.callBackListener = callBackListener;
    }
    private String tag = "CCC";
    private MediaBrowserCompat mMediaBrowser;
    private Activity activity;


    private MediaMetadataCompat mMediaMetadataCompat;
    private PlaybackStateCompat state;


    private static PlayMusic instance;
    public static PlayMusic getInstance(){
        if (instance == null){
            instance = new PlayMusic();
        }
        return instance;
    }
    public PlayMusic(){
        // TODO something code
    }

    public void initMediaBrowser(){

        mMediaBrowser = new MediaBrowserCompat(activity,
                new ComponentName(activity, MusicPlayback.class), mConnectionCallback, null);
    }
    public MediaBrowserCompat.ConnectionCallback mConnectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {

                    Log.d(tag, "onConnected");
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                        ContextCompat.startForegroundService(getActivity(),
                                new Intent(activity, MusicPlayback.class));
                    } catch (RemoteException e) {
                        Log.d(tag, e.getMessage());
                        Log.d(tag, "could not connect media controller");
                    }
                }

                @Override
                public void onConnectionFailed() {
                    Log.d(tag, "onConnectionFailed");
                    super.onConnectionFailed();

                }

                @Override
                public void onConnectionSuspended() {
                    Log.d(tag,"onConnectionSuspended");
                    super.onConnectionSuspended();
                }
            }
        ;

    public void connectToSession(MediaSessionCompat.Token token) throws RemoteException {
        MediaControllerCompat mMediaController = new MediaControllerCompat(activity, token);
        if (mMediaController == null){
            return;
        }
        MediaControllerCompat.setMediaController(getActivity(), mMediaController);
        mMediaController.registerCallback(mCallback);
        PlaybackStateCompat state = mMediaController.getPlaybackState();
        callBackListener.getMetadataCompat(mMediaController.getMetadata());
        callBackListener.getState(state);
        /*setMediaMetadataCompat(mMediaController.getMetadata());
        setState(state);*/
    }

    public MediaControllerCompat.Callback mCallback = new MediaControllerCompat.Callback() {
            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                Log.d(tag, "onPlayBackStateChanged" + state);
                callBackListener.getState(state);
            }

            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                if (metadata != null) {
                    Log.d(tag, "onMediaMetadataCompat: On" );
                    callBackListener.getMetadataCompat(metadata);
                }
                Log.d(tag, "onMediaMetadataCompat: Off" );
            }
    };
    public void disconnect(){
        if (mMediaBrowser != null) {
            mMediaBrowser.disconnect();
            Log.d(tag, "disconnecting from MediaSession");
        }
        if (MediaControllerCompat.getMediaController(activity) != null) {
            MediaControllerCompat.getMediaController(activity).unregisterCallback(mCallback);
        }
    }

    public void connect(){
        if (mMediaBrowser != null) {
            mMediaBrowser.connect();
            Log.d(tag, "connecting to MediaSession");
        }
    }

    public PlaybackStateCompat getState() {
        return state;
    }

    public void setState(PlaybackStateCompat state) {
        this.state = state;
    }

    public MediaMetadataCompat getMediaMetadataCompat() {
        return mMediaMetadataCompat;
    }

    public void setMediaMetadataCompat(MediaMetadataCompat mMediaMetadataCompat) {
        this.mMediaMetadataCompat = mMediaMetadataCompat;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
