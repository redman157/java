package company.ai.musicplayer.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcelable
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import company.ai.musicplayer.R
import company.ai.musicplayer.extensions.toSavedMusic
import company.ai.musicplayer.extensions.toToast
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.player.MediaPlayerHolder
import company.ai.musicplayer.player.MusicNotificationManager

private const val WAKELOCK_MILLI: Long = 25000
class PlayerService: Service() {
    // Binder given to clients
    private val binder = LocalBinder()

    // notification music
    public lateinit var musicNotificationManager: MusicNotificationManager

    // Media player
    lateinit var mediaPlayerHolder: MediaPlayerHolder

    // WakeLock
    private lateinit var mWakeLock: PowerManager.WakeLock

    // Check if is already running
    var isRunning = false
    var isRestoredFromPause = false

    private lateinit var mMediaSessionCompat: MediaSessionCompat

    private val mMediaSessionCallback = object : MediaSessionCompat.Callback() {

        override fun onSeekTo(pos: Long) {
            mediaPlayerHolder.seekTo(
                pos.toInt(),
                updatePlaybackStatus = true,
                restoreProgressCallBack = false
            )
        }

        override fun onMediaButtonEvent(mediaButtonEvent: Intent?) =
            handleMediaIntent(mediaButtonEvent)
    }


    override fun onBind(intent: Intent): IBinder {
        if(!::mediaPlayerHolder.isInitialized){
            mediaPlayerHolder = MediaPlayerHolder(this).apply {
                registerActionsReceiver()
            }
            musicNotificationManager = MusicNotificationManager(this)
        }
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /*This mode makes sense for things that will be explicitly started
        and stopped to run for arbitrary periods of time, such as a service
        performing background music playback.*/
        isRunning = true
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayerHolder.isInitialized && mediaPlayerHolder.isCurrentSong){
            mediaPlayerHolder.apply {
                currentSong.first?.let {musicToSave ->
                    mPreferences.latestPlayedSong =
                        musicToSave.toSavedMusic(playerPosition, launchedBy)
                }
            }
            mPreferences.latestVolume = mediaPlayerHolder.currentVolumeInPercent
            if (::mMediaSessionCompat.isInitialized && mMediaSessionCompat.isActive){
                mMediaSessionCompat.isActive = false
                mMediaSessionCompat.setCallback(null)
                mMediaSessionCompat.release()
            }
            mediaPlayerHolder.release()
            if (::mWakeLock.isInitialized && mWakeLock.isHeld) {
                mWakeLock.release()
            }
            isRunning = false
        }
    }

    fun acquireWakeLock() {
        if (::mWakeLock.isInitialized) {
            mWakeLock.acquire(WAKELOCK_MILLI)
        }
    }

    fun getMediaSession(): MediaSessionCompat {
        return mMediaSessionCompat
    }

    override fun onCreate() {
        super.onCreate()
        if (!::mWakeLock.isInitialized){
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, javaClass.name)
            mWakeLock.setReferenceCounted(false)
        }
        configureMediaSession()
    }

    private fun configureMediaSession(){
        mMediaSessionCompat = MediaSessionCompat(this, packageName).apply {
            isActive = true
            setCallback(mMediaSessionCallback)
        }
    }

    private fun handleMediaIntent(intent: Intent?): Boolean{
        var isSuccess = false
        try {
            intent?.let {
                val event = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_KEY_EVENT) as KeyEvent
                if (event.action == KeyEvent.ACTION_DOWN){
                    when (event.keyCode){
                        KeyEvent.KEYCODE_MEDIA_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_HEADSETHOOK -> {
                            mediaPlayerHolder.resumeOrPause()
                            isSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_CLOSE, KeyEvent.KEYCODE_MEDIA_STOP -> {
                            mediaPlayerHolder.stopPlaybackService(stopPlayBack = true)
                            isSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            mediaPlayerHolder.skip(false)
                            isSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            mediaPlayerHolder.skip(true)
                            isSuccess = true
                        }
                        KeyEvent.KEYCODE_MEDIA_REWIND -> {
                            mediaPlayerHolder.repeatSong()
                            isSuccess = true
                        }
                    }
                }
            }
        }catch (e : Exception){
            isSuccess = false
            getString(R.string.error_media_buttons).toToast(this)
            e.printStackTrace()
        }
        return isSuccess
    }

    inner class LocalBinder : Binder() {
        // Return this instance of PlayerService so we can call public methods
        fun getService() = this@PlayerService
    }}