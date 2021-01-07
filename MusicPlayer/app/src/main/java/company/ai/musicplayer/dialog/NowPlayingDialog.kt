package company.ai.musicplayer.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.ai.musicplayer.R
import company.ai.musicplayer.ui.HomeActivity
import company.ai.musicplayer.controller.MediaControllerInterface
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.DialogMediaBinding
import company.ai.musicplayer.extensions.decodeColor
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.extensions.imageByPicasso
import company.ai.musicplayer.extensions.toFormattedDuration
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.player.MediaPlayerHolder
import company.ai.musicplayer.player.MediaPlayerInterface
import company.ai.musicplayer.service.PlayerService
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.EqualizerHelper
import company.ai.musicplayer.utils.MusicOrg
import company.ai.musicplayer.utils.ThemeHelper

class NowPlayingDialog(var mPlayerService: PlayerService,var position: Int): BottomSheetDialogFragment(), View.OnLongClickListener {
    lateinit var mDialogBinding: DialogMediaBinding
    private lateinit var mContext: Context
    private lateinit var mediaController: MediaControllerInterface
    private lateinit var mMediaPlayerHolder: MediaPlayerHolder

    private var color: Int = 0;
    private val mResolvedIconsColor by lazy { R.color.widgetsColor.decodeColor(requireContext()) }

    val mResolvedAccentColor by lazy { ThemeHelper.resolveThemeAccent(requireContext()) }

    private val mResolvedDisabledIconsColor by lazy {
                ThemeHelper.resolveColorAttr(
                    requireContext(),
                    android.R.attr.colorButtonNormal
                )
            }


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
        mDialogBinding = DialogMediaBinding.inflate(layoutInflater)
        mDialogBinding.root.fitsSystemWindows = true
        mMediaPlayerHolder = mPlayerService.mediaPlayerHolder
        mMediaPlayerHolder.mediaPlayerInterface = mMediaPlayerInterface
        return mDialogBinding.root
    }

    fun isShowing(): Boolean{
        return dialog?.isShowing!!
    }

    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        assignView()
    }


    override fun onLongClick(view: View): Boolean {

        return true
    }

    private fun setRepeat() {
        mMediaPlayerHolder.repeat(mMediaPlayerHolder.isPlaying)
        updateRepeatStatus(false)
    }

    private fun initView(){
        mMediaPlayerHolder.currentSong.first?.let {
            mDialogBinding.textSubTitle.text = it.displayName
            mDialogBinding.textAlbumArtist.text = requireActivity().resources.getString(R.string.artist_and_album, it.artist, it.album)
            mDialogBinding.imageAlbumArt.setImageBitmap(it.getAlbumArt(requireContext()))
        }

        mDialogBinding.apply {
            ThemeHelper.updateIconTint(mDialogBinding.imgPrev, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgNext, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgAbout, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgAddToPlaylist, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgEqualizer, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgFavorite, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgRepeat, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgViewQueue, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.imgShuffle, mResolvedAccentColor)
            ThemeHelper.updateIconTint(mDialogBinding.npVolume, mResolvedAccentColor)

            seekbarTimer.thumb.setColorFilter(mResolvedAccentColor,PorterDuff.Mode.SRC_ATOP)
            npVolumeSeek.thumb.setColorFilter(mResolvedAccentColor,PorterDuff.Mode.SRC_ATOP)

        }
        mMediaPlayerHolder.setStatusPlaying()

        if (mPreferences.isPreciseVolumeEnabled){
            setupPreciseVolumeHandler()
        }else{
            mDialogBinding.npVolumeSeek.isEnabled = false
            ThemeHelper.updateIconTint(
                mDialogBinding.npVolume,
                mResolvedDisabledIconsColor
            )
        }
    }

    private fun setupPreciseVolumeHandler(){
        mMediaPlayerHolder.currentVolumeInPercent.apply {
            mDialogBinding.npVolume.setImageResource(
                ThemeHelper.getPreciseVolumeIcon(
                    this
                )
            )
            mDialogBinding.npVolumeSeek.progress = this
        }

        mDialogBinding.npVolumeSeek.apply {
            max = 100
            Log.d("MMM","npVolumeSeek: $max")
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                var isUserSeeking = false

                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (isUserSeeking) {
                        mMediaPlayerHolder.setPreciseVolume(progress)
                        mDialogBinding.npVolume.setImageResource(
                            ThemeHelper.getPreciseVolumeIcon(
                                progress
                            )
                        )

                        ThemeHelper.updateIconTint(
                            mDialogBinding.npVolume,
                            mResolvedAccentColor
                        )
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    isUserSeeking = true
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    isUserSeeking = false
                    ThemeHelper.updateIconTint(
                        mDialogBinding.npVolume,
                        mResolvedIconsColor
                    )
                }
            })
        }
    }

    private fun MediaPlayerHolder.setStatusPlaying(){
        if (isPlaying) mDialogBinding.imgPlayPause.setIconPlay() else mDialogBinding.imgPlayPause.setIconPause()
    }

    private fun assignView(){
        mDialogBinding.imgPrev.setOnClickListener(mOnClick)
        mDialogBinding.imgNext.setOnClickListener(mOnClick)
        mDialogBinding.imgRepeat.setOnClickListener(mOnClick)
        mDialogBinding.imgPlayPause.setOnClickListener(mOnClick)
        mDialogBinding.imgAbout.setOnClickListener(mOnClick)
        mDialogBinding.imgAddToPlaylist.setOnClickListener(mOnClick)
        mDialogBinding.imgEqualizer.setOnClickListener(mOnClick)
        mDialogBinding.imgFavorite.setOnClickListener(mOnClick)
        mDialogBinding.imgShuffle.setOnClickListener(mOnClick)
        mDialogBinding.imgClose.setOnClickListener(mOnClick)
        mDialogBinding.imgViewQueue.setOnClickListener(mOnClick)

        setSeekBarProgressListener()
    }

    private fun updateNowPlayingInfo(){
        mMediaPlayerHolder.currentSong.first?.let {
            mDialogBinding.seekbarTimer.progress = 0
            mDialogBinding.seekbarTimer.max = it.duration.toInt()
            mDialogBinding.textStart.text = mDialogBinding.seekbarTimer.progress.toLong().toFormattedDuration(isAlbum = false, isSeekBar = false)
            mDialogBinding.textEnd.text = it.duration.toFormattedDuration(isAlbum = false, isSeekBar = false)
            mDialogBinding.textSubTitle.text = it.displayName
            mDialogBinding.textAlbumArtist.text = requireActivity().resources.getString(R.string.artist_and_album, it.artist, it.album)
            mDialogBinding.imageAlbumArt.setImageBitmap(it.getAlbumArt(requireContext()))
        }
        mMediaPlayerHolder.setStatusPlaying()
    }


    private fun updatePlayingStatus(){
        val isPlaying = mMediaPlayerHolder.state != Constants.PAUSED

        if (isPlaying) mDialogBinding.imgPlayPause.setIconPlay() else mDialogBinding.imgPlayPause.setIconPause()
    }


    private fun setSeekBarProgressListener(){

        mMediaPlayerHolder.currentSong.first?.let {
            mDialogBinding.seekbarTimer.max = it.duration.toInt()
            mDialogBinding.seekbarTimer.progress = position
            Log.d("NNN", "NowPlayingDialog: ${position}")
            mDialogBinding.textStart.text = mDialogBinding.seekbarTimer.progress.toLong().toFormattedDuration(isAlbum = false, isSeekBar = false)
            mDialogBinding.textEnd.text = it.duration.toFormattedDuration(isAlbum = false, isSeekBar = false)
        }

        updateRepeatStatus(false)
        mDialogBinding.seekbarTimer.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            val defaultPositionColor = mDialogBinding.textStart.currentTextColor
            var userSelectedPosition = 0
            var isUserSeeking = false

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isUserSeeking){
                    userSelectedPosition = progress
                    position = userSelectedPosition
                    mDialogBinding.textStart.setTextColor(
                        ThemeHelper.resolveThemeAccent(mContext)
                    )
                }
                mDialogBinding.textStart.text =
                    progress.toLong().toFormattedDuration(isAlbum = false, isSeekBar = true)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (isUserSeeking) {
                    mDialogBinding.textStart.setTextColor(defaultPositionColor)
                    mMediaPlayerHolder.onPauseSeekBarCallback()
                    isUserSeeking = false
                }

                if (::mMediaPlayerHolder.isInitialized && mMediaPlayerHolder.isCurrentSong) {
                    Log.d("MMM", "enter if")
                    if (mMediaPlayerHolder.state != Constants.PLAYING) {
                        mediaController.onCurrentPosition(userSelectedPosition)
                        mDialogBinding.seekbarTimer.progress = userSelectedPosition
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


    private fun updateRepeatStatus(onPlaybackCompletion: Boolean) {
        mDialogBinding.imgRepeat.setImageResource(
            ThemeHelper.getRepeatIcon(
                mMediaPlayerHolder
            )
        )
        when {
            onPlaybackCompletion -> ThemeHelper.updateIconTint(
                mDialogBinding.imgRepeat,
                mResolvedAccentColor
            )
            mMediaPlayerHolder.isRepeat1X or mMediaPlayerHolder.isLooping -> {
                ThemeHelper.updateIconTint(
                    mDialogBinding.imgRepeat,
                    mResolvedAccentColor
                )
            }
            else -> ThemeHelper.updateIconTint(
                mDialogBinding.imgRepeat,
                mResolvedIconsColor
            )
        }
    }

    // save current media
    private fun saveSongToPref() {
        if (::mMediaPlayerHolder.isInitialized && !mMediaPlayerHolder.isPlaying || mMediaPlayerHolder.state == Constants.PAUSED) mMediaPlayerHolder.apply {
            MusicOrg.saveLatestSong(currentSong.first, mMediaPlayerHolder, launchedBy)
        }
    }

    private val mOnClick = View.OnClickListener { view ->
        when (view){
            mDialogBinding.imgShuffle -> {
                mediaController.onShuffle()
            }

            mDialogBinding.imgPlayPause -> {
                mediaController.onResumeOrPause()
                mMediaPlayerHolder.setStatusPlaying()
            }
            mDialogBinding.imgClose -> {
                dismiss()
                mediaController.onCancelDialog()
            }
            mDialogBinding.imgNext -> {
                mediaController.onSkip(true)
            }
            mDialogBinding.imgPrev -> {
                mediaController.onSkip(false)
            }
            mDialogBinding.imgRepeat -> {
                mDialogBinding.imgRepeat.setImageResource(ThemeHelper.getRepeatIcon(mMediaPlayerHolder))
                setRepeat()
            }
            mDialogBinding.imgEqualizer -> {
//                openEqualizer()
                dismiss()
                mediaController.onEqualizer()
            }
            mDialogBinding.imgAbout -> {
                val dialog = SampleFragment.newInstance(
                    "Information Music",
                    mMediaPlayerHolder.currentSong.first
                )
                dialog.show(requireActivity().supportFragmentManager, Constants.LIST_DIALOG_FRAGMENT)
            }
            mDialogBinding.imgViewQueue -> {
                val dialog = SampleFragment.newInstance(
                    "List Music",
                    (requireActivity() as HomeActivity).currentListMusic!!.toMutableList(),
                    (requireActivity() as HomeActivity).currentLaunchedBy
                )
                dialog.show(requireActivity().supportFragmentManager, Constants.LIST_DIALOG_FRAGMENT)
            }
        }
    }

    private val mMediaPlayerInterface = object: MediaPlayerInterface{
        override fun onPositionChanged(position: Int) {
            Log.d("NNN", "NowPlayingDialog onPositionChanged: ${position}")
            mDialogBinding.seekbarTimer.progress = position
        }

        override fun onStateChanged() {
            updatePlayingStatus()
            Log.d("NNN", "NowPlayingDialog onStateChanged: ${mMediaPlayerHolder.currentSong.first!!.displayName}")
            if (mMediaPlayerHolder.state != Constants.RESUMED && mMediaPlayerHolder.state != Constants.PAUSED){
                updateNowPlayingInfo()
            }
        }

        override fun onPlaybackCompleted() {
            updateRepeatStatus(true)
        }

        override fun onClose() {

        }

        override fun onUpdateRepeatStatus() {
            updateRepeatStatus(false)
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

}