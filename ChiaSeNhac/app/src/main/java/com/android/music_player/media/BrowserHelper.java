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

import com.android.music_player.managers.QueueManager;
import com.android.music_player.services.MediaService;
import com.android.music_player.services.MutilMediaService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for a MediaBrowser that handles connecting, disconnecting,
 * and basic browsing with simplified callbacks.
 */
public abstract class BrowserHelper {
//    private static final String TAG = BrowserHelper.class.getSimpleName();
    private static final String TAG = "JJJ";
    private Context mContext;
    private final Class<? extends MediaService> mMediaBrowserService;

    private final Map<String, MediaControllerCompat.Callback> mCallbackList = new HashMap<>();

    private MediaControllerCompat mMediaController;

    private final MediaBrowserConnectionCallback mMediaBrowserConnectionCallback;
    private final MediaControllerCallback mMediaControllerCallback;
    private QueueManager mQueueManager;
    private MediaBrowserCompat mMediaBrowser;
    private MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;
    public BrowserHelper(Context mContext,
                         Class<? extends MediaService> mMediaBrowserService) {
        // thực hiện công việc kết nối từ activity tới service
        this.mContext = mContext;
        this.mMediaBrowserService = mMediaBrowserService;

        mMediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        mMediaControllerCallback = new MediaControllerCallback();
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
        mQueueManager = QueueManager.getInstance(mContext);
    }

    /**
     * start service media browerser
     * */
    public void onStart(){
        if (mMediaBrowser == null){
            mMediaBrowser = new MediaBrowserCompat
                    (mContext,
                    new ComponentName(mContext, mMediaBrowserService),
                    mMediaBrowserConnectionCallback,
                            null);
            mMediaBrowser.connect();
            Log.d(TAG, "BrowserHelper --- onStart: Creating MediaBrowser, and connecting");
        } else {
//            if (mMediaBrowser.isConnected()){
//                mMediaBrowser.disconnect();
//                mMediaBrowser.connect();
//                Log.d(TAG, "BrowserHelper --- isConnected: true");
//            }else {
//                Log.d(TAG, "BrowserHelper --- isConnected: false");
//            }

        }
    }

    public void onStop(){
        Log.d(TAG, "BrowserHelper --- onStop --- mMediaController enter");
        if (mMediaController != null){
            mMediaController.unregisterCallback(mMediaControllerCallback);
            mMediaController = null;

        }else {
            Log.d(TAG, "BrowserHelper --- onStop --- mMediaController là null");
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.unsubscribe(mQueueManager.getParentId(), mMediaBrowserSubscriptionCallback);
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
            Log.d(TAG, "BrowserHelper --- onStop --- mMediaBrowser là null");
        }else {
            Log.d(TAG, "BrowserHelper --- onStop ---mMediaBrowser null");
        }
        resetState();
        Log.d(TAG, "BrowserHelper --- onStop: Releasing MediaController, Disconnecting from " +
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

    protected void unSetSubscribe(String parentID,
                                MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback){
        mMediaBrowser.unsubscribe(parentID, mediaBrowserSubscriptionCallback);
    }

    protected void setSubscribe(String parentID, MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback){
        mMediaBrowser.subscribe(parentID, mediaBrowserSubscriptionCallback);
    }

    /**
     * Called after loading a browserable {@link MediaBrowserCompat.MediaItem}
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
    public final MediaControllerCompat getMediaController() {
        if (mMediaController == null) {
            Log.d(TAG,"BrowserHelper --- MediaController is null!" );
            throw new IllegalStateException("BrowserHelper --- MediaController is null!");
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
        Log.d(TAG, "BrowserHelper --- resetState: Enter");
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        if (mMediaController == null) {
            Log.d(TAG, "BrowserHelper --- getTransportControls: MediaController is null!");
            throw new IllegalStateException("MediaController is null!");
        }
        return mMediaController.getTransportControls();
    }

    public MediaMetadataCompat getMetadata(){
        if (mMediaController == null) {
            Log.d(TAG, "BrowserHelper --- getTransportControls: MediaController is null!");
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
                    Log.d(TAG,
                            "BrowserHelper --- registerCallback --- musicID: " + metadata.getDescription().getMediaId());
                    callback.onMetadataChanged(metadata);
                }

                final PlaybackStateCompat playbackState = mMediaController.getPlaybackState();

                if (playbackState != null) {
                    Log.d(TAG,
                            "BrowserHelper --- registerCallback --- state:" + playbackState.getState());
                    callback.onPlaybackStateChanged(playbackState);
                }
            }else {
                Log.d(TAG,
                        "BrowserHelper --- registerCallback --- mMediaController: null");
            }
        }
    }

    // Receives callbacks from the MediaBrowser when it has successfully connected to the
    // MediaBrowserService (MusicService).
    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback  {
        // Happens as a result of onStart().

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            Log.d(TAG,"BrowserHelper --- onConnectionSuspended: enter");
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            Log.d(TAG,"BrowserHelper --- onConnectFailded: "+(mMediaBrowser == null? "null":
                    "khac null"));
           /* Log.d(TAG","BrowserHelper --- onConnectionFailed: "+(mMediaBrowser.isConnected() ?
                    "connect":"k connect"));*/
        }

        @Override
        public void onConnected() {
            try {
                // Get a MediaController for the MediaSession.
                mMediaController = new MediaControllerCompat(mContext, mMediaBrowser.getSessionToken());
                mMediaController.registerCallback(mMediaControllerCallback);

                // Sync existing MediaSession state to the UI.

                if (mMediaController.getMetadata() != null) {
                    mMediaControllerCallback.onMetadataChanged(mMediaController.getMetadata());
                    mMediaControllerCallback.onPlaybackStateChanged(mMediaController.getPlaybackState());

                    BrowserHelper.this.onConnected(mMediaController);
                }

                Log.d(TAG,
                        "MediaBrowserConnectionCallback --- check null MeidaController: "+(mMediaController == null ? "null":"khac null"));

            }catch (AndroidException e) {
                Log.d(TAG, String.format("onConnected: Problem: %s", e.toString()));
                Log.d("BBB", "MediaBrowserConnectionCallback --- exception: "+e.getMessage());
                throw new RuntimeException(e);
            }
            // truyền xuống service ParrentID thay đổi
//            mMediaBrowser.subscribe(MusicLibrary.getRoot(), mMediaBrowserSubscriptionCallback);
        }
    }

    // Receives callbacks from the MediaBrowser when the MediaBrowserService has loaded new media
    // that is ready for playback.
    public class MediaBrowserSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {
        // tu service gọi lên từ onChildrenLoaded
        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            Log.d(TAG,"MediaBrowserSubscriptionCallback --- onChildrenLoaded: "+parentId);

            BrowserHelper.this.onChildrenLoaded(parentId, children);
        }

        @Override
        public void onError(@NonNull String parentId) {
            Log.d(TAG, "BrowserHelper --- MediaBrowserSubscriptionCallback: "+parentId);
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
                    // set metadata default when to on app
                    callback.onMetadataChanged(metadata);
                }
            });
        }

        @Override
        public void onPlaybackStateChanged(@Nullable final PlaybackStateCompat state) {
            performOnAllCallbacks(new CallbackCommand() {
                @Override
                public void perform(@NonNull MediaControllerCompat.Callback callback) {
                    // set state default when to on app
                    callback.onPlaybackStateChanged(state);
                }
            });
        }

        // This might happen if the MusicService is killed while the Activity is in the
        // foreground and onStart() has been called (but not onStop()).
        @Override
        public void onSessionDestroyed() {
            Log.d(TAG,this.getClass().getSimpleName() + " --- onSessionDestroyed : Enter");
            resetState();
            onPlaybackStateChanged(null);
            BrowserHelper.this.onDisconnected();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
            Log.d("DDD", this.getClass().getSimpleName()+"--- "+queue.size());
        }
    }


}
