package company.ai.musicplayer.ui

import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationListener
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Bundle
import android.os.IBinder
import android.provider.OpenableColumns
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import company.ai.musicplayer.database.MusicViewModel
import company.ai.musicplayer.adapters.QueueAdapter
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.HandleLifecycles
import company.ai.musicplayer.controller.MediaControllerInterface
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.*
import company.ai.musicplayer.dialog.LoadingDialog
import company.ai.musicplayer.dialog.NowPlayingDialog
import company.ai.musicplayer.extensions.*
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.player.MediaPlayerHolder
import company.ai.musicplayer.player.MediaPlayerInterface
import company.ai.musicplayer.service.PlayerService
import company.ai.musicplayer.utils.*
import kotlinx.android.synthetic.main.dialog_media.*
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity(), View.OnClickListener, UIControlInterface {
    lateinit var mHomeBinding: ActivityHomeBinding
    lateinit var mLayoutMain: LayoutMainBinding
    private lateinit var mLayoutSplash: LayoutSplashBinding
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mFrameLayout: FrameLayout
    private lateinit var mHomeContent: HomeMainContentBinding
    // Our PlayerService shit
    private lateinit var mPlayerService: PlayerService
    private var sBound = false
    private var mNowPlayingDialog: NowPlayingDialog? = null

    // The player
    lateinit var mMediaPlayerHolder: MediaPlayerHolder
    private val isMediaPlayerHolder get() = ::mMediaPlayerHolder.isInitialized
    private lateinit var mBindingIntent: Intent

    private var sAppearanceChanged = false
    private var sRestoreSettingsFragment = false
    // View model
    private val mMusicViewModel: MusicViewModel by viewModels()
    private var mBundle: Bundle? = null

    var songOri: Music? = null
    private lateinit var mQueueAdapter: QueueAdapter
    // Colors
    val mResolvedAccentColor by lazy { ThemeHelper.resolveThemeAccent(this) }
    val mResolvedAlphaAccentColor by lazy {
        ThemeHelper.getAlphaAccent(
            this,
            ThemeHelper.getAlphaForAccent()
        )
    }

    lateinit var currentLaunchedBy: String
    var currentListMusic: List<Music>? = null
    private fun checkIsPlayer(showError: Boolean) = mMediaPlayerHolder.apply {
        if (!isMediaPlayer && !isSongRestoredFromPrefs && showError) {
            getString(
                R.string.error_bad_id
            ).toToast(this@HomeActivity)
        }
    }.isMediaPlayer

    private fun MediaPlayerHolder.setStatusPlayed(){
        if (isPlay) {
            mLayoutMain.imgPlayPause.setIconPlay()
        } else {
            mLayoutMain.imgPlayPause.setIconPause()
        }
    }

    private fun MediaPlayerHolder.setStatusPlaying(){
        if (isPlaying) {
            mLayoutMain.imgPlayPause.setIconPlay()
        } else {
            mLayoutMain.imgPlayPause.setIconPause()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
       /* if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Do something
            if (mNowPlayingDialog != null && mNowPlayingDialog!!.dialog!!.isShowing){
                Log.d("MMM", "KEYCODE_VOLUME_DOWN")

                mNowPlayingDialog!!.mDialogBinding.npVolumeSeek.apply {
                    progress -= 1
                }
            }else{
                Log.d("MMM", "KEYCODE_VOLUME_DOWN: not show")
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){

            if (mNowPlayingDialog != null && mNowPlayingDialog!!.dialog!!.isShowing){
                Log.d("MMM", "KEYCODE_VOLUME_UP")
                mNowPlayingDialog!!.mDialogBinding.npVolumeSeek.apply {
                    progress += 1
                }
            }else{
                Log.d("MMM", "KEYCODE_VOLUME_UP: not show")
            }
        }*/
        return true
    }

    private val isSettingsFragment get() = supportFragmentManager.isFragment(Constants.TAG_FRAGMENT)

    private val mMediaControllerInterface: MediaControllerInterface = object : MediaControllerInterface {
        override fun onCurrentPosition(pos: Int) {
            mHomeBinding.layoutMain.songProgress.progress = pos
        }

        override fun onShuffle() {
            onShuffleSongs(currentListMusic!!.toMutableList(), currentLaunchedBy)
        }

        override fun onResumeOrPause() {
            resumeOrPause()
            mMediaPlayerHolder.setStatusPlaying()
        }

        override fun onCancelDialog() {
            mMediaPlayerHolder.mediaPlayerInterface = mMediaPlayerInterface
            val fragment = supportFragmentManager.findFragmentByTag(Constants.TAG_FRAGMENT)
            if (fragment is LibraryFragment){
                (fragment).setUpHeader(mMediaPlayerHolder.currentSong.first!!)
            }


        }

        override fun onEqualizer() {
            openEqualizer()

        }

        override fun onSkip(isNext: Boolean) {
            skip(isNext)
            updateNowPlayingInfo()
        }
    }

    lateinit var myLocationListener: HandleLifecycles
    override fun onStart() {
        super.onStart()
        myLocationListener.start()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        myLocationListener = HandleLifecycles(this){ location ->
            sRestoreSettingsFragment =
                savedInstanceState?.getBoolean(Constants.RESTORE_SETTINGS_FRAGMENT)
                    ?: intent.getBooleanExtra(Constants.RESTORE_SETTINGS_FRAGMENT, false)

            Log.d("AAA", if (savedInstanceState == null) "null" else "khác null")
            Log.d("AAA","sRestoreSettingsFragment: ${sRestoreSettingsFragment}")
            super.onCreate(savedInstanceState)
            setTheme(ThemeHelper.getAccentedTheme().first)
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
        myLocationListener.stop()
    }

    override fun onResume() {
        super.onResume()
        if (isMediaPlayerHolder && mMediaPlayerHolder.isMediaPlayer) {
            mMediaPlayerHolder.onRestartSeekBarCallback()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (sAppearanceChanged) {
            super.onSaveInstanceState(outState)
            outState.putBoolean(
                Constants.RESTORE_SETTINGS_FRAGMENT,
                true
            )
        }
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

    override fun onBackPressed() {
        baseBackPressed()
    }

    private fun openEqualizer(){
        mMediaPlayerHolder.mediaPlayerInterface = mMediaPlayerInterface
        if(!EqualizerHelper.hasEqualizer(this)){
            supportFragmentManager.addFragment(EqFragment.newInstance(), Constants.TAG_FRAGMENT, isSettingsFragment)
        }else{
            mMediaPlayerHolder.openEqualizer(this)
        }
    }

    private fun baseBackPressed() {
        // Otherwise, it may return to the previous fragment stack
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            // Lastly, it will rely on the system behavior for back
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bạn có muốn thoát App không ?")
            builder.setNegativeButton(
                "Không"
            ) { dialog, _ -> dialog.cancel() }
            builder.setPositiveButton(
                "Có"
            ) { _, _ -> exitProcess(1) }
            builder.show()
        }
    }

    private fun initView(){
        mLoadingDialog = LoadingDialog()
        mLayoutSplash = mHomeBinding.layoutSplash
        mLayoutMain = mHomeBinding.layoutMain
        mHomeContent = mLayoutMain.homeMainContent
        supportFragmentManager.addFragment(HomeFragment.newInstance(), Constants.TAG_FRAGMENT, isSettingsFragment)
    }

    private fun assignView(){
        mLayoutMain.relativeInfoMusic.setOnClickListener(this)
        mLayoutMain.btnLibrary.setOnClickListener(this)
        mLayoutMain.btnHome.setOnClickListener(this)
        mLayoutMain.imgPlayPause.setOnClickListener(this)
        mLayoutMain.linearNext.setOnClickListener(this)
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
            if (::mLoadingDialog.isInitialized &&  mLoadingDialog.isShowing()){
                mLoadingDialog.dismiss()
            }

        }
    }

    /**
     * connection service
     * */
    private fun doBindService() {
        if (sRestoreSettingsFragment){
            setShowProgress(true)
            supportFragmentManager.addFragment(SettingsFragment.newInstance(), Constants.TAG_FRAGMENT, isSettingsFragment)
        }else {
            mLayoutSplash.splash.handleViewVisibility(true)
        }
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
                setStatusPlayed()
            }
            mMusicViewModel.mDeviceMusic.observe(
                this@HomeActivity,
                Observer<MutableList<Music>?> { returnedMusic ->
                    finishSetup(returnedMusic)
                }
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
                Log.d("NNN", "HomeActivity onClick: ${mHomeBinding.layoutMain.songProgress.progress}")
                if (checkIsPlayer(true) && mMediaPlayerHolder.isCurrentSong) {
                    mNowPlayingDialog = NowPlayingDialog(mPlayerService, mHomeBinding.layoutMain.songProgress.progress)
                    mNowPlayingDialog?.apply {
                        setMediaController(mMediaControllerInterface)
                        show(supportFragmentManager, Constants.NOW_PLAY_DIALOG_FRAGMENT)
                    }!!
                }
            }
            mLayoutMain.btnHome -> {
                mLayoutMain.textHome.setTextColor(mResolvedAccentColor)
                mLayoutMain.textLibrary.setTextColor(ContextCompat.getColor(this, R.color.black))
                supportFragmentManager.addFragment(HomeFragment.newInstance(), Constants.TAG_FRAGMENT, true)
            }
            mLayoutMain.btnLibrary -> {
                mLayoutMain.textHome.setTextColor(ContextCompat.getColor(this, R.color.black))
                mLayoutMain.textLibrary.setTextColor(mResolvedAccentColor)
                supportFragmentManager.addFragment(LibraryFragment.newInstance(), Constants.TAG_FRAGMENT, true)
            }
            mLayoutMain.imgPlayPause -> {
                resumeOrPause()
                mMediaPlayerHolder.setStatusPlaying()

            }
            mLayoutMain.linearNext -> {
                skip(true)
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

    private fun skip(isNext: Boolean){
        if (checkIsPlayer(true)){
            if (!mMediaPlayerHolder.isPlay){
                mMediaPlayerHolder.isPlay = true
            }
            if (isNext) {
                mMediaPlayerHolder.skip(true)
            } else {
                Log.d("CCC", "Skip false")
                mMediaPlayerHolder.instantReset()
            }
            if (mMediaPlayerHolder.isSongRestoredFromPrefs) {
                mMediaPlayerHolder.isSongRestoredFromPrefs = false
            }
        }
    }

    private fun updateNowPlayingInfo(){
        val selectedSong = mMediaPlayerHolder.currentSong.first
        mLayoutMain.songProgress.progress = 0
        setUiCurrentSong(selectedSong)
        mMediaPlayerHolder.setStatusPlayed()
    }

    private fun finishSetup(allMusic: MutableList<Music>?){
        if (!allMusic.isNullOrEmpty()){
            if (sRestoreSettingsFragment){
                supportFragmentManager.addFragment(SettingsFragment.newInstance(), Constants.TAG_FRAGMENT, isSettingsFragment)
            }else {
                mLayoutSplash.splash.handleViewVisibility(false)
                mLayoutMain.home.handleViewVisibility(true)
            }


            synchronized(handlerRestore()){
                mLayoutMain.controller.animate().apply {
                    duration = 500
                    alpha(1.0F)
                }
            }
        }
    }

    private fun handlerRestore(){
        if (intent != null && Intent.ACTION_VIEW == intent.action && intent.data != null){
            handleIntent(intent)
            Log.d("XXX","enter if")
        }else{
            Log.d("XXX","enter else")
            restorePlayerStatus()
        }
    }

    private fun handleIntent(intent: Intent){
        intent.data?.let { returnUri ->
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            try {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                val song = mMusicViewModel.getSongFromIntent(cursor.getString(displayNameIndex))
                val allMusic = ListsHelper.getSortedMusicList(Constants.DESCENDING_SORTING, mMusicViewModel.mDeviceMusicFiltered)
                onSongSelected(song, allMusic, Constants.ARTIST_VIEW)
                //get album songs and sort them
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }


    private fun startPlayback(song: Music?, songs: List<Music>?, launchedBy: String){
        if (isMediaPlayerHolder){
            if(::mPlayerService.isInitialized && !mPlayerService.isRunning){
                startService(mBindingIntent)
            }
            mMediaPlayerHolder.apply {
                currentLaunchedBy = launchedBy
                currentListMusic = songs
                setServiceCurrentSong(song, songs, isFromQueue = false, isFolderAlbum = launchedBy)
                initMediaPlayer(song)
                setUiCurrentSong(song)
            }
        }
    }

    private fun getLatestSongLaunchedBy() = mPreferences.latestPlayedSong?.launchedBy ?: Constants.ARTIST_VIEW

    private fun restorePlayerStatus(){
        if(isMediaPlayerHolder){
            // If we are playing and the activity was restarted
            // update the controls panel
            mMediaPlayerHolder.apply {
                if (isMediaPlayer && mMediaPlayerHolder.isPlaying) {
                    onRestartSeekBarCallback()
                    updatePlayingInfo(true)
                } else {
                    isSongRestoredFromPrefs = mPreferences.latestPlayedSong != null
                    songOri =
                        if (isSongRestoredFromPrefs){
                            MusicOrg.getSongForRestore(mPreferences.latestPlayedSong, mMusicViewModel.mDeviceMusicList)
                        }else{
                            mMusicViewModel.mRandomMusic
                        }
                    val allMusic = ListsHelper.getSortedMusicList(Constants.DESCENDING_SORTING, mMusicViewModel.mDeviceMusicFiltered)
                    if (!allMusic.isNullOrEmpty()){
                        isPlay = false
                        startPlayback(
                            songOri,
                            allMusic,
                            getLatestSongLaunchedBy()
                        )
                        updatePlayingInfo(false)
                    }
                }
            }
        }
    }

    private fun setUiCurrentSong(song: Music?){
        song?.let {
            mLayoutMain.textTitle.text = it.displayName!!.split("-")[0]
            mLayoutMain.textSuptitle.text =  getString(
                R.string.artist_and_album,
                it.artist,
                it.album
            )
            mHomeBinding.layoutMain.songProgress.max = it.duration.toInt()
            mLayoutMain.imgAlbumArt.setImageBitmap(song.getAlbumArt(this))
        }
    }

    // save current media
    private fun saveSongToPref() {
        if (::mMediaPlayerHolder.isInitialized && !mMediaPlayerHolder.isPlaying || mMediaPlayerHolder.state == Constants.PAUSED) mMediaPlayerHolder.apply {
            MusicOrg.saveLatestSong(currentSong.first, mMediaPlayerHolder, launchedBy)
        }
    }

    private fun updatePlayingStatus(){
        val isPlaying = mMediaPlayerHolder.state != Constants.PAUSED
        Log.d("NNN","HomeActivity --- updatePlayingStatus: ${isPlaying}")
        mLayoutMain.imgPlayPause.setIsPlay(isPlaying)
        if (isPlaying){
            mLayoutMain.imgPlayPause.setIconPlay()
        } else {
            mLayoutMain.imgPlayPause.setIconPause()
        }

    }

    private fun updatePlayingInfo(restore: Boolean){
        if (::mMediaPlayerHolder.isInitialized) {
            val selectedSong = mMediaPlayerHolder.currentSong.first
            mLayoutMain.songProgress.progress = 0
            setUiCurrentSong(selectedSong)
        }
        if (restore){
            if (!mMediaPlayerHolder.queueSongs.isNullOrEmpty() && !mMediaPlayerHolder.isQueueStarted) {
                mMediaPlayerInterface.onQueueEnabled()
            } else {
                mMediaPlayerInterface.onQueueStartedOrEnded(mMediaPlayerHolder.isQueueStarted)
            }
            updatePlayingStatus()
            if (::mPlayerService.isInitialized) {
                //stop foreground if coming from pause state
                mPlayerService.apply {
                    if (isRestoredFromPause) {
                        stopForeground(false)
                        musicNotificationManager.updateNotification()
                        isRestoredFromPause = false
                    }
                }
            }
        }
    }

    // interface to let MediaPlayerHolder update the UI media player controls.
    val mMediaPlayerInterface = object : MediaPlayerInterface {
        override fun onPositionChanged(position: Int) {
            mHomeBinding.layoutMain.songProgress.progress = position

        }

        override fun onStateChanged() {
            Log.d("NNN", "HomeActivity: ${mMediaPlayerHolder.currentSong.first!!.displayName}")
            Log.d("NNN", "HomeActivity --- State: ${mMediaPlayerHolder.state}")

            if (mMediaPlayerHolder.state != Constants.RESUMED && mMediaPlayerHolder.state != Constants.PAUSED){
                updateNowPlayingInfo()
            }else{
                updatePlayingStatus()
            }
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
            saveSongToPref()
        }

        override fun onFocusLoss() {
            saveSongToPref()
        }

        override fun onPlaylistEnded() {

        }

    }

    override fun onAppearanceChanged(isAccentChanged: Boolean, restoreSettings: Boolean) {
        sAppearanceChanged = true
        synchronized(saveSongToPref()) {
            ThemeHelper.applyChanges(this)
        }
    }

    override fun onThemeChanged() {
        sAppearanceChanged = true
        synchronized(saveSongToPref()) {
            AppCompatDelegate.setDefaultNightMode(
                ThemeHelper.getDefaultNightMode(this)
            )
        }
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

                setStatusPlaying()
            }
        }else{
            Log.d("BBB","HomeActivity else: ${song?.displayName}")
        }
    }

    override fun onShuffleSongs(songs: MutableList<Music>?, launchedBy: String) {
        val randomNumber = (0 until songs?.size!!).getRandom()
        songs.shuffle()
        val song = songs[randomNumber]
        onSongSelected(song = song, songs = songs, launchedBy = launchedBy)
    }

    override fun onLovedSongsUpdate(clear: Boolean) {

    }

    override fun onCloseActivity(fragment: Fragment) {
        Log.d("VVV","onCloseActivity: Enter")
        if (fragment is SettingsFragment || fragment is EqFragment){
            supportFragmentManager.addFragment(HomeFragment.newInstance(), Constants.TAG_FRAGMENT, true)
        }
    }

    override fun onAddToQueue(song: Music?) {

    }

    override fun onAddToFilter(stringToFilter: String?) {

    }

    override fun onDenyPermission() {

    }

    override fun onHandleFocusPref() {
        if (isMediaPlayerHolder) {
            if (isMediaPlayerHolder && mMediaPlayerHolder.isMediaPlayer) {
                if (mPreferences.isFocusEnabled) {
                    mMediaPlayerHolder.tryToGetAudioFocus()
                } else {
                    mMediaPlayerHolder.giveUpAudioFocus()
                }
            }
        }
    }

    override fun onHandleNotificationUpdate(isAdditionalActionsChanged: Boolean) {
        Log.d("NNN", "HomeActivity --- onHandleNotificationUpdate: $isAdditionalActionsChanged")
        if (isMediaPlayerHolder) {
            mMediaPlayerHolder.onHandleNotificationUpdate(isAdditionalActionsChanged)
        }
    }

    override fun onGetEqualizer(): Triple<Equalizer, BassBoost, Virtualizer> = mMediaPlayerHolder.getEqualizer()

    override fun onEnableEqualizer(isEnabled: Boolean) {

    }

    override fun onSaveEqualizerSettings(
        selectedPreset: Int,
        bassBoost: Short,
        virtualizer: Short
    ) {

    }


}