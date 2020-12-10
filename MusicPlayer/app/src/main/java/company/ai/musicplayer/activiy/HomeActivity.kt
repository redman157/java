package company.ai.musicplayer.activiy

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import company.ai.musicplayer.MusicViewModel
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.MediaControllerInterface
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.*
import company.ai.musicplayer.dialog_custom.LoadingDialog
import company.ai.musicplayer.dialog_custom.NowPlayingDialog
import company.ai.musicplayer.extensions.*
import company.ai.musicplayer.fragment.HomeFragment
import company.ai.musicplayer.fragment.LibraryFragment
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.player.MediaPlayerHolder
import company.ai.musicplayer.player.MediaPlayerInterface
import company.ai.musicplayer.service.PlayerService
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.MusicOrg
import company.ai.musicplayer.utils.PermissionHelper
import company.ai.musicplayer.utils.ThemeHelper

class HomeActivity : ActionBarCastActivity(), View.OnClickListener, UIControlInterface {
    lateinit var mHomeBinding: ActivityHomeBinding
    private lateinit var mLayoutMain: LayoutMainBinding
    private lateinit var mLayoutSplash: LayoutSplashBinding
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mFrameLayout: FrameLayout
    private lateinit var mHomeContent: HomeMainContentBinding
    // Our PlayerService shit
    private lateinit var mPlayerService: PlayerService
    private var sBound = false
    private lateinit var mNowPlayingBinding: NowPlayingDialog

    // The player
    lateinit var mMediaPlayerHolder: MediaPlayerHolder
    private val isMediaPlayerHolder get() = ::mMediaPlayerHolder.isInitialized
    private lateinit var mNowPlayingDialog: NowPlayingDialog
    private val isNowPlaying get() = ::mNowPlayingDialog.isInitialized && mNowPlayingDialog.isShowing()!!
    private lateinit var mBindingIntent: Intent

    // View model
    private val mMusicViewModel: MusicViewModel by viewModels()
    private var mBundle: Bundle? = null

    // Colors
    private val mResolvedAccentColor by lazy { ThemeHelper.resolveThemeAccent(this) }
    private val mResolvedAlphaAccentColor by lazy {
        ThemeHelper.getAlphaAccent(
            this,
            ThemeHelper.getAlphaForAccent()
        )
    }
    private fun checkIsPlayer(showError: Boolean) = mMediaPlayerHolder.apply {
        if (!isMediaPlayer && !isSongRestoredFromPrefs && showError) {
            getString(
                R.string.error_bad_id
            ).toToast(
                this@HomeActivity
            )
        }
    }.isMediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mHomeBinding.root)
        initView()
        assignView()
        if (PermissionHelper.hasToAskStoragePermission(this)) {
            PermissionHelper.manageAskForReadStoragePermission(
                activity = this, uiControlInterface = this
            )
        } else {
            doBindService()
        }
    }

    override fun onPause(){
        super.onPause()
        if (isMediaPlayerHolder && mMediaPlayerHolder.isMediaPlayer){
            saveSongToPref()
            mMediaPlayerHolder.apply {
                onPauseSeekBarCallback()
                if (!isPlaying) {
                    mMediaPlayerHolder.giveUpAudioFocus()
                }
            }
        }
    }

    override fun onStop(){
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMusicViewModel.cancel()
        if (sBound) {
            unbindService(connection)
        }
        if (isMediaPlayerHolder && !mMediaPlayerHolder.isPlaying && ::mPlayerService.isInitialized && mPlayerService.isRunning) {
            mPlayerService.stopForeground(true)
            stopService(mBindingIntent)
        }
    }

    private fun initView(){
        mLoadingDialog = LoadingDialog()
        mLayoutSplash = mHomeBinding.layoutSplash
        mLayoutMain = mHomeBinding.layoutMain
        mHomeContent = mLayoutMain.homeMainContent
        supportFragmentManager.addFragment(HomeFragment(), Constants.TAG_FRAGMENT, false)
    }

    private fun assignView(){
        mLayoutMain.relativeInfoMusic.setOnClickListener(this)
        mLayoutMain.btnLibrary.setOnClickListener(this)
        mLayoutMain.btnHome.setOnClickListener(this)
        mLayoutMain.imgPlayPause.setOnClickListener(this)
    }

    public fun saveInstance(bundle: Bundle){
        mBundle = bundle
    }

    public fun loadInstance(): Bundle? {
        return mBundle
    }

    public fun setShowProgress(isShow: Boolean){
        if (isShow){
            mLoadingDialog.show(supportFragmentManager, "progress")
        }else{
            mLoadingDialog.dismiss()
        }
    }

    /**
     * connection service
     * */
    private fun doBindService() {
        mLayoutSplash.splash.handleViewVisibility(true)
        mBindingIntent = Intent(this, PlayerService::class.java).also {
            bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    // Defines callbacks for service binding, passed to bindService()
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as PlayerService.LocalBinder
            Log.d("BBB", "onServiceConnected: Enter")
            mPlayerService = binder.getService()
            sBound = true
            mMediaPlayerHolder = mPlayerService.mediaPlayerHolder.apply {
                mediaPlayerInterface = mMediaPlayerInterface
                if (isPlay) {
                    mLayoutMain.imgPlayPause.setIconPlay()
                } else {
                    mLayoutMain.imgPlayPause.setIconPause()
                }
            }

            mMusicViewModel.mDeviceMusic.observe(
                this@HomeActivity,

                Observer<MutableList<Music>?> { returnedMusic -> finishSetup(returnedMusic) }
            )
            mMusicViewModel.getDeviceMusic()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            sBound = false
            Log.d("BBB", "onServiceDisconnected: Enter")
        }
    }


    override fun onClick(view: View?) {
        when (view){
            mLayoutMain.relativeInfoMusic -> {
                if (checkIsPlayer(true) && mMediaPlayerHolder.isCurrentSong) {
                    mNowPlayingBinding = NowPlayingDialog(mPlayerService, mHomeBinding.layoutMain.songProgress.progress)
                    mNowPlayingBinding.apply {
                        setMediaController(object : MediaControllerInterface{
                            override fun onCurrentPosition(pos: Int) {
                                mHomeBinding.layoutMain.songProgress.progress = pos
                            }

                            override fun onDismissDialog() {
                                mMediaPlayerHolder.mediaPlayerInterface = mMediaPlayerInterface
                            }

                        })

                        show(supportFragmentManager, Constants.DIALOG_FRAGMENT)
                    }

                }
            }
            mLayoutMain.btnHome -> {
                mLayoutMain.textHome.setTextColor(ContextCompat.getColor(this, R.color.red_alpha_100))
                mLayoutMain.textLibrary.setTextColor(ContextCompat.getColor(this, R.color.black))
                supportFragmentManager.beginTransaction().apply {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(mHomeContent.container.id, HomeFragment(), Constants.TAG_FRAGMENT).commit()
                }
            }
            mLayoutMain.btnLibrary -> {
                mLayoutMain.textHome.setTextColor(ContextCompat.getColor(this, R.color.black))
                mLayoutMain.textLibrary.setTextColor(ContextCompat.getColor(this, R.color.red_alpha_100))
                supportFragmentManager.beginTransaction().apply {
                    setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    replace(mHomeContent.container.id, LibraryFragment(), Constants.TAG_FRAGMENT).commit()
                }
            }
            mLayoutMain.imgPlayPause -> {
                resumeOrPause()
                if (mMediaPlayerHolder.isPlaying) mLayoutMain.imgPlayPause.setIconPlay() else mLayoutMain.imgPlayPause.setIconPause()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, yay! Do bind service
                    doBindService()
                } /*else {
                    // Permission denied, boo! Error!
                    notifyError(Constants.TAG_NO_PERMISSION)
                }*/
            }
        }
    }

    private fun resumeOrPause() {
        if (checkIsPlayer(true)) {
            mMediaPlayerHolder.resumeOrPause()
        }
    }

    private fun updateNowPlayingInfo(){
        val selectedSong = mMediaPlayerHolder.currentSong.first
        val selectedSongDuration = selectedSong?.duration!!

    }

    private fun updatePlayingStatus(){

    }

    private fun finishSetup(allMusic: MutableList<Music>?){
        if (!allMusic.isNullOrEmpty()){
            mLayoutSplash.splash.handleViewVisibility(false)
            mLayoutMain.home.handleViewVisibility(true)

            synchronized(handlerRestore()){

            }
        }
    }

    private fun handlerRestore(){
        if (intent != null && Intent.ACTION_VIEW == intent.action && intent.data != null){
            handleIntent(intent)
        }else{
            restorePlayerStatus()
        }
    }

    private fun handleIntent(intent: Intent){
        intent.data?.let { returnUri ->
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
         /*   try {
                val displayNameIndex =
            }catch (e: Exception){
                e.printStackTrace()
            }*/

        }
    }

    private fun startPlayback(song: Music?, songs: List<Music>?, launchedBy: String){
        if (isMediaPlayerHolder){
            if(::mPlayerService.isInitialized && !mPlayerService.isRunning){
                startService(mBindingIntent)
            }
            mMediaPlayerHolder.apply {
                setServiceCurrentSong(song, songs, isFromQueue = false, isFolderAlbum = launchedBy)
                initMediaPlayer(song)
                setUiCurrentSong(song)
            }
        }
    }

    private fun restorePlayerStatus(){
//        if (isMe)
    }

    private fun setUiCurrentSong(song: Music?){
        song?.let {
            mLayoutMain.textTitle.text = it.displayName
            mLayoutMain.textSuptitle.text =  getString(
                R.string.artist_and_album,
                it.artist,
                it.album
            )
            mHomeBinding.layoutMain.songProgress.max = it.duration.toInt()
            mLayoutMain.imgAlbumArt.setImageBitmap(song.getAlbumArt(this))
            if (mMediaPlayerHolder.isPlay) mLayoutMain.imgPlayPause.setIconPlay() else mLayoutMain.imgPlayPause.setIconPause()
        }
    }

    // save current media
    private fun saveSongToPref() {
        if (::mMediaPlayerHolder.isInitialized && !mMediaPlayerHolder.isPlaying || mMediaPlayerHolder.state == Constants.PAUSED) mMediaPlayerHolder.apply {
            MusicOrg.saveLatestSong(currentSong.first, mMediaPlayerHolder, launchedBy)
        }
    }

    private fun updatePlayingInfo(){
        val selectedSong = mMediaPlayerHolder.currentSong.first


    }

    // interface to let MediaPlayerHolder update the UI media player controls.
    private val mMediaPlayerInterface = object : MediaPlayerInterface {
        override fun onPositionChanged(position: Int) {
            mHomeBinding.layoutMain.songProgress.progress = position

        }

        override fun onStateChanged() {

        }

        override fun onPlaybackCompleted() {

        }

        override fun onClose() {

        }

        override fun onUpdateRepeatStatus() {

        }

        override fun onQueueEnabled() {

        }

        override fun onQueueCleared() {

        }

        override fun onQueueStartedOrEnded(started: Boolean) {

        }

        override fun onSaveSong() {

        }

        override fun onFocusLoss() {

        }

        override fun onPlaylistEnded() {

        }

    }

    override fun onAppearanceChanged(isAccentChanged: Boolean, restoreSettings: Boolean) {

    }

    override fun onThemeChanged() {

    }

    override fun onArtistOrFolderSelected(artistOrFolder: String, launchedBy: String) {

    }

    override fun onSongSelected(song: Music?, songs: List<Music>?, launchedBy: String) {
        if(isMediaPlayerHolder){
            Log.d("BBB","HomeActivity if: ${song?.displayName}")
            mMediaPlayerHolder.apply {
                isSongRestoredFromPrefs = false
                if (!isPlay) isPlay = true
                if (isQueue) setQueueEnabled(false)
                startPlayback(song, songs, launchedBy)
            }
        }else{
            Log.d("BBB","HomeActivity else: ${song?.displayName}")
        }
    }

    override fun onShuffleSongs(songs: MutableList<Music>?, launchedBy: String) {

    }

    override fun onLovedSongsUpdate(clear: Boolean) {

    }

    override fun onCloseActivity() {

    }

    override fun onAddToQueue(song: Music?) {

    }

    override fun onAddToFilter(stringToFilter: String?) {

    }

    override fun onDenyPermission() {

    }

    override fun onHandleFocusPref() {

    }

    override fun onHandleNotificationUpdate(isAdditionalActionsChanged: Boolean) {

    }

    override fun onGetEqualizer(): Triple<Equalizer, BassBoost, Virtualizer> {
        TODO("Not yet implemented")
    }

    override fun onEnableEqualizer(isEnabled: Boolean) {

    }

    override fun onSaveEqualizerSettings(
        selectedPreset: Int,
        bassBoost: Short,
        virtualizer: Short
    ) {

    }


}