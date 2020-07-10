package com.android.music_player.services;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
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

import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MediaNotificationManager;
import com.android.music_player.managers.MediaPlayerManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.media.PlaybackInfoListener;
import com.android.music_player.media.PlayerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MediaService extends MediaBrowserServiceCompat {

//    private static final String TAG = MediaService.class.getSimpleName();
    private static final String TAG ="JJJ";
    private MediaSessionCompat mSessionCompat;
    private PlayerAdapter mPlayback;
    private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;
    private MediaManager mMediaManager;
    private boolean mServiceStarted;
    private boolean isAutoPlay = false;
    
    private BroadcastReceiver updateQueueItems = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
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
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(this);
        initMediaSession();
        if (mPlayback == null){
            mPlayback = new MediaPlayerManager(this, new MediaPlayerListener());
            mMediaNotificationManager = new MediaNotificationManager(this);
        }
        Log.d(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager");
    }

    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        mPlayback.stop();
        mSessionCompat.release();
        Log.d(TAG, "onDestroy: MediaPlayerManager stopped, and MediaSession released");
    }


    private void initMediaSession() {
        mSessionCompat = new MediaSessionCompat(this,getClass().getSimpleName());
        mSessionCompat.setMetadata(MusicLibrary.music.get(mMediaManager.getCurrentMusic()));
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
        // lúc connect sẽ chạy vô đây để check root
        return new BrowserRoot(MusicLibrary.MEDIA_ID_ROOT, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                                   @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        // service gọi lên MediaBrowserSubscriptionCallback
        // Use result.detach to allow calling result.sendResult from another thread
        // Assign returned result to temporary variable
        Log.d("WWW", "MediaService --- onLoadChildren: "+parentId);
        if (parentId.equals(MusicLibrary.MEDIA_ID_EMPTY_ROOT)) {
            result.sendResult(new ArrayList<MediaBrowserCompat.MediaItem>());
        }else if (parentId.equals(MusicLibrary.MEDIA_ID_ROOT)) {
            result.sendResult(MusicLibrary.getAllMediaService());
        }
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerManager
    public class MediaSessionCallback extends MediaSessionCompat.Callback {
        private final List<MediaSessionCompat.QueueItem> mPlayList = new ArrayList<>();
        private List<MediaSessionCompat.QueueItem> mShuffleList = new ArrayList<>();
        private List<MediaSessionCompat.QueueItem> mMainList = new ArrayList<>();
        private MediaSessionCompat.QueueItem queueItem;
        private MediaMetadataCompat mPreparedMedia;
        private MediaManager mMediaManager = MediaManager.getInstance();

        public MediaSessionCallback(){
            mMediaManager.setContext(MediaService.this);
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
            Log.d("XXX","onAddQueueItem: "+ queueItem.getDescription().getMediaId());
            mPlayList.add(queueItem);
            mSessionCompat.setQueue(mPlayList);

            Log.d("XXX","mSessionCompat: "+mPlayList.size());
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
     /*       queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
              mPlayList.remove(queueItem);
              mSessionCompat.setQueue(mPlayList);*/
            mPlayList.clear();
            mSessionCompat.setQueue(mPlayList);
//
//            mSessionCompat.setQueue(mPlayList);

//            Log.d("XXX","onRemoveQueueItem: "+ queueItem.getDescription().getMediaId());
//            Log.d("XXX","mSessionCompat: "+mPlayList.size());
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            super.onPlayFromMediaId(mediaId, extras);
            Log.d("JJJ","MediaService ---onPlayFromMediaId: "+mediaId);

            if (mPreparedMedia == null) {
                // công đoan chuẩn bị từ media session
                onPrepareFromMediaId(mediaId, null);
            }else {
                // khởi động app play liền
                mPlayback.playFromMedia(mMediaManager.getMetadata(MediaService.this, mediaId));
                mPreparedMedia = null;
            }
            Log.d(TAG, "onPlayFromMediaId: MediaSession active");
        }

        @Override
        public void onPlay() {
            super.onPlay();
            onPlayFromMediaId(mMediaManager.getCurrentMusic(), null);
        }


        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle bundle) {
            setupMediaList();
            // set media current để chạy

            mPreparedMedia = mMediaManager.getMetadata(MediaService.this,
                   mediaId);
            mSessionCompat.setMetadata(mPreparedMedia);
            Log.d("JJJ", "MediaService --- onPrepareFromMediaId: "+mediaId);
            onPlayFromMediaId(mediaId, null);
            isActive();
        }

        private void isActive() {
            if (!mSessionCompat.isActive()) {
                mSessionCompat.setActive(true);
            }
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            if (repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE){
                mPlayback.setRepeat(false);
                mSessionCompat.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
            }else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
                mPlayback.setRepeat(true);
                mSessionCompat.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
            }
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            switch (shuffleMode){
                case PlaybackStateCompat.SHUFFLE_MODE_NONE:
                    mSessionCompat.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                    break;
                case PlaybackStateCompat.SHUFFLE_MODE_ALL:

                    mSessionCompat.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
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
            setupMediaList();
            int currentPos = 0;
            Log.d("ZZZ","mMainList.size(): "+mMainList.size());
            try {
                currentPos= MusicLibrary.getPosition(mMainList, mMediaManager.getCurrentMusic());
                currentPos = (currentPos + 1) % mMainList.size();
                Log.d("ZZZ",mMainList.get(currentPos).getDescription().getMediaId() + " --- "+currentPos);


            }catch (ArithmeticException e){
                Log.d("ZZZ", "onSkipToNext: "+e.getMessage());
//                currentPos = 0;
            }
            String mediaId = mMainList.get(currentPos).getDescription().getMediaId();
            // khi next muốn tự động phát hay k ?

            mPreparedMedia = null;
            onPlayFromMediaId(mediaId, null);
        }

        @Override
        public void onSkipToPrevious() {
            setupMediaList();
            int currentPos;

            currentPos= MusicLibrary.getPosition(mMainList, mMediaManager.getCurrentMusic());
            currentPos = currentPos > 0 ? currentPos - 1 : mPlayList.size() - 1;

            String mediaId = mMainList.get(currentPos).getDescription().getMediaId();

            // khi next muốn tự động phát hay k ?
            mPreparedMedia = null;
            onPlayFromMediaId(mediaId, null);

        }

        private void setupMediaList() {
            if (mSessionCompat.getController().getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                mSessionCompat.setQueue(mPlayList);
            } else if (mSessionCompat.getController().getShuffleMode() == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                mShuffleList = mMediaManager.getShuffleQueue(mMediaManager.getMetadata(
                        MediaService.this, mMediaManager.getCurrentMusic()), mPlayList);
                mSessionCompat.setQueue(mShuffleList);
            }
            mMainList = mSessionCompat.getController().getQueue();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
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
        public void onPlaybackCompleted(boolean isCompleted) {
            if (isCompleted){
                mCallback.onSkipToNext();
            }else {
                mCallback.onPrepareFromMediaId(mMediaManager.getCurrentMusic(),  null);
            }
        }

        class ServiceManager{
            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Notification notification =
                        mMediaNotificationManager.getNotification(

                                mPlayback.getCurrentMedia(), state, getSessionToken());

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            MediaService.this,
                            new Intent(MediaService.this, MediaService.class));
                    mServiceInStartedState = true;
                }

                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                stopForeground(false);
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());
                mMediaNotificationManager.getNotificationManager()
                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }
    }
}
