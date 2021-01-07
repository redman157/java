package company.ai.musicplayer.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import company.ai.musicplayer.*
import company.ai.musicplayer.controller.LibrarySelectInterface
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.database.MusicViewModel
import company.ai.musicplayer.databinding.FragmentBlankBinding
import company.ai.musicplayer.databinding.FragmentMusicControllerListBinding
import company.ai.musicplayer.dialog_custom.ListDialog
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.extensions.toFormattedDuration
import company.ai.musicplayer.extensions.toMusic
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.ListsHelper

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mLaunchedBy: String
    private lateinit var mUIControlInterface: UIControlInterface
    private lateinit var mSelectMusicInterface: LibrarySelectInterface
    private lateinit var mBinding: FragmentBlankBinding
    private lateinit var mMusicViewModel: MusicViewModel
    private val mDataSource = emptyDataSource()
    override fun onAttach(context: Context) {
        super.onAttach(context)

        arguments?.getString(TAG_LAUNCHED_BY)?.let { launchedBy ->
            mLaunchedBy = launchedBy
        }

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            val mFragment = requireActivity().supportFragmentManager.findFragmentByTag(Constants.TAG_FRAGMENT)
            if (mFragment is LibraryFragment){
                mSelectMusicInterface = mFragment as LibrarySelectInterface
            }
            mUIControlInterface = activity as UIControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentBlankBinding.inflate(layoutInflater)
        return mBinding.root
//        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMusicViewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
        mMusicViewModel.mDeviceMusic.observe(
           viewLifecycleOwner,
           {returnedMusic ->
               if (!returnedMusic.isNullOrEmpty()){
                   setMusicDataSource(setupData(mLaunchedBy))
                   assignView()
               }
           })
    }
    private var mAllMusic: MutableList<Music>? = null

    private fun setupData(launchedBy: String):  List<String>? {
        when (launchedBy) {
            Constants.ALL_MUSIC_VIEW -> {
                val allMusic: ArrayList<String> = ArrayList()
                mAllMusic = ListsHelper.getSortedMusicList(mPreferences.allMusicSorting, mMusicViewModel.mDeviceMusicFiltered)
                for (music in mAllMusic!!){
                    music.displayName?.let { allMusic.add(it) }
                }
                return allMusic
            }
            Constants.ARTIST_VIEW -> {
                return ListsHelper.getSortedList(
                    mPreferences.artistsSorting,
                    mMusicViewModel.mDeviceAlbumsByArtist?.keys?.toMutableList())

            }
            Constants.ALBUM_VIEW -> {
                return ListsHelper.getSortedListWithNull(
                    mPreferences.albumsSorting,
                    mMusicViewModel.mDeviceMusicByAlbum?.keys?.toMutableList()
                )
            }
            else -> {
                return  ListsHelper.getSortedList(
                    mPreferences.foldersSorting,
                    mMusicViewModel.mDeviceMusicByFolder?.keys?.toMutableList()
                )
            }
        }
    }


    private fun setMusicDataSource(musicList: List<String>?) {
        musicList?.apply {
            mDataSource.set(this)
        }
    }

    private fun assignView(){
        mBinding.rcControllerMusic.apply {
//            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
//            isNestedScrollingEnabled = false
            // setup{} is an extension method on RecyclerView
            setup {
                // item is a `val` in `this` here
                withDataSource(mDataSource)
                when (mLaunchedBy) {
                    Constants.ALL_MUSIC_VIEW -> {
                        withLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false))
                        withItem<String, MusicsViewHolder>(R.layout.item_music_line) {
                            onBind(::MusicsViewHolder) { _, item ->
                                // GenericViewHolder is `this` here
                                val music = item.toMusic(mAllMusic)
                                title.text = music!!.let { it.displayName!!.removeRange(it.displayName.length - 4, it.displayName.length) }
                                subtitle.text = getString(R.string.artist_and_album, music.artist, music.album)

                                icon.setImageBitmap(music.getAlbumArt(requireContext()))
                                duration.text = music.duration.toFormattedDuration(
                                    isAlbum = false,
                                    isSeekBar = false
                                )
                            }
                            onClick {
                                val music = item.toMusic(mAllMusic)
                                mUIControlInterface.onSongSelected(music,mAllMusic, mLaunchedBy)
                                mSelectMusicInterface.onSelectMusic(music)
                               /* mSelectMusicInterface.onSelectMusic(music)
                                mUiControlInterface.onSongSelected(music,mAllMusic, mLaunchedBy)*/
                            }
                        }

                    }
                    Constants.FOLDER_VIEW -> {
                        withItem<String, GenericViewHolder>(R.layout.item_music_folder) {
                            onBind(::GenericViewHolder) { _, item ->
                                // GenericViewHolder is `this` here
                                title.text = item
                                icon.setImageResource(R.drawable.ic_folder)
                                subtitle.text =  "${mMusicViewModel.mDeviceMusicByFolder?.getValue(item)?.size} songs"
                            }
                            onClick {
                                val dialog = ListDialog.newInstance(mLaunchedBy, mMusicViewModel.mDeviceMusicByFolder?.getValue(item))
                                dialog.show(
                                    (activity as HomeActivity).supportFragmentManager,
                                    Constants.LIST_DIALOG_FRAGMENT
                                )
//                                mSelectMusicInterface.onSelectGroup(mMusicViewModel.mDeviceMusicByFolder?.getValue(item), mLaunchedBy)
                            }
                        }
                    }
                    else -> {
                        withItem<String, AlbumArtistViewHolder>(R.layout.item_music_artist_and_album) {
                            onBind(::AlbumArtistViewHolder) { _, item ->
                                // GenericViewHolder is `this` here
                                title.text = item
                                if (mLaunchedBy == Constants.ALBUM_VIEW){
                                    subtitle.text =  "${mMusicViewModel.mDeviceMusicByAlbum?.getValue(item)?.size} songs"
                                    icon.setImageResource(R.drawable.ic_music_album)

                                }else{
                                    icon.setImageResource(R.drawable.ic_artists)
                                    subtitle.text =  "${mMusicViewModel.mDeviceMusicByArtist?.getValue(item)?.size} songs"

                                }

                            }
                            if (mLaunchedBy == Constants.ALBUM_VIEW){
                                onClick {
                                    val dialog = ListDialog.newInstance(mLaunchedBy, mMusicViewModel.mDeviceMusicByAlbum?.getValue(item))
                                    dialog.show(
                                        (activity as HomeActivity).supportFragmentManager,
                                        Constants.LIST_DIALOG_FRAGMENT
                                    )
//                                    mSelectMusicInterface.onSelectGroup(mMusicViewModel.mDeviceMusicByAlbum?.getValue(item), mLaunchedBy)
                                }
                            }else{
                                onClick {
                                    val dialog = ListDialog.newInstance(mLaunchedBy, mMusicViewModel.mDeviceMusicByArtist?.getValue(item))
                                    dialog.show(
                                        (activity as HomeActivity).supportFragmentManager,
                                        Constants.LIST_DIALOG_FRAGMENT
                                    )
//                                    mSelectMusicInterface.onSelectGroup(mMusicViewModel.mDeviceMusicByArtist?.getValue(item), mLaunchedBy)
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */
        private const val TAG_LAUNCHED_BY = "SELECTED_FRAGMENT"
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(launchedBy: String) =
            BlankFragment().apply {
                arguments = Bundle().apply {
                    putString(TAG_LAUNCHED_BY, launchedBy)
                }
            }
    }
}