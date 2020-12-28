package company.ai.musicplayer.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.ai.musicplayer.R
import company.ai.musicplayer.ui.HomeActivity
import company.ai.musicplayer.controller.MediaControllerInterface
import company.ai.musicplayer.databinding.DialogMediaBinding
import company.ai.musicplayer.dialog.SampleFragment
import company.ai.musicplayer.extensions.imageByPicasso
import company.ai.musicplayer.extensions.toFormattedDuration
import company.ai.musicplayer.player.MediaPlayerHolder
import company.ai.musicplayer.player.MediaPlayerInterface
import company.ai.musicplayer.service.PlayerService
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.EqualizerHelper
import company.ai.musicplayer.utils.ThemeHelper

class NowPlayingDialog(var mPlayerService: PlayerService,var position: Int): BottomSheetDialogFragment(), View.OnClickListener, View.OnLongClickListener, MediaPlayerInterface {
    private lateinit var mDialogMedia: DialogMediaBinding
    private lateinit var mContext: Context
    private lateinit var mediaController: MediaControllerInterface
    private lateinit var mMediaPlayerHolder: MediaPlayerHolder

    private var color: Int = 0;
   /* override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }*/

    override fun onAttach(context: Context){
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        color = ThemeHelper.getAccentedTheme().first
        setStyle(STYLE_NORMAL,R.style.BottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)

        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        //id = com.google.android.material.R.id.design_bottom_sheet for Material Components
        //id = android.support.design.R.id.design_bottom_sheet for support librares
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        // set height dialog bottom
//        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            isHideable = true
            peekHeight = 100
        }
    }

    fun setMediaController(mediaController: MediaControllerInterface){
        this.mediaController = mediaController
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 80 / 100
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDialogMedia = DialogMediaBinding.inflate(layoutInflater)
        mDialogMedia.root.fitsSystemWindows = true
        mMediaPlayerHolder = mPlayerService.mediaPlayerHolder
        mMediaPlayerHolder.mediaPlayerInterface = this
        return mDialogMedia.root
    }

    fun isShowing(): Boolean? {
        return dialog?.isShowing
    }

    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)
        mediaController.onCancelDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        assignView()
    }


    override fun onClick(view: View) {
        when (view){
            mDialogMedia.imgPlayPause -> {
                mediaController.onResumeOrPause()
                mMediaPlayerHolder.setStatusPlaying()
            }
            mDialogMedia.imgClose -> {
                dismiss()
            }
            mDialogMedia.imgNext -> {
                mediaController.onSkip(true)
            }
            mDialogMedia.imgPrev -> {
                mediaController.onSkip(false)
            }
            mDialogMedia.imgRepeat -> {
                mDialogMedia.imgRepeat.setImageResource(ThemeHelper.getRepeatIcon(mMediaPlayerHolder))
                setRepeat()
            }
            mDialogMedia.imgEqualizer -> {
                openEqualizer()
            }
            mDialogMedia.imgAbout -> {
                val dialog = SampleFragment.newInstance(
                    "Information Music",
                    mMediaPlayerHolder.currentSong.first
                )
                dialog.show(requireActivity().supportFragmentManager, Constants.LIST_DIALOG_FRAGMENT)
            }
            mDialogMedia.imgViewQueue -> {
                val dialog = SampleFragment.newInstance(
                    "List Music",
                    (requireActivity() as HomeActivity).currentListMusic!!.toMutableList(),
                    (requireActivity() as HomeActivity).currentLaunchedBy
                )
                dialog.show(requireActivity().supportFragmentManager, Constants.LIST_DIALOG_FRAGMENT)
            }
        }
    }

    override fun onLongClick(view: View): Boolean {

        return true
    }

    private fun setRepeat() {
        mMediaPlayerHolder.repeat(mMediaPlayerHolder.isPlaying)
    }

    private fun initView(){
        mMediaPlayerHolder.currentSong.first?.let {
            mDialogMedia.textSubTitle.text = it.displayName
            mDialogMedia.textAlbumArtist.text = requireActivity().resources.getString(R.string.artist_and_album, it.artist, it.album)
            mDialogMedia.imageAlbumArt.imageByPicasso(it.albumID)

        }
        mMediaPlayerHolder.setStatusPlaying()

        mDialogMedia.apply {

        }

    }

    private fun MediaPlayerHolder.setStatusPlaying(){
        if (isPlaying) mDialogMedia.imgPlayPause.setIconPlay() else mDialogMedia.imgPlayPause.setIconPause()
    }

    private fun assignView(){
        mDialogMedia.imgPrev.setOnClickListener(this)
        mDialogMedia.imgNext.setOnClickListener(this)
        mDialogMedia.imgRepeat.setOnClickListener(this)
        mDialogMedia.imgPlayPause.setOnClickListener(this)
        mDialogMedia.imgAbout.setOnClickListener(this)
        mDialogMedia.imgAddToPlaylist.setOnClickListener(this)
        mDialogMedia.imgEqualizer.setOnClickListener(this)
        mDialogMedia.imgFavorite.setOnClickListener(this)
        mDialogMedia.imgShuffle.setOnClickListener(this)
        mDialogMedia.imgClose.setOnClickListener(this)
        mDialogMedia.imgViewQueue.setOnClickListener(this)

        setSeekBarProgressListener()
    }

    private fun updateNowPlayingInfo(){
        mMediaPlayerHolder.currentSong.first?.let {
            mDialogMedia.seekbarTimer.progress = 0
            mDialogMedia.seekbarTimer.max = it.duration.toInt()
            mDialogMedia.textStart.text = mDialogMedia.seekbarTimer.progress.toLong().toFormattedDuration(isAlbum = false, isSeekBar = false)
            mDialogMedia.textEnd.text = it.duration.toFormattedDuration(isAlbum = false, isSeekBar = false)
            mDialogMedia.textSubTitle.text = it.displayName
            mDialogMedia.textAlbumArtist.text = requireActivity().resources.getString(R.string.artist_and_album, it.artist, it.album)
            mDialogMedia.imageAlbumArt.imageByPicasso(it.albumID)

        }
        mMediaPlayerHolder.setStatusPlaying()
    }

    private fun openEqualizer(){
        if (EqualizerHelper.hasEqualizer(mContext)){
            mMediaPlayerHolder.openEqualizer(mContext as Activity)
        }else{

        }
    }

    /*private fun closeEqualizerFragment() {
        if (!sRevealAnimationRunning) {
            mEqualizerFragment.onHandleBackPressed().apply {
                sRevealAnimationRunning = true
                doOnEnd {
                    synchronized(super.onBackPressed()) {
                        sRevealAnimationRunning = false
                        setTabLayoutEnabled(isFirstSetup = false, isTabsEnabled = true)
                    }
                }
            }
        }
    }*/


    private fun updatePlayingStatus(){
        val isPlaying = mMediaPlayerHolder.state != Constants.PAUSED

        if (isPlaying) mDialogMedia.imgPlayPause.setIconPlay() else mDialogMedia.imgPlayPause.setIconPause()
    }


    private fun setSeekBarProgressListener(){

        mMediaPlayerHolder.currentSong.first?.let {
            mDialogMedia.seekbarTimer.max = it.duration.toInt()
            mDialogMedia.seekbarTimer.progress = position
            Log.d("NNN", "NowPlayingDialog: ${position}")
            mDialogMedia.textStart.text = mDialogMedia.seekbarTimer.progress.toLong().toFormattedDuration(isAlbum = false, isSeekBar = false)
            mDialogMedia.textEnd.text = it.duration.toFormattedDuration(isAlbum = false, isSeekBar = false)
        }

        mDialogMedia.seekbarTimer.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            val defaultPositionColor = mDialogMedia.textStart.currentTextColor
            var userSelectedPosition = 0
            var isUserSeeking = false
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isUserSeeking){
                    userSelectedPosition = progress
                    position = userSelectedPosition
                    mDialogMedia.textStart.setTextColor(
                        ThemeHelper.resolveThemeAccent(mContext)
                    )
                }
                mDialogMedia.textStart.text =
                    progress.toLong().toFormattedDuration(isAlbum = false, isSeekBar = true)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isUserSeeking) {
                    mDialogMedia.textStart.setTextColor(defaultPositionColor)
                    mMediaPlayerHolder.onPauseSeekBarCallback()
                    isUserSeeking = false
                }

                if (::mMediaPlayerHolder.isInitialized && mMediaPlayerHolder.isCurrentSong) {
                    Log.d("MMM", "enter if")
                    if (mMediaPlayerHolder.state != Constants.PLAYING) {
                        mediaController.onCurrentPosition(userSelectedPosition)
                        mDialogMedia.seekbarTimer.progress = userSelectedPosition
                    }
                    Log.d("MMM", "${mMediaPlayerHolder.currentSong.first?.displayName}")
                    mMediaPlayerHolder.seekTo(
                        userSelectedPosition,
                        updatePlaybackStatus = mMediaPlayerHolder.isPlaying,
                        restoreProgressCallBack = !isUserSeeking
                    )
                }
            }

        })
    }

    override fun onPositionChanged(position: Int) {
        Log.d("NNN", "NowPlayingDialog onPositionChanged: ${position}")
        mDialogMedia.seekbarTimer.progress = position
    }

    override fun onStateChanged() {
        updatePlayingStatus()
        Log.d("NNN", "NowPlayingDialog onStateChanged: ${mMediaPlayerHolder.currentSong.first!!.displayName}")
        if (mMediaPlayerHolder.state != Constants.RESUMED && mMediaPlayerHolder.state != Constants.PAUSED){
            updateNowPlayingInfo()
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

    }

    override fun onFocusLoss() {

    }

    override fun onPlaylistEnded() {

    }
}