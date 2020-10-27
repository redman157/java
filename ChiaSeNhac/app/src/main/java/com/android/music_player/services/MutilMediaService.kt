package com.android.music_player.services

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.android.music_player.managers.*
import com.android.music_player.media.PlaybackInfoListener
import com.android.music_player.media.PlayerAdapter
import com.android.music_player.utils.ChangeTheme
import java.util.*

class MutilMediaService: MediaBrowserServiceCompat() {
    //    private static final String TAG = MediaService.class.getSimpleName();
    private val TAG = "MediaService"
    private lateinit var mSessionCompat: MediaSessionCompat
    private lateinit var mPlayback: PlayerAdapter
    private lateinit var mMediaNotificationManager: MediaNotificationManager
    private lateinit var mCallback: MediaSessionCallback
    private var mServiceInStartedState = false
    private lateinit var mMediaManager: MediaManager
    private val mServiceStarted = false
    private val isAutoPlay = false
    private lateinit var mQueueManager: QueueManager
//    private final IBinder mIBinder = new LocalBinder();
//   public class LocalBinder extends Binder {
//        public MediaService getInstance() {
//            return MediaService.this;
//        }
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mIBinder;
//    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    /**
     * Khởi tạo service và truyền intent xuống
     */
    override fun onCreate() {
        super.onCreate()
        // Create a new MediaSession.
        initMediaSession()
    }

    override fun onDestroy() {
        mMediaNotificationManager.onDestroy()
        mPlayback.stop()
        mSessionCompat.release()
        Log.d(TAG, "onDestroy: MediaPlayerManager stopped, and MediaSession released")
    }


    private fun initMediaSession() {
        mMediaManager = MediaManager.getInstance().apply {
            context = this@MutilMediaService
        }

        mQueueManager = QueueManager.getInstance(this)
        mSessionCompat = MediaSessionCompat(this, javaClass.simpleName).apply {
            setMetadata(MusicLibrary.music[mMediaManager.currentMusic])
            mCallback = MediaSessionCallback()
            setCallback(mCallback)
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        }

        sessionToken = mSessionCompat.sessionToken
        mPlayback = MediaPlayerManager(this, MediaPlayerListener(this@MutilMediaService))
        mMediaNotificationManager = MediaNotificationManager(this@MutilMediaService)
        Log.d(TAG, (this.javaClass.simpleName + " --- onCreate: MusicService creating MediaSession, and " +
                "MediaNotificationManager"))
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    /******* ---------------------------------------------------------------
     * Defaults
     * ---------------------------------------------------------------- */
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        Log.d("WWW", "MediaService --- onGetRoot --- clientPackageName: $clientPackageName")
        Log.d("WWW", "MediaService --- onGetRoot --- clientUid: $clientUid")
        Log.d("WWW", "MediaService --- onGetRoot --- rootHints: $rootHints")
        // lúc connect sẽ chạy vô đây để check root
        return BrowserRoot(MusicLibrary.MEDIA_ID_ROOT, null)
    }

    override fun onLoadChildren(parentId: String,
                                result: Result<List<MediaBrowserCompat.MediaItem?>?>) {
        // service gọi lên MediaBrowserSubscriptionCallback
        // Use result.detach to allow calling result.sendResult from another thread
        // Assign returned result to temporary variable
        Log.d("WWW", "MediaService --- onLoadChildren: $parentId")
        if ((parentId == MusicLibrary.MEDIA_ID_EMPTY_ROOT)) {
            result.sendResult(ArrayList())
        } else if ((parentId == MusicLibrary.MEDIA_ID_ROOT)) {
            result.sendResult(MusicLibrary.getAllMediaService())
        }
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerManager
    private inner class MediaSessionCallback() : MediaSessionCompat.Callback() {
        private val mPlayList: MutableList<MediaSessionCompat.QueueItem> = ArrayList()
        private var mShuffleList: List<MediaSessionCompat.QueueItem> = ArrayList()
        private var mMainList: List<MediaSessionCompat.QueueItem> = ArrayList()
        private var queueItem: MediaSessionCompat.QueueItem? = null
        private var mPreparedMedia: MediaMetadataCompat? = null
        private val mMediaManager = MediaManager.getInstance()
        override fun onAddQueueItem(description: MediaDescriptionCompat) {
            queueItem = MediaSessionCompat.QueueItem(description, description.hashCode().toLong())
            Log.d("XXX", "onAddQueueItem: " + queueItem!!.description.mediaId)
            mPlayList.add(queueItem!!)
            mSessionCompat.setQueue(mPlayList)
            Log.d("XXX", "mSessionCompat: " + mPlayList.size)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
            /*       queueItem = new MediaSessionCompat.QueueItem(description, description.hashCode());
              mPlayList.remove(queueItem);
              mSessionCompat.setQueue(mPlayList);*/
            mPlayList.clear()
            mSessionCompat.setQueue(mPlayList)
            //
//            mSessionCompat.setQueue(mPlayList);

//            Log.d("XXX","onRemoveQueueItem: "+ queueItem.getDescription().getMediaId());
//            Log.d("XXX","mSessionCompat: "+mPlayList.size());
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            Log.d(TAG, "MediaService ---onPlayFromMediaId: $mediaId")
            if (mPreparedMedia == null) {
                // công đoan chuẩn bị từ media session
                onPrepareFromMediaId(mediaId, null)
            } else {
                // khởi động app play liền
                mPlayback.playFromMedia(mMediaManager.getMetadata(this@MutilMediaService, mediaId))
                mPreparedMedia = null
            }
            Log.d(TAG, "onPlayFromMediaId: MediaSession active")
        }

        override fun onPlay() {
            super.onPlay()
            onPlayFromMediaId(mMediaManager.currentMusic, null)
        }

        override fun onPrepareFromMediaId(mediaId: String, bundle: Bundle?) {
            setupMediaList()
            // set media current để chạy
            mPreparedMedia = mMediaManager.getMetadata(this@MutilMediaService,
                    mediaId)
            mSessionCompat.setMetadata(mPreparedMedia)
            Log.d(TAG, "MediaService --- onPrepareFromMediaId: $mediaId")
            onPlayFromMediaId(mediaId, null)
            isActive
        }

        private val isActive: Unit
            private get() {
                if (!mSessionCompat.isActive) {
                    mSessionCompat.isActive = true
                }
            }

        override fun onCustomAction(action: String, extras: Bundle) {
            super.onCustomAction(action, extras)
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            if (repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE) {
                mPlayback.setRepeat(false)
                mSessionCompat.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
            } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
                mPlayback.setRepeat(true)
                mSessionCompat.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
            }
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            when (shuffleMode) {
                PlaybackStateCompat.SHUFFLE_MODE_NONE -> mSessionCompat.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
                PlaybackStateCompat.SHUFFLE_MODE_ALL -> mSessionCompat.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
            }
        }

        override fun onPause() {
            mPlayback.pause()
        }

        override fun onStop() {
            mPlayback.stop()
            mSessionCompat.setActive(false)
        }

        override fun onSkipToNext() {
            setupMediaList()
            var currentPos = 0
            Log.d("ZZZ", "mMainList.size(): " + mMainList.size)
            try {
                currentPos = MusicLibrary.getPosition(mMainList, mMediaManager.currentMusic)
                currentPos = (currentPos + 1) % mMainList.size
                Log.d("ZZZ", mMainList[currentPos].description.mediaId + " --- " + currentPos)
                val mediaId = mMainList[currentPos].description.mediaId
                // khi next muốn tự động phát hay k ?
                mPreparedMedia = null
                onPlayFromMediaId((mediaId)!!, null)
            } catch (e: ArithmeticException) {
                Log.d("ZZZ", "onSkipToNext: " + e.message)
                //                currentPos = 0;
            }
        }

        override fun onSkipToPrevious() {
            setupMediaList()
            var currentPos: Int
            currentPos = MusicLibrary.getPosition(mMainList, mMediaManager.currentMusic)
            currentPos = if (currentPos > 0) currentPos - 1 else mPlayList.size - 1
            val mediaId = mMainList[currentPos].description.mediaId

            // khi next muốn tự động phát hay k ?
            mPreparedMedia = null
            onPlayFromMediaId((mediaId)!!, null)
        }

        private fun setupMediaList() {
            if (mSessionCompat.controller.shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                mSessionCompat.setQueue(mPlayList)
            } else if (mSessionCompat.controller.shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_ALL) {
                mShuffleList = mMediaManager.getShuffleQueue(mMediaManager.getMetadata(
                        this@MutilMediaService, mMediaManager.currentMusic), mPlayList)
                mSessionCompat.setQueue(mShuffleList)
            }
            mMainList = mSessionCompat.controller.queue
        }

        override fun onSeekTo(pos: Long) {
            mPlayback.seekTo(pos)
        }

        init {
            mMediaManager.context = this@MutilMediaService
        }
    }

    // MediaPlayerManager Callback: MediaPlayerManager state -> MediaService.
    private inner class MediaPlayerListener(mediaService: Service) : PlaybackInfoListener() {
        private val mServiceManager: ServiceManager = ServiceManager(mediaService as MediaService)
        override fun onPlaybackStateChange(state: PlaybackStateCompat) {
            // Report the state to the MediaSession.
            mSessionCompat.setPlaybackState(state)
            when (state.state) {
                PlaybackStateCompat.STATE_PLAYING -> mServiceManager.moveServiceToStartedState(state)
                PlaybackStateCompat.STATE_PAUSED -> mServiceManager.updateNotificationForPause(state)
                PlaybackStateCompat.STATE_STOPPED -> mServiceManager.moveServiceOutOfStartedState(state)
            }
        }

        override fun onPlaybackCompleted(isCompleted: Boolean) {
            if (isCompleted) {
                mCallback.onSkipToNext()
            } else {
                mCallback.onPrepareFromMediaId(mMediaManager.currentMusic, null)
            }
        }

    }

    inner class ServiceManager(private val mService: Service) {
        fun moveServiceToStartedState(state: PlaybackStateCompat) {
            mMediaNotificationManager.setAccentColor(ChangeTheme.getAccent(mService))
            if (!mServiceInStartedState) {
                ContextCompat.startForegroundService(
                        this@MutilMediaService,
                        Intent(this@MutilMediaService, MediaService::class.java))
                mServiceInStartedState = true
            }
            startForeground(MediaNotificationManager.NOTIFICATION_ID, mMediaNotificationManager.getNotification(
                    mPlayback.currentMedia, state, sessionToken))
        }

        fun updateNotificationForPause(state: PlaybackStateCompat) {
            mMediaNotificationManager.setAccentColor(ChangeTheme.getAccent(mService))
            stopForeground(false)
            mMediaNotificationManager.notificationManager
                    .notify(MediaNotificationManager.NOTIFICATION_ID, mMediaNotificationManager.getNotification(
                            mPlayback.currentMedia, state, sessionToken))
        }

        fun moveServiceOutOfStartedState(state: PlaybackStateCompat) {
            stopForeground(true)
            stopSelf()
            mServiceInStartedState = false
        }
    }

}