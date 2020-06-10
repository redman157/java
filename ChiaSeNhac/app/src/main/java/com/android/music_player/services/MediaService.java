package com.android.music_player.services;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.NotificationManager;
import com.android.music_player.managers.MediaPlayerManager;
import com.android.music_player.media.PlaybackInfoListener;
import com.android.music_player.media.PlayerAdapter;
import com.android.music_player.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaService extends MediaBrowserServiceCompat {

//    private static final String TAG = MediaService.class.getSimpleName();
    private static final String TAG ="JJJ";
    private MediaSessionCompat mSessionCompat;
    private PlayerAdapter mPlayback;
    private NotificationManager mNotificationManager;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;
    private MediaManager mMediaManager;
    private boolean mServiceStarted;
    private boolean isAutoPlay = false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY ;
    }

    /**
     * Khởi tạo service và truyền intent xuống
     **/
    @Override
    public void onCreate() {
        super.onCreate();
        // Create a new MediaSession.
        initMediaSession();

        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(this);

        if (mPlayback == null){
            mPlayback = new MediaPlayerManager(this, new MediaPlayerListener());
            mNotificationManager = new NotificationManager(this);
        }


        Log.d(TAG, "onCreate: MusicService creating MediaSession, and NotificationManager");
    }

    @Override
    public void onDestroy() {
        mNotificationManager.onDestroy();
        mPlayback.stop();
        mSessionCompat.release();
        Log.d(TAG, "onDestroy: MediaPlayerManager stopped, and MediaSession released");
    }


    private void initMediaSession() {
        mSessionCompat = new MediaSessionCompat(this,getClass().getSimpleName());
        mCallback = new MediaSessionCallback();

        mSessionCompat.setCallback(mCallback);
        mSessionCompat.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSessionCompat.getSessionToken());
    }

    @Override
    public void onCustomAction(@NonNull String action, Bundle extras, @NonNull Result<Bundle> result) {
        super.onCustomAction(action, extras, result);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /******* ---------------------------------------------------------------
     Defaults
     ----------------------------------------------------------------*******/
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.

        Log.d("WWW","MediaService --- onGetRoot --- clientPackageName: "+clientPackageName);
        Log.d("WWW","MediaService --- onGetRoot --- clientUid: "+clientUid);
        Log.d("WWW","MediaService --- onGetRoot --- rootHints: "+rootHints);
        return new BrowserRoot(MusicLibrary.getRoot(), null);
    }



    @Override
    public void onLoadChildren(@NonNull String parentId,
                                   @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        // service gọi lên MediaBrowserSubscriptionCallback
        // Use result.detach to allow calling result.sendResult from another thread
        // Assign returned result to temporary variable
        result.sendResult(MusicLibrary.getMediaItems());

    }

    // MediaSession Callback: Transport Controls -> MediaPlayerManager
    public class MediaSessionCallback extends MediaSessionCompat.Callback{
        private final List<MediaSessionCompat.QueueItem> mPlaylist = new ArrayList<>();
        private MediaSessionCompat.QueueItem queueItem;
        private int position = -1;
        private MediaMetadataCompat mPreparedMedia;
        private MediaManager mMediaManager = MediaManager.getInstance();

        public MediaSessionCallback(){
            mMediaManager.setContext(MediaService.this);
        }


        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
            mPlaylist.add(queueItem);
            mSessionCompat.setQueue(mPlaylist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
            mPlaylist.remove(queueItem);
            mSessionCompat.setQueue(mPlaylist);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            Log.d("JJJ","MediaService ---onPlayFromMediaId: "+mediaId);

            if (!isReadyToPlay()) {
                // Nothing to play.
                return;
            }
            if (mPreparedMedia == null) {
                onPrepareFromMediaId(mediaId, extras);
            }

            mPlayback.playFromMedia(MusicLibrary.getMetadata(MediaService.this,mediaId));

            Log.d(TAG, "onPlayFromMediaId: MediaSession active");
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            if (mPlaylist.isEmpty()) {
                // Nothing to play.
                return;
            }
            position = MusicLibrary.getPosition(mediaId);

            mPreparedMedia = MusicLibrary.getMetadata(MediaService.this,
                    mPlaylist.get(position).getDescription().getMediaId());
            Log.d("CCC", "MediaService --- onPrepareFromMediaId --- pos: "+position +" --- " +
                    "mediaId: "+mPreparedMedia.getString(Constants.METADATA.Title));
            mSessionCompat.setMetadata(mPreparedMedia);
            Log.d("JJJ", "MediaService --- onPrepareFromMediaId: "+mediaId);
            if (extras != null) {
                isAutoPlay = extras.getBoolean(Constants.INTENT.AUTO_PLAY);
                if (isAutoPlay){
                    onPlayFromMediaId(mediaId, null);
                }
            }
            isActive();
        }

        private void isActive() {
            if (!mSessionCompat.isActive()) {
                mSessionCompat.setActive(true);
            }
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            if (repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE){

            }else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL){

            }else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {

            }
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            switch (shuffleMode){
                case PlaybackStateCompat.SHUFFLE_MODE_ALL:
                    ArrayList<MediaSessionCompat.QueueItem> mShuffle = new ArrayList<>(mPlaylist);
                    Collections.shuffle(mShuffle);
                    MediaSessionCompat.QueueItem mCurrentQueue =
                            new MediaSessionCompat.QueueItem(mPlayback.getCurrentMedia().getDescription(), mPlayback.getCurrentMedia().getDescription().hashCode());
                    mShuffle.remove(mCurrentQueue);
                    for (int i = 0 ; i < mShuffle.size(); i ++){
                        if (mShuffle.get(i).getDescription().getMediaId().equals(
                                mPlayback.getCurrentMedia().getDescription().getMediaId())){
                            mShuffle.add(0, mCurrentQueue);
                            break;
                        }
                    }
                    mSessionCompat.setQueue(mShuffle);

                    Log.d("KKK",
                            "SHUFFLE_MODE_ALL: "+ mShuffle.get(0).getDescription().getMediaId());
                    break;
                case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                    mSessionCompat.setQueue(mPlaylist);
                    Log.d("KKK",
                            "SHUFFLE_MODE_NONE: "+ mSessionCompat.getController().getQueue().get(0).getDescription().getMediaId());
                    break;
            }
        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSessionCompat.setActive(false);
        }

        @Override
        public void onSkipToNext() {
            position = (position + 1) % mPlaylist.size();
            String mediaId = mPlaylist.get(position).getDescription().getMediaId();
            // bundle update trạng thái auto play và position của nó
            mPreparedMedia = null;
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.INTENT.AUTO_PLAY, true);
            onPlayFromMediaId(mediaId, bundle );
            Log.d("JJJ", "MediaService --- onSkipToNext: "+(mPreparedMedia.getString(MediaMetadataCompat.METADATA_KEY_TITLE)));
        }
        @Override
        public void onSkipToPrevious() {
            position = position > 0 ? position - 1 : mPlaylist.size() - 1;
            mPreparedMedia = null;
            String mediaId = mPlaylist.get(position).getDescription().getMediaId();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.INTENT.AUTO_PLAY, true);
            onPlayFromMediaId(mediaId, bundle );
            Log.d("JJJ", "MediaService --- onSkipToPrevious: "+(mPreparedMedia.getString(MediaMetadataCompat.METADATA_KEY_TITLE)));
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        private boolean isReadyToPlay() {
            return (!mPlaylist.isEmpty());
        }
    }

    // MediaPlayerManager Callback: MediaPlayerManager state -> MediaService.
    public class MediaPlayerListener extends PlaybackInfoListener{
        private final ServiceManager mServiceManager;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }
        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            mSessionCompat.setPlaybackState(state);

            Log.d("SSS", "MediaService --- onPlaybackStateChange: " + state.getState());
            // Manage the started state of this service.
            switch (state.getState()){
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(state);
                    break;

            }
        }

        @Override
        public void onPlaybackCompleted(boolean isNext) {
            if (isNext){
                mCallback.onSkipToNext();
            }
        }

        class ServiceManager{
            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Notification notification =
                        mNotificationManager.getNotification(

                                mPlayback.getCurrentMedia(), state, getSessionToken());

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            MediaService.this,
                            new Intent(MediaService.this, MediaService.class));
                    mServiceInStartedState = true;
                }

                startForeground(NotificationManager.NOTIFICATION_ID, notification);
            }



            private void updateNotificationForPause(PlaybackStateCompat state) {
                stopForeground(false);
                Notification notification =
                        mNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());
                mNotificationManager.getNotificationManager()
                        .notify(NotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }
    }
}
