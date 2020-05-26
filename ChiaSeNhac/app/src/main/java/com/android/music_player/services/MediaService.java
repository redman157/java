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
import com.android.music_player.managers.MusicManager;
import com.android.music_player.media.MediaNotificationManager;
import com.android.music_player.media.MediaPlayerAdapter;
import com.android.music_player.media.PlaybackInfoListener;
import com.android.music_player.media.PlayerAdapter;
import com.android.music_player.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MediaService extends MediaBrowserServiceCompat {

//    private static final String TAG = MediaService.class.getSimpleName();
    private static final String TAG ="JJJ";
    private MediaSessionCompat mSession;
    private PlayerAdapter mPlayback;
    private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;
    private MusicManager mMusicManager;
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

        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(this);
        mMediaNotificationManager = new MediaNotificationManager(this);
        mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());

        Log.d(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager");
    }

    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        mPlayback.stop();
        mSession.release();
        Log.d(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

    private void initMediaSession() {
        mSession = new MediaSessionCompat(this,getClass().getSimpleName());
        mCallback = new MediaSessionCallback();

        mSession.setCallback(mCallback);
        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());
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
        return new BrowserRoot(MusicLibrary.getRoot(), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(MusicLibrary.getMediaItems());
    }


    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback{
        private final List<MediaSessionCompat.QueueItem> mPlaylist = new ArrayList<>();
        private MediaSessionCompat.QueueItem queueItem;
        private int position;
        private MediaMetadataCompat mPreparedMedia;

        private void setSessionActive() {
            if (!mServiceStarted) {
                startService(new Intent(getApplicationContext(), MediaService.class));
                mServiceStarted = true;
            }

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        public int getPosition() {
            return position;
        }


        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {

            queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
            mPlaylist.add(queueItem);

//            position = (position == - 1) ?  0: position;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
            mPlaylist.remove(queueItem);
//            position = (mPlaylist.isEmpty()) ? -1: position;
            mSession.setQueue(mPlaylist);
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
                Log.d("VVV", "MediaService --- mPlaylist.isEmpty Enter");
                return;
            }
            Log.d("VVV", "MediaService --- onPrepareFromMediaId: position=" + position);

            position = MusicLibrary.getPosition(mediaId);
            mPreparedMedia = MusicLibrary.getMetadata(MediaService.this, mediaId);
            Log.d("VVV", "MediaService --- onPrepareFromMediaId: "+ mPreparedMedia.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
            mSession.setMetadata(mPreparedMedia);
            Log.d("JJJ", "MediaService --- onPrepareFromMediaId: "+mediaId);
            if (extras != null) {
                isAutoPlay = extras.getBoolean(Constants.INTENT.AUTO_PLAY);
                if (isAutoPlay){
                    onPlayFromMediaId(mediaId, null);
                }
            }

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        @Override
        public void onPlay() {
            if (!isReadyToPlay()) {
                // Nothing to play.
                return;
            }
            Log.d("JJJ", "MediaService --- onPlay: Enter");
            if (mPreparedMedia == null) {
                onPrepare();
            }

            mPlayback.playFromMedia(mPreparedMedia);
//            Log.d(TAG, "MediaService ---onPlayFromMediaId: MediaSession active");
        }

        @Override
        public void onPrepare() {
            if (position < 0 && mPlaylist.isEmpty()) {
                // Nothing to play.
                return;
            }

            final String mediaId = mPlaylist.get(position).getDescription().getMediaId();
            Log.d("JJJ", "MediaService --- onPrepare: "+mediaId +" --- pos: "+position);
            mPreparedMedia = MusicLibrary.getMetadata(MediaService.this, mediaId);
            mSession.setMetadata(mPreparedMedia);

            setSessionActive();
           /* if (!mSession.isActive()) {
                mSession.setActive(true);
            }*/
        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSession.setActive(false);
        }

        @Override
        public void onSkipToNext() {
            position = (position + 1) % mPlaylist.size();
            String name = mPlaylist.get(position).getDescription().getMediaId();
            Log.d("JJJ", "MediaService --- onSkipToNext: " + name + ", position=" + position);
            mPreparedMedia = MusicLibrary.getMetadata(MediaService.this, name);
            Log.d("JJJ", "MediaService --- mPreparedMedia: "+(mPreparedMedia.getString(MediaMetadataCompat.METADATA_KEY_TITLE)));
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.INTENT.AUTO_PLAY, true);

            onPrepareFromMediaId(name, bundle);
        }


        @Override
        public void onSkipToPrevious() {
            position = position > 0 ? position - 1 : mPlaylist.size() - 1;
            String name = mPlaylist.get(position).getDescription().getMediaId();
            mPreparedMedia = MusicLibrary.getMetadata(MediaService.this, name);
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.INTENT.AUTO_PLAY, true);
            onPrepareFromMediaId(name, bundle);
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        private boolean isReadyToPlay() {
            return (!mPlaylist.isEmpty());
        }
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> MediaService.
    public class MediaPlayerListener extends PlaybackInfoListener{
        private final ServiceManager mServiceManager;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }
        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            mSession.setPlaybackState(state);

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
