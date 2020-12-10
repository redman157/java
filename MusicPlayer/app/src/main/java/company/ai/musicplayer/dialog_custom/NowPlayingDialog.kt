package company.ai.musicplayer.dialog_custom

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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.MediaControllerInterface
import company.ai.musicplayer.databinding.DialogMediaBinding
import company.ai.musicplayer.extensions.imageByPicasso
import company.ai.musicplayer.extensions.toFormattedDuration
import company.ai.musicplayer.player.MediaPlayerHolder
import company.ai.musicplayer.player.MediaPlayerInterface
import company.ai.musicplayer.service.PlayerService
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.ThemeHelper

class NowPlayingDialog(var mPlayerService: PlayerService,var position: Int): BottomSheetDialogFragment(), View.OnClickListener, View.OnLongClickListener,MediaPlayerInterface {
    private lateinit var mDialogMedia: DialogMediaBinding
    private lateinit var mContext: Context
    private lateinit var mediaController: MediaControllerInterface
    private lateinit var mMediaPlayerHolder: MediaPlayerHolder
    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }


    override fun onAttach(context: Context){
        super.onAttach(context)
        mContext = context
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
        return mDialogMedia.root
    }

    fun isShowing(): Boolean? {
        return dialog?.isShowing
    }

    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)
        mediaController.onDismissDialog()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        assignView()
        mMediaPlayerHolder.mediaPlayerInterface = this
    }


    override fun onClick(view: View) {
        when (view){

        }
    }

    override fun onLongClick(view: View): Boolean {

        return true
    }

    private fun initView(){
        mMediaPlayerHolder.currentSong.first?.let {
            mDialogMedia.textDisplayName.text = it.displayName
            mDialogMedia.textAlbumArtist.text = requireActivity().resources.getString(R.string.artist_and_album, it.artist, it.album)
            mDialogMedia.imageAlbumArt.imageByPicasso(it.albumID)
        }
    }

    private fun assignView(){
        mDialogMedia.imagePrev.setOnClickListener(this)
        mDialogMedia.imageNext.setOnClickListener(this)
        mDialogMedia.imageReplay.setOnClickListener(this)
        mDialogMedia.imagePlayPause.setOnClickListener(this)
        mDialogMedia.imageMore.setOnClickListener(this)
        mDialogMedia.imageAbout.setOnClickListener(this)
        mDialogMedia.imageSetTime.setOnClickListener(this)
        mDialogMedia.imageAddToPlaylist.setOnClickListener(this)
        mDialogMedia.imageEqualizer.setOnClickListener(this)
        mDialogMedia.imageFavorite.setOnClickListener(this)
        mDialogMedia.imageShuffle.setOnClickListener(this)
        mDialogMedia.imageClosePanel.setOnClickListener(this)
        mDialogMedia.imageViewQueue.setOnClickListener(this)

        setSeekBarProgressListener()
    }

    private fun setSeekBarProgressListener(){
        val max = mMediaPlayerHolder.currentSong.first!!.duration
        mDialogMedia.seekbarTimer.max = max.toInt()
        mDialogMedia.seekbarTimer.progress = position
        mDialogMedia.textStart.text = position.toLong().toFormattedDuration(isAlbum = false, isSeekBar = false)
        mDialogMedia.textEnd.text = max.toFormattedDuration(isAlbum = false, isSeekBar = false)
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
        mDialogMedia.seekbarTimer.progress = position
    }

    override fun onStateChanged() {
        TODO("Not yet implemented")
    }

    override fun onPlaybackCompleted() {
        TODO("Not yet implemented")
    }

    override fun onClose() {
        TODO("Not yet implemented")
    }

    override fun onUpdateRepeatStatus() {
        TODO("Not yet implemented")
    }

    override fun onQueueEnabled() {
        TODO("Not yet implemented")
    }

    override fun onQueueCleared() {
        TODO("Not yet implemented")
    }

    override fun onQueueStartedOrEnded(started: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSaveSong() {
        TODO("Not yet implemented")
    }

    override fun onFocusLoss() {
        TODO("Not yet implemented")
    }

    override fun onPlaylistEnded() {
        TODO("Not yet implemented")
    }

}