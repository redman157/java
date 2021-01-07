package company.ai.musicplayer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import company.ai.musicplayer.*
import company.ai.musicplayer.controller.LibrarySelectInterface
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.database.MusicViewModel
import company.ai.musicplayer.databinding.FragmentMusicControllerListBinding
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.extensions.toFormattedDuration
import company.ai.musicplayer.extensions.toMusic
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.ListsHelper

/**
 * A simple [Fragment] subclass.
 * Use the [MusicControllerListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicControllerListFragment(
  /*  var mLaunchedBy: String,
    var mUiControlInterface: UIControlInterface,
    var mSelectMusicInterface: LibrarySelectInterface*/
    private var mLaunchedBy: String
    ) : Fragment() {
    private lateinit var mUiControlInterface: UIControlInterface
    lateinit var mSelectMusicInterface: LibrarySelectInterface
    private lateinit var mBinding: FragmentMusicControllerListBinding
    private lateinit var mMusicViewModel: MusicViewModel
    private val mDataSource = emptyDataSource()
    private var mAllMusic: MutableList<Music>? = null

    @SuppressLint("SetTextI18n")
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
                                Log.d("NNN","${music!!.displayName!!.split("-")[0]}")
                                title.text = music!!.displayName!!.split(" - ")[0]
                                subtitle.text = getString(R.string.artist_and_album, music.artist, music.album)

                                icon.setImageBitmap(music.getAlbumArt(requireContext()))
                                duration.text = music.duration.toFormattedDuration(
                                    isAlbum = false,
                                    isSeekBar = false
                                )
                            }
                            onClick {
                                val music = item.toMusic(mAllMusic)
                                mSelectMusicInterface.onSelectMusic(music)
                                mUiControlInterface.onSongSelected(music,mAllMusic, mLaunchedBy)
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
                                mSelectMusicInterface.onSelectGroup(mMusicViewModel.mDeviceMusicByFolder?.getValue(item), mLaunchedBy)
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
                                    mSelectMusicInterface.onSelectGroup(mMusicViewModel.mDeviceMusicByAlbum?.getValue(item), mLaunchedBy)
                                }
                            }else{
                                onClick {
                                    mSelectMusicInterface.onSelectGroup(mMusicViewModel.mDeviceMusicByArtist?.getValue(item), mLaunchedBy)
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mUiControlInterface = activity as UIControlInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentMusicControllerListBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       /* mMusicViewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
        mMusicViewModel.mDeviceMusic.observe(
            viewLifecycleOwner,
            {returnedMusic ->
                if (!returnedMusic.isNullOrEmpty()){
                    setMusicDataSource(setupData(mLaunchedBy))
                    assignView()
                }
            })*/
    }

    private fun setMusicDataSource(musicList: List<String>?) {
        musicList?.apply {
            mDataSource.set(this)
        }
    }

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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicControllerListFragment.
         */
        private const val TAG_LAUNCHED_BY = "SELECTED_FRAGMENT"
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(launchedBy: String): MusicControllerListFragment {
//            MusicControllerListFragment(launchedBy,uiControlInterface, librarySelectInterface)

            return MusicControllerListFragment(launchedBy)
        }
    }
}