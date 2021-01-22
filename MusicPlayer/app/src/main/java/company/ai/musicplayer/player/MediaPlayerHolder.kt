package company.ai.musicplayer.player

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import company.ai.musicplayer.R
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.extensions.toContentUri
import company.ai.musicplayer.extensions.toToast
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.models.SavedEqualizerSettings
import company.ai.musicplayer.service.PlayerService
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.EqualizerHelper
import company.ai.musicplayer.utils.VersioningHelper
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.reflect.KProperty0

/**
 * Exposes the functionality of the [MediaPlayer]
 */

// The volume we set the media player to when we lose audio focus, but are
// allowed to reduce the volume instead of stopping playback.
private const val VOLUME_DUCK = 0.2f

// The volume we set the media player when we have audio focus.
private const val VOLUME_NORMAL = 1.0f

// We don't have audio focus, and can't duck (play at a low volume)
private const val AUDIO_NO_FOCUS_NO_DUCK = 0

// We don't have focus, but can duck (play at a low volume)
private const val AUDIO_NO_FOCUS_CAN_DUCK = 1

// We have full audio focus
private const val AUDIO_FOCUSED = 2

// The headset connection states (0,1)
private const val HEADSET_DISCONNECTED = 0
private const val HEADSET_CONNECTED = 1

class MediaPlayerHolder(private val playerService: PlayerService): MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private val mStateBuilder =
        PlaybackStateCompat.Builder().apply {
            setActions(PlaybackStateCompat.ACTION_SEEK_TO)
        }

    // Equalizer
    private lateinit var mEqualizer: Equalizer
    private lateinit var mBassBoost: BassBoost
    private lateinit var mVirtualizer: Virtualizer

    // Audio focus
    private var mAudioManager = playerService.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private lateinit var mAudioFocusRequestOreo: AudioFocusRequest
    private val mHandler = Handler(Looper.getMainLooper())

    private val sFocusEnabled get() = mPreferences.isFocusEnabled
    private var mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
    private var sPlayOnFocusGain = false

    private val mOnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange){
            AudioManager.AUDIOFOCUS_GAIN -> mCurrentAudioFocusState = AUDIO_FOCUSED
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK ->
                // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Lost audio focus, but will gain it back (shortly), so note whether
                // playback should resume
                mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
                sPlayOnFocusGain =
                    isMediaPlayer && state == Constants.PLAYING || state == Constants.RESUMED
            }
            AudioManager.AUDIOFOCUS_LOSS ->
                // Lost audio focus, probably "permanently"
                mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
        }
        // Update the player state based on the change
        if (isMediaPlayer) {
            configurePlayerState()
        }
    }
    var currentVolumeInPercent = mPreferences.latestVolume
    // save position media seekto
    val playerPosition
        get() = if (!isMediaPlayer) {
            mPreferences.latestPlayedSong?.startFrom!!
        } else {
            mediaPlayer.currentPosition
        }

    // Media player state/booleans
    val isPlaying get() = isMediaPlayer && mediaPlayer.isPlaying
    val isMediaPlayer get() = ::mediaPlayer.isInitialized

    private var sNotificationForeground = false

    val isCurrentSong get() = ::currentSong.isInitialized
    var isRepeat1X = false
    var isLooping = false


    private val mCurrentAlbumSize get() = mPlayingAlbumSongs?.size!! - 1
    private val mCurrentSongIndex get() = mPlayingAlbumSongs?.indexOf(currentSong.first)!!
    private val mNextSongIndex get() = mCurrentSongIndex + 1
    private val mPrevSongIndex get() = mCurrentSongIndex - 1
    private val mNextSong: Music?
        get() = when {
            mNextSongIndex <= mCurrentAlbumSize -> mPlayingAlbumSongs?.get(mNextSongIndex)
            isQueue -> stopQueueAndGetSkipSong(true)
            else -> mPlayingAlbumSongs?.get(0)
        }
    private val mPrevSong: Music?
        get() = when {
            mPrevSongIndex <= mCurrentAlbumSize && mPrevSongIndex != -1 -> mPlayingAlbumSongs?.get(
                mPrevSongIndex
            )
            isQueue -> stopQueueAndGetSkipSong(false)
            else -> mPlayingAlbumSongs?.get(mPlayingAlbumSongs?.lastIndex!!)
        }

    var isQueue = false
    var isQueueStarted = false

    private lateinit var preQueueSong: Pair<Music?, List<Music>?>

    var queueSongs = mutableListOf<Music>()

    var isSongRestoredFromPrefs = false
    var isSongFromLovedSongs = Pair(false, 0)

    var state = Constants.PAUSED
    var isPlay = false

    // Media player
    private lateinit var mediaPlayer: MediaPlayer
    private var mExecutor: ScheduledExecutorService? = null
    private var mSeekBarPositionUpdateTask: Runnable? = null

    // First: current song, second: isFromQueue
    lateinit var currentSong: Pair<Music?, Boolean>
    var launchedBy = Constants.ARTIST_VIEW
    private var isPlayingFromFolderPreQueue = Constants.ARTIST_VIEW
    private var mPlayingAlbumSongs: List<Music>? = null

    lateinit var mediaPlayerInterface: MediaPlayerInterface

    // Notifications
    private lateinit var mNotificationActionsReceiver: NotificationReceiver
    private val mMusicNotificationManager: MusicNotificationManager by lazy {
        playerService.musicNotificationManager
    }

    private fun startForeground(){
        if (!sNotificationForeground) {
            playerService.startForeground(
                Constants.NOTIFICATION_ID,
                mMusicNotificationManager.createNotification()
            )
            sNotificationForeground = true
        } else {
            mMusicNotificationManager.apply {
                updateNotificationContent()
                updatePlayPauseAction()
                updateRepeatIcon()
                updateNotification()
            }
        }
    }

    fun registerActionsReceiver() {
        mNotificationActionsReceiver = NotificationReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(Constants.REPEAT_ACTION)
            addAction(Constants.REWIND_ACTION)
            addAction(Constants.PREV_ACTION)
            addAction(Constants.PLAY_PAUSE_ACTION)
            addAction(Constants.NEXT_ACTION)
            addAction(Constants.FAST_FORWARD_ACTION)
            addAction(Constants.CLOSE_ACTION)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            addAction(Intent.ACTION_HEADSET_PLUG)
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }
        playerService.registerReceiver(mNotificationActionsReceiver, intentFilter)
    }

    private fun unregisterActionReceiver(){
        try {
            playerService.unregisterReceiver(mNotificationActionsReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    /**
     * create and setting equalizer music
     * */

    private fun createCustomEqualizer(){
        if (mediaPlayer.audioSessionId != AudioEffect.ERROR_BAD_VALUE && !::mEqualizer.isInitialized && !::mBassBoost.isInitialized && !::mVirtualizer.isInitialized){
            mBassBoost = BassBoost(0, mediaPlayer.audioSessionId)
            mVirtualizer = Virtualizer(0, mediaPlayer.audioSessionId)
            mEqualizer = Equalizer(0, mediaPlayer.audioSessionId)
            setEqualizerEnabled(false)
            restoreCustomEqSettings()
        }
    }

    fun getEqualizer() = Triple(mEqualizer, mBassBoost, mVirtualizer)

    private fun setEqualizerEnabled(isEnabled: Boolean){
        mEqualizer.enabled = isEnabled
        mBassBoost.enabled = isEnabled
        mVirtualizer.enabled = isEnabled
    }

    fun openEqualizer(activity: Activity){
        EqualizerHelper.openEqualizer(activity, mediaPlayer)
    }

//    fun openEqualizerCustom() = EqFragment.newInstance()

    private fun restoreCustomEqSettings(){
        mediaPlayer.apply {
            val savedEqualizerSettings = mPreferences.savedEqualizerSettings
            savedEqualizerSettings?.let { eqSettings ->

                setEqualizerEnabled(eqSettings.enabled)

                mEqualizer.usePreset(eqSettings.preset.toShort())

                val bandSettings = eqSettings.bandsSettings

                bandSettings?.iterator()?.withIndex()?.forEach {
                    mEqualizer.setBandLevel(it.index.toShort(), it.value.toInt().toShort())
                }

                mBassBoost.setStrength(eqSettings.bassBoost)
                mVirtualizer.setStrength(eqSettings.virtualizer)
            }
        }
    }

    fun setServiceCurrentSong(song: Music?, songs: List<Music>?, isFromQueue: Boolean, isFolderAlbum: String){
        launchedBy = isFolderAlbum
        currentSong = Pair(song, isFromQueue)
        mPlayingAlbumSongs = songs
    }


    private fun updateMediaSessionMetaData() {
        val mediaMediaPlayerCompat = MediaMetadataCompat.Builder().apply {
            putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                currentSong.first?.duration!!
            )
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, currentSong.first?.artist)
            putString(MediaMetadataCompat.METADATA_KEY_AUTHOR, currentSong.first?.artist)
            putString(MediaMetadataCompat.METADATA_KEY_COMPOSER, currentSong.first?.artist)
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, currentSong.first?.title)
            putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, currentSong.first?.title)
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, currentSong.first?.album)
            putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, currentSong.first?.album)
            putString(MediaMetadataCompat.METADATA_KEY_ALBUM, currentSong.first?.album)
            val bitmap = currentSong.first?.getAlbumArt(playerService)
            putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,bitmap )

            BitmapFactory.decodeResource(playerService.resources, R.drawable.ic_music_note)
                ?.let { bmp ->
                    putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bmp)
                }
        }
        playerService.getMediaSession().setMetadata(mediaMediaPlayerCompat.build())
    }

    /**
     * setup seekbar call back
     * */
    fun onRestartSeekBarCallback() {
        if (mExecutor == null) {
            startUpdatingCallbackWithPosition()
        }
    }

    fun onPauseSeekBarCallback() {
        stopUpdatingCallbackWithPosition()
    }

    fun onUpdateDefaultAlbumArt(bitmapRes: Bitmap) {
        mMusicNotificationManager.onUpdateDefaultAlbumArt(bitmapRes, isPlaying)
    }

    fun onHandleNotificationUpdate(isAdditionalActionsChanged: Boolean) {
        mMusicNotificationManager.onHandleNotificationUpdate(isAdditionalActionsChanged)
    }

    fun tryToGetAudioFocus(){
        mCurrentAudioFocusState = when (getAudioFocusResult()) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> AUDIO_FOCUSED
            else -> AUDIO_NO_FOCUS_NO_DUCK
        }
    }

    private fun getAudioFocusResult() = when {
        VersioningHelper.isOreoMR1() -> {
            mAudioFocusRequestOreo =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                    setAudioAttributes(AudioAttributes.Builder().run {
                        setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    })
                    setAcceptsDelayedFocusGain(true)
                    setOnAudioFocusChangeListener(mOnAudioFocusChangeListener, mHandler)
                    build()
                }
            mAudioManager.requestAudioFocus(mAudioFocusRequestOreo)
        }
        else ->{
            mAudioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    fun giveUpAudioFocus(){
        when {
            VersioningHelper.isOreo() -> if (::mAudioFocusRequestOreo.isInitialized) {
                mAudioManager.abandonAudioFocusRequest(
                    mAudioFocusRequestOreo
                )
            }
            else -> mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener)
        }
        mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK
    }

    fun resumeMediaPlayer() {
        if (!isPlaying) {
            if (isMediaPlayer) {
                if (sFocusEnabled) {
                    tryToGetAudioFocus()
                }
                mediaPlayer.start()
            }
            state = if (isSongRestoredFromPrefs) {
                isSongRestoredFromPrefs = false
                Constants.PLAYING
            } else {
                Constants.RESUMED
            }

            updatePlaybackStatus(true)

            startForeground()

            if (!isPlay) {
                isPlay = true
            }
        }
    }

    fun pauseMediaPlayer() {
        mediaPlayer.pause()
        playerService.stopForeground(false)
        sNotificationForeground = false
        state = Constants.PAUSED
        updatePlaybackStatus(true)
        mMusicNotificationManager.apply {
            updatePlayPauseAction()
            updateNotification()
        }
        mediaPlayerInterface.onFocusLoss()
    }

    fun repeatSong() {
        isRepeat1X = false
        mediaPlayer.setOnSeekCompleteListener { mp ->
            mp.setOnSeekCompleteListener(null)
            play()
        }
        mediaPlayer.seekTo(0)
    }

    private fun manageQueue(isNext: Boolean){
        if (isSongRestoredFromPrefs){
            isSongRestoredFromPrefs = false
        }
        when {
            isQueueStarted -> currentSong = Pair(getSkipSong(isNext), true)
            else -> {
                setServiceCurrentSong(
                    queueSongs[0],
                    queueSongs,
                    isFromQueue = true,
                    isFolderAlbum = Constants.ARTIST_VIEW)
                isQueueStarted = true
            }
        }
        initMediaPlayer(currentSong.first)
    }

    fun restorePreQueueSongs() {
        setServiceCurrentSong(preQueueSong.first, preQueueSong.second, false, isPlayingFromFolderPreQueue)
    }

    private fun getSkipSong(isNext: Boolean): Music?{
        if (isNext){
            if (mNextSong != null){
                return mNextSong
            }else if (isQueue){
                return stopQueueAndGetSkipSong(true)
            }
        }else {
            if (mPrevSong != null) {
                return mPrevSong
            } else if (isQueue) {
                return stopQueueAndGetSkipSong(false)
            }
        }
        return null
    }

    private fun stopQueueAndGetSkipSong(restorePreviousAlbum: Boolean)
            : Music? = if (restorePreviousAlbum) {
                setQueueEnabled(false)
                restorePreQueueSongs()
                getSkipSong(true)
                } else {
                    isQueueStarted = false
                    preQueueSong.first
                }


    /**
     * setup media controller play,pause, seekto, fastseek, previous, next
     * */

    private fun play() {
        mediaPlayer.start()
        state = Constants.PLAYING
        updatePlaybackStatus(true)
        startForeground()
    }

    fun onSaveEqualizerSettings(selectedPreset: Int, bassBoost: Short, virtualizer: Short) {
        mPreferences.savedEqualizerSettings = SavedEqualizerSettings(mEqualizer.enabled, selectedPreset, mEqualizer.properties.bandLevels.toList(), bassBoost, virtualizer)
    }


    fun seekTo(position: Int, updatePlaybackStatus: Boolean, restoreProgressCallBack: Boolean) {
        if (isMediaPlayer) {
            mediaPlayer.setOnSeekCompleteListener { mp ->
                mp.setOnSeekCompleteListener(null)
                if (restoreProgressCallBack) {
                    startUpdatingCallbackWithPosition()
                }
                if (updatePlaybackStatus) {
                    updatePlaybackStatus(!restoreProgressCallBack)
                }
            }
            mediaPlayer.seekTo(position)
        }
    }

    private fun updatePlaybackStatus(updateUI: Boolean){
        playerService.getMediaSession().setPlaybackState(
            mStateBuilder.setState(
                if (state == Constants.RESUMED){
                    Constants.PLAYING
                }else state,
                mediaPlayer.currentPosition.toLong(),
                1F
            ).build()
        )
        if (updateUI){
            mediaPlayerInterface.onStateChanged()
        }
    }

    fun release(){
        if (isMediaPlayer){
            EqualizerHelper.closeAudioEffectSession(
                playerService,
                mediaPlayer.audioSessionId
            )
            releaseCustomEqualizer()
            mediaPlayer.release()
            if (sFocusEnabled){
                giveUpAudioFocus()
            }
            stopUpdatingCallbackWithPosition()
        }
        unregisterActionReceiver()
    }

    private fun releaseCustomEqualizer(){
        if (::mEqualizer.isInitialized){
            mEqualizer.release()
            mBassBoost.release()
            mVirtualizer.release()
        }
    }

    fun resumeOrPause() {
        if (isPlaying) {
            pauseMediaPlayer()
        } else {
            resumeMediaPlayer()
        }
    }

    // setup repeat mode
    private fun getRepeatMode(){
        var message = R.string.repeat_enabled
        when{
            isRepeat1X -> {
                isRepeat1X = false
                isLooping = true
                message = R.string.repeat_loop_enabled
            }
            isLooping -> {
                isLooping = false
                message = R.string.repeat_disabled
            }
            else -> isRepeat1X = true
        }
        playerService.getString(message)
            .toToast(playerService)
    }

    fun setQueueEnabled(enabled: Boolean){
        isQueue = enabled
        isQueueStarted = false
        if (isQueue){
            preQueueSong = Pair(currentSong.first, mPlayingAlbumSongs)
            isPlayingFromFolderPreQueue = launchedBy
            isQueue = true
            mediaPlayerInterface.onQueueEnabled()
        }else{
            queueSongs.clear()
            mediaPlayerInterface.onQueueCleared()
            mediaPlayerInterface.onQueueStartedOrEnded(false)
        }
    }

    fun skip(isNext: Boolean){
        if (isQueue){
            manageQueue(isNext)
        }else{
            currentSong = Pair(getSkipSong(isNext), false)
            initMediaPlayer(currentSong.first)
        }
    }

    fun fastSeek(isForward: Boolean){
        var step: Int = mPreferences.fastSeekingStep * 1000
        if (isMediaPlayer){
            mediaPlayer.apply {
                var newPosition:Int = currentPosition
                if (isForward) {
                    newPosition += step
                } else {
                    newPosition -= step
                }
                seekTo(newPosition, updatePlaybackStatus = true, restoreProgressCallBack = false)
            }
        }
    }

    fun repeat(updatePlaybackStatus: Boolean){
        getRepeatMode()
        if (updatePlaybackStatus) updatePlaybackStatus(true)
        if (isPlaying) mMusicNotificationManager.updateRepeatIcon()
    }

    /**
     * Syncs the mMediaPlayer position with mPlaybackProgressCallback via recurring task.
     */

    private fun startUpdatingCallbackWithPosition(){
        if (mSeekBarPositionUpdateTask == null){
            mSeekBarPositionUpdateTask = Runnable { updateProgressCallbackTask() }
        }
        mExecutor = Executors.newSingleThreadScheduledExecutor()
        mExecutor?.scheduleAtFixedRate(mSeekBarPositionUpdateTask, 0, 1000, TimeUnit.MILLISECONDS)

    }

    // Reports media playback position to mPlaybackProgressCallback.
    private fun stopUpdatingCallbackWithPosition(){
        mExecutor?.shutdownNow()
        mExecutor = null
        mSeekBarPositionUpdateTask = null
    }

    private fun updateProgressCallbackTask() {
        if (isPlaying) {
            val currentPosition = mediaPlayer.currentPosition
            mediaPlayerInterface.onPositionChanged(currentPosition)
        }
    }

    fun instantReset() {
        if (isMediaPlayer && !isSongRestoredFromPrefs) {
            when {
                mediaPlayer.currentPosition < 5000 -> skip(false)
                else -> repeatSong()
            }
        } else {
            skip(false)
        }
    }


    /**
     * Once the [MediaPlayer] is released, it can't be used again, and another one has to be
     * created. In the onStop() method of the [MainActivity] the [MediaPlayer] is
     * released. Then in the onStart() of the [MainActivity] a new [MediaPlayer]
     * object has to be created. That's why this method is private, and called by load(int) and
     * not the constructor.
     */
    fun initMediaPlayer(song: Music?){
        try {
            if (isMediaPlayer){
                mediaPlayer.reset()
            }else{
                mediaPlayer = MediaPlayer().apply {
                    EqualizerHelper.openAudioEffectSession(
                        playerService.applicationContext,
                        audioSessionId
                    )

                    setOnPreparedListener(this@MediaPlayerHolder)
                    setOnCompletionListener(this@MediaPlayerHolder)
                    setOnErrorListener(this@MediaPlayerHolder)
                    setWakeMode(playerService, PowerManager.PARTIAL_WAKE_LOCK)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                }
                if (sFocusEnabled && isPlay) tryToGetAudioFocus()
                if (mPreferences.isPreciseVolumeEnabled) setPreciseVolume(currentVolumeInPercent)
            }
            song?.audioId.toContentUri()?.let { uri ->
                mediaPlayer.setDataSource(playerService, uri)
            }
            Log.d("BBB",this.javaClass.simpleName + ": ${song?.displayName}")
            mediaPlayer.prepare()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    /**
     * Reconfigures the player according to audio focus settings and starts/restarts it. This method
     * starts/restarts the MediaPlayer instance respecting the current audio focus state. So if we
     * have focus, it will play normally; if we don't have focus, it will either leave the player
     * paused or set it to a low volume, depending on what is permitted by the current focus
     * settings.
     */

    private fun configurePlayerState(){
        if (isMediaPlayer) {
            when (mCurrentAudioFocusState) {
                AUDIO_NO_FOCUS_NO_DUCK -> pauseMediaPlayer()
                else -> {
                    when (mCurrentAudioFocusState) {
                        AUDIO_NO_FOCUS_CAN_DUCK -> mediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK)
                        else -> mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL)
                    }
                    // If we were playing when we lost focus, we need to resume playing.
                    if (sPlayOnFocusGain) {
                        resumeMediaPlayer()
                        sPlayOnFocusGain = false
                    }
                }
            }
        }
    }

    /* Sets the volume of the media player */
    fun setPreciseVolume(percent: Int) {
        currentVolumeInPercent = percent

        if (isMediaPlayer) {
            fun volFromPercent(percent: Int): Float {
                if (percent == 100) {
                    return 1f
                }
                return (1 - (ln((101 - percent).toFloat()) / ln(101f)))
            }

            val new = volFromPercent(percent)
            mediaPlayer.setVolume(new, new)
        }
    }

    fun stopPlaybackService(stopPlayBack: Boolean){
        if (playerService.isRunning && isMediaPlayer && stopPlayBack) {
            playerService.stopForeground(true)
            playerService.stopSelf()
        }
        mediaPlayerInterface.onClose()
    }

    private inner class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action

            if (action != null) {
                when (action) {
                    Constants.REWIND_ACTION -> fastSeek(false)
                    Constants.PREV_ACTION -> instantReset()
                    Constants.PLAY_PAUSE_ACTION -> resumeOrPause()
                    Constants.NEXT_ACTION -> skip(true)
                    Constants.FAST_FORWARD_ACTION -> fastSeek(true)
                    Constants.REPEAT_ACTION -> {
                        repeat(true)
                        mediaPlayerInterface.onUpdateRepeatStatus()
                    }
                    Constants.CLOSE_ACTION -> if (playerService.isRunning && isMediaPlayer) {
                        stopPlaybackService(
                            stopPlayBack = true
                        )
                    }

                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> if (::currentSong.isInitialized && mPreferences.isHeadsetPlugEnabled) {
                        pauseMediaPlayer()
                    }
                    BluetoothDevice.ACTION_ACL_CONNECTED -> if (::currentSong.isInitialized && mPreferences.isHeadsetPlugEnabled) {
                        resumeMediaPlayer()
                    }

                    AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED ->
                        when (intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1)) {
                            AudioManager.SCO_AUDIO_STATE_CONNECTED -> if (isCurrentSong && mPreferences.isHeadsetPlugEnabled) {
                                resumeMediaPlayer()
                            }
                            AudioManager.SCO_AUDIO_STATE_DISCONNECTED -> if (isCurrentSong && mPreferences.isHeadsetPlugEnabled) {
                                pauseMediaPlayer()
                            }
                        }

                    Intent.ACTION_HEADSET_PLUG -> if (isCurrentSong && mPreferences.isHeadsetPlugEnabled){
                        when (intent.getIntExtra("state", -1)) {
                            // 0 means disconnected
                            HEADSET_DISCONNECTED -> if (isCurrentSong && mPreferences.isHeadsetPlugEnabled) {
                                pauseMediaPlayer()
                            }
                            // 1 means connected
                            HEADSET_CONNECTED -> if (isCurrentSong && mPreferences.isHeadsetPlugEnabled) {
                                resumeMediaPlayer()
                            }
                        }
                    }
                    AudioManager.ACTION_AUDIO_BECOMING_NOISY -> if (isPlaying && mPreferences.isHeadsetPlugEnabled) {
                        pauseMediaPlayer()
                    }
                }
            }
            if (isOrderedBroadcast) {
                abortBroadcast()
            }
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
            mediaPlayer.release()
            initMediaPlayer(currentSong.first)
        }
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        playerService.acquireWakeLock()

        mediaPlayerInterface.onStateChanged()
        mediaPlayerInterface.onPlaybackCompleted()

        when {
            isRepeat1X or isLooping ->
                if (isLooping) repeatSong()
            isQueue -> manageQueue(true)
            else -> {
                if (mPlayingAlbumSongs?.indexOf(currentSong.first) == mPlayingAlbumSongs?.size?.minus(1)) {
                    if (mPreferences.onListEnded == Constants.CONTINUE) {
                        skip(true)
                    } else {
                        synchronized(pauseMediaPlayer()) {
                            mMusicNotificationManager.cancelNotification()
                            mediaPlayerInterface.onPlaylistEnded()
                        }
                    }
                } else {
                    skip(true)
                }
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        if (isRepeat1X) {
            isRepeat1X = false
        }

        if (isSongRestoredFromPrefs) {
            if (mPreferences.isPreciseVolumeEnabled) {
                setPreciseVolume(currentVolumeInPercent)
            }
            mediaPlayer.seekTo(mPreferences.latestPlayedSong?.startFrom!!)
        } else if (isSongFromLovedSongs.first) {
            mediaPlayer.seekTo(isSongFromLovedSongs.second)
            isSongFromLovedSongs = Pair(false, 0)
        }

        if (isQueue) {
            mediaPlayerInterface.onQueueStartedOrEnded(isQueueStarted)
        }

        updateMediaSessionMetaData()

        if (mExecutor == null) {
            startUpdatingCallbackWithPosition()
        }

        if (isPlay) {

            play()
            Log.d("BBB",this.javaClass.simpleName +": Play")
        }else{
            Log.d("BBB",this.javaClass.simpleName +": not play")
        }
        if (mediaPlayer.audioSessionId != AudioEffect.ERROR_BAD_VALUE && !::mEqualizer.isInitialized && !::mBassBoost.isInitialized && !::mVirtualizer.isInitialized) {
            // instantiate custom equalizer
            createCustomEqualizer()
        }
    }
}