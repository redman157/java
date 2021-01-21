package company.ai.musicplayer.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import company.ai.musicplayer.ListViewHolder
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.FragmentSampleBinding
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.extensions.imageByPicasso
import company.ai.musicplayer.extensions.toFormattedDuration
import company.ai.musicplayer.models.Music


/**
 * A simple [Fragment] subclass.
 * Use the [SampleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SampleFragment : DialogFragment {
    // TODO: Rename and change types of parameters
    private lateinit var mBinding: FragmentSampleBinding
    private var music: Music? = null
    private var list: MutableList<Music>? = null
    private var title: String
    private lateinit var launchedBy: String
    private var mDataSource = emptyDataSource()
    private lateinit var mUIControlInterface: UIControlInterface

    constructor(title: String, music: Music?) {
        this.title = title
        this.music = music
    }
    constructor(title: String, list: MutableList<Music>?, launchedBy: String){
        this.title = title
        this.list = list
        this.launchedBy = launchedBy
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            setupRatio()
        }
        return dialog
    }

    private fun setupRatio() {
        //id = com.google.android.material.R.id.design_bottom_sheet for Material Components
        //id = android.support.design.R.id.design_bottom_sheet for support librares

        val bottomSheet = mBinding.root
        // set height dialog bottom
        if (list != null && list!!.size > 6) {
            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = getBottomSheetDialogDefaultHeight()
            bottomSheet.layoutParams = layoutParams
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSampleBinding.inflate(layoutInflater)
        mUIControlInterface = activity as UIControlInterface
        return mBinding.root
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle? ){
        super.onViewCreated(view,savedInstanceState)
        initView()
    }

    private fun setMusicDataSource(musicList: List<Music>?) {
        musicList?.apply {
            mDataSource.set(this)
        }
    }

    private fun initView(){
        mBinding.textTitle.text = title
        if (music != null){
            mBinding.textAbout.text = music?.let {
                "Name: ${it.displayName!!.split("-")[0]} \n" +
                "Artist: ${it.artist} \n" +
                "Album: ${it.album}\n" +
                "Path: ${it.relativePath}\n" +
                "Time: ${it.duration.toFormattedDuration(isAlbum = false, isSeekBar = false)}"
            }
        }
        if (list!= null){
            setMusicDataSource(list)
            mBinding.recyclerList.apply {
                setup {
                    withDataSource(mDataSource)
                    withLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false))
                    withItem<Music, ListViewHolder>(R.layout.item_select_music){
                        onBind(::ListViewHolder){ _, item ->
                            title.text = item.displayName
                            image.setImageBitmap(item.getAlbumArt(requireContext()))
                        }
                        onClick {
                            mUIControlInterface.onSongSelected(item, list, launchedBy)
                            dismiss()
                        }
                    }
                }
            }
        }
        mBinding.imgClose.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SampleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(title: String, music: Music?) = SampleFragment(title,music)
        fun newInstance(title: String, list: MutableList<Music>?, launchedBy: String) = SampleFragment(title, list, launchedBy)
    }
}