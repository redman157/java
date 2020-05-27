package com.android.music_player.media;

import android.content.ComponentName;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AndroidException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;

import com.android.music_player.services.MediaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for a MediaBrowser that handles connecting, disconnecting,
 * and basic browsing with simplified callbacks.
 */
public class MediaBrowserHelper {
//    private static final String TAG = MediaBrowserHelper.class.getSimpleName();
    private static final String TAG = "JJJ";
    private Context mContext;
    private final Class<? extends MediaBrowserServiceCompat> mMediaBrowserServiceClass;

    private final Map<String, MediaControllerCompat.Callback> mCallbackList = new HashMap<>();

    private MediaControllerCompat mMediaController;

    private final MediaBrowserConnectionCallback mMediaBrowserConnectionCallback;
    private final MediaControllerCallback mMediaControllerCallback;
    private final MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;

    private MediaBrowserCompat mMediaBrowser;
    public MediaBrowserHelper(Context mContext,
                              Class<? extends MediaBrowserServiceCompat> mMediaBrowserServiceClass) {
        // thực hiện công việc kết nối từ activity tới service

        this.mContext = mContext;
        this.mMediaBrowserServiceClass = mMediaBrowserServiceClass;

        mMediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        mMediaControllerCallback = new MediaControllerCallback();
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
    }

    public void onStart(){
        if (mMediaBrowser == null){
            mMediaBrowser = new MediaBrowserCompat
                    (mContext,
                    new ComponentName(mContext, mMediaBrowserServiceClass),
                    mMediaBrowserConnectionCallback,
                            null);

            mMediaBrowser.connect();
            Log.d(TAG, "MediaBrowserHelper --- onStart: Creating MediaBrowser, and connecting");
        } else {
            Log.d(TAG, "MediaBrowserHelper --- mMediaBrowser: khác null");
        }
    }

    public void onStop(){
        if (mMediaController != null){
            mMediaController.unregisterCallback(mMediaControllerCallback);
            mMediaController = null;
            Log.d(TAG, "MediaBrowserHelper --- mMediaController enter");
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
            Log.d(TAG, "MediaBrowserHelper --- mMediaBrowser enter");
        }
        resetState();
        Log.d(TAG, "MediaBrowserHelper --- onStop: Releasing MediaController, Disconnecting from " +
                "MediaBrowser");
    }

    private void performOnAllCallbacks(@NonNull CallbackCommand command) {
        for (MediaControllerCompat.Callback callback : mCallbackList.values()) {
            if (callback != null) {
                command.perform(callback);
            }
        }
    }
    /**
     * Helper for more easily performing operations on all listening clients.
     */
    private interface CallbackCommand {
        void perform(@NonNull MediaControllerCompat.Callback callback);
    }

    /**
     * Called after connecting with a {@link MediaBrowserServiceCompat}.
     * <p>
     * Override to perform processing after a connection is established.
     *
     * @param mediaController {@link MediaControllerCompat} associated with the connected
     *                        MediaSession.
     */

    protected void onConnected(@NonNull MediaControllerCompat mediaController) {
    }

    /**
     * Called after loading a browsable {@link MediaBrowserCompat.MediaItem}
     *
     * @param parentId The media ID of the parent item.
     * @param children List (possibly empty) of child items.
     */
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
    }

    /**
     * Called when the {@link MediaBrowserServiceCompat} connection is lost.
     */
    protected void onDisconnected() {
    }

    @NonNull
    protected final MediaControllerCompat getMediaController() {
        if (mMediaController == null) {
            Log.d(TAG,"MediaBrowserHelper --- MediaController is null!" );
            throw new IllegalStateException("MediaBrowserHelper --- MediaController is null!");
        }
        return mMediaController;
    }

    /**
     * The internal state of the app needs to revert to what it looks like when it started before
     * any connections to the {@link MediaService} happens via the {@link MediaSessionCompat}.
     */
    private void resetState(){
        performOnAllCallbacks(new CallbackCommand() {
            @Override
            public void perform(@NonNull MediaControllerCompat.Callback callback) {
                callback.onPlaybackStateChanged(null);
            }
        });
        Log.d(TAG, "MediaBrowserHelper --- resetState: Enter");
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        if (mMediaController == null) {
            Log.d(TAG, "MediaBrowserHelper --- getTransportControls: MediaController is null!");
            throw new IllegalStateException("MediaController is null!");
        }
        return mMediaController.getTransportControls();
    }

    public MediaMetadataCompat getMetadata(){
        if (mMediaController == null) {
            Log.d(TAG, "MediaBrowserHelper --- getTransportControls: MediaController is null!");
            throw new IllegalStateException("MediaController is null!");
        }
        return mMediaController.getMetadata();
    }

    public void registerCallback(String tag, MediaControllerCompat.Callback callback){
        if (callback != null){
            mCallbackList.put(tag, callback);

            // Update with the latest metadata/playback state.
            if (mMediaController != null) {
                final MediaMetadataCompat metadata = mMediaController.getMetadata();

                if (metadata != null) {
                    Log.d("JJJ",
                            "MediaBrowserHelper --- registerCallback --- musicID: " + metadata.getDescription().getMediaId());
                    callback.onMetadataChanged(metadata);
                }

                final PlaybackStateCompat playbackState = mMediaController.getPlaybackState();

                if (playbackState != null) {
                    Log.d("JJJ",
                            "MediaBrowserHelper --- registerCallback --- state:" + playbackState.getState());
                    callback.onPlaybackStateChanged(playbackState);
                }
            }else {
                Log.d("JJJ",
                        "MediaBrowserHelper --- registerCallback --- mMediaController: null");
            }
        }
    }

    // Receives callbacks from the MediaBrowser when it has successfully connected to the
    // MediaBrowserService (MusicService).
    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        // Happens as a result of onStart().

        @Override
        public void onConnected() {
            try {
                // Get a MediaController for the MediaSession.
                mMediaController = new MediaControllerCompat(mContext, mMediaBrowser.getSessionToken());
                mMediaController.registerCallback(mMediaControllerCallback);

                // Sync existing MediaSession state to the UI.
                mMediaControllerCallback.onMetadataChanged(mMediaController.getMetadata());
                mMediaControllerCallback.onPlaybackStateChanged(mMediaController.getPlaybackState());
                Log.d(TAG, "MediaBrowserConnectionCallback --- onConnected: enter");
                MediaBrowserHelper.this.onConnected(mMediaController);
            }catch (AndroidException e) {
                Log.d(TAG, String.format("onConnected: Problem: %s", e.toString()));
                Log.d("BBB", "MediaBrowserConnectionCallback --- exception: "+e.getMessage());
                throw new RuntimeException(e);
            }
            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mMediaBrowserSubscriptionCallback);
        }
    }

    // Receives callbacks from the MediaBrowser when the MediaBrowserService has loaded new media
    // that is ready for playback.
    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            MediaBrowserHelper.this.onChildrenLoaded(parentId, children);
        }
    }

    // Receives callbacks from the MediaController and updates the UI state,
    // i.e.: Which is the current item, whether it's playing or paused, etc.
    private class MediaControllerCallback extends MediaControllerCompat.Callback{
        @Override
        public void onMetadataChanged(final MediaMetadataCompat metadata) {
            performOnAllCallbacks(new CallbackCommand() {
                @Override
                public void perform(@NonNull MediaControllerCompat.Callback callback) {
                    callback.onMetadataChanged(metadata);
                }
            });
        }

        @Override
        public void onPlaybackStateChanged(@Nullable final PlaybackStateCompat state) {
            performOnAllCallbacks(new CallbackCommand() {
                @Override
                public void perform(@NonNull MediaControllerCompat.Callback callback) {
                    callback.onPlaybackStateChanged(state);
                }
            });
        }

        // This might happen if the MusicService is killed while the Activity is in the
        // foreground and onStart() has been called (but not onStop()).
        @Override
        public void onSessionDestroyed() {
            resetState();
            onPlaybackStateChanged(null);
            MediaBrowserHelper.this.onDisconnected();
        }

    }
}
