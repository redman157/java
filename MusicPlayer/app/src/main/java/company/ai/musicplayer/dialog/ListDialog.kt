package company.ai.musicplayer.dialog_custom

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.ai.musicplayer.ListViewHolder
import company.ai.musicplayer.database.MusicViewModel
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.FragmentListBinding
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.ui.LibraryFragment
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants


/**
 * A simple [Fragment] subclass.
 * Use the [ListDialog.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListDialog(var launchedBy: String, var musics: List<Music>?) : BottomSheetDialogFragment() {
    private lateinit var mBinding: FragmentListBinding
    // View model
    private lateinit var mMusicViewModel: MusicViewModel
    private lateinit var mUiControlInterface: UIControlInterface

    private val mDataSource = emptyDataSource()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        for (index in musics!!){
            Log.d("CCC", "ListDialog: ${index.displayName}")
        }
        mBinding = FragmentListBinding.inflate(layoutInflater)
        mUiControlInterface = activity as UIControlInterface
        mMusicViewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
        mMusicViewModel.mDeviceMusic.observe(
            viewLifecycleOwner,
            Observer { returnedMusic ->
                if (!returnedMusic.isNullOrEmpty()) {
                    setMusicDataSource(musics!!.toMutableList())
                    assignView()
                }
            })
        return mBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    override fun onDismiss(dialog: DialogInterface){
        super.onDismiss(dialog)
    }

    private fun setMusicDataSource(musicList: List<Music>?) {
        musicList?.apply {
            mDataSource.set(this)
        }
    }

    @Override
    override fun setupDialog(dialog: Dialog, style: Int) {

    }

    private lateinit var behavior: BottomSheetBehavior<*>
    private val callBack = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        //id = com.google.android.material.R.id.design_bottom_sheet for Material Components
        //id = android.support.design.R.id.design_bottom_sheet for support librares

        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        behavior = BottomSheetBehavior.from(bottomSheet)

        // set height dialog bottom
        if (musics!!.size > 6) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = getBottomSheetDialogDefaultHeight()
            bottomSheet.layoutParams = layoutParams
        }
        behavior.apply {
            isDraggable = false
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true
            isHideable = true
            peekHeight = 100
            addBottomSheetCallback (callBack)
        }

    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        Log.d("CCC"," getWindowHeight(): ${ getWindowHeight()}")
        return getWindowHeight() * 40 / 100
    }

    private fun getWindowHeight(): Int {
        // Calculate window height for fullscreen use
        val displayMetrics = DisplayMetrics()
        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun assignView(){
        mBinding.imgClose.apply {
            setOnClickListener {
                this@ListDialog.dismiss()
            }
        }
        mBinding.textTitle.apply {
            when (launchedBy){
                Constants.FOLDER_VIEW -> {
                    text = getString(R.string.folder_list)
                }
                Constants.ARTIST_VIEW -> {
                    text = getString(R.string.album_list)
                }
                Constants.ALBUM_VIEW -> {
                    text = getString(R.string.album_list)
                }
            }
        }
        mBinding.recyclerList.apply {
//            isNestedScrollingEnabled = false
            setup {
                withDataSource(mDataSource)
                withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
                withItem<Music, ListViewHolder>(R.layout.item_select_music){
                    onBind(::ListViewHolder) { _, item ->
                        // GenericViewHolder is `this` here
                        image.setImageBitmap(item.getAlbumArt(requireContext()))
                        title.text = item.displayName!!.split("-")[0]
                    }
                    onClick {
                        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(Constants.TAG_FRAGMENT)
                        if (fragment is LibraryFragment){
                            fragment.onSelectMusic(item)
                        }
                        this@ListDialog.dismiss()
                        mUiControlInterface.onSongSelected(item,musics, launchedBy)
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG_LAUNCHED_BY = "SELECTED_FRAGMENT"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(launchedBy: String, list: List<Music>?) =
            ListDialog(launchedBy, list).apply {
                arguments = Bundle().apply {
                    putString(TAG_LAUNCHED_BY, launchedBy)
                }
            }
    }
}