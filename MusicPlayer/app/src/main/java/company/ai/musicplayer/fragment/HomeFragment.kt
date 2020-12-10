package company.ai.musicplayer.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import company.ai.musicplayer.MusicViewModel
import company.ai.musicplayer.MusicsViewHolder
import company.ai.musicplayer.R
import company.ai.musicplayer.activiy.ActionBarCastActivity
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.FragmentHomeBinding
import company.ai.musicplayer.extensions.afterMeasured
import company.ai.musicplayer.extensions.imageByPicasso
import company.ai.musicplayer.extensions.toFormattedDuration
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.ListsHelper

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {
    private lateinit var mHomeFragment: FragmentHomeBinding
    // View model
    private lateinit var mMusicViewModel: MusicViewModel

    private var mAllMusic: MutableList<Music>? = null
    private val mDataSource = emptyDataSource()

    private lateinit var mSortMenuItem: MenuItem

    private var mSorting = mPreferences.allMusicSorting

    private var sIsFastScroller = false
    private val sIsFastScrollerVisible get() = sIsFastScroller && mSorting != Constants.DEFAULT_SORTING

    private var sLandscape = false

    private lateinit var mUIControlInterface: UIControlInterface

//    private val mResolvedAccentColor by lazy { Theme.resolveThemeAccent(requireActivity()) }

    private lateinit var mImageShuffle: ImageView
    private lateinit var mImageMost: ImageView
    private lateinit var mImageRecently: ImageView
    private lateinit var mLinearMusicMost: LinearLayout
    private lateinit var mLinearPlayer1: LinearLayout
    private lateinit var mLinearPlayer2: LinearLayout
    private lateinit var mRecentlyAdd: RecyclerView
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mUIControlInterface = activity as UIControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mHomeFragment = FragmentHomeBinding.inflate(layoutInflater)
        (activity as ActionBarCastActivity).initializeToolbar(true)
        return mHomeFragment.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mMusicViewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
        mMusicViewModel.mDeviceMusic.observe(
            viewLifecycleOwner,
            Observer {returnedMusic ->
                if (!returnedMusic.isNullOrEmpty()){
                    mAllMusic = ListsHelper.getSortedMusicList(Constants.DESCENDING_SORTING, mMusicViewModel.mDeviceMusicFiltered)
                    setMusicDataSource(mAllMusic)
                    assignView()
                }
            })
    }

    private fun setMusicDataSource(musicList: List<Music>?) {
        musicList?.apply {
            mDataSource.set(this)
        }
    }

    private fun initView(){
        mImageShuffle = mHomeFragment.imageShuffleAll
        mImageMost = mHomeFragment.imageMostPlayer
        mImageRecently = mHomeFragment.imageRecentlyAdd
        mLinearMusicMost = mHomeFragment.linearMusicMost
        mLinearPlayer1 = mHomeFragment.linearPlayer1
        mLinearPlayer2 = mHomeFragment.linearPlayer2
        mRecentlyAdd = mHomeFragment.recyclerRecentlyAdd
    }

    private fun assignView(){
        mRecentlyAdd.apply {
            // setup{} is an extension method on RecyclerView
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            setup {
                // item is a `val` in `this` here
                withDataSource(mDataSource)
                withItem<Music, MusicsViewHolder>(R.layout.item_music_line){
                    onBind(::MusicsViewHolder){_, item ->
                        title.text = item.title
                        duration.text = item.duration.toFormattedDuration(
                            isAlbum = false,
                            isSeekBar = false
                        )
                        subtitle.text = getString(R.string.artist_and_album, item.artist, item.album)
                        icon.imageByPicasso(item.albumID)
                    }
                    onClick {
                        mUIControlInterface.onSongSelected(
                            item,
                            mAllMusic,
                            Constants.ARTIST_VIEW
                        )
                    }
                }
            }
        }
        setupIndicatorFastScrollerView()
    }

    @SuppressLint("DefaultLocale")
    private fun setupIndicatorFastScrollerView() {

        // Set indexes if artists rv is scrollable
        mRecentlyAdd.afterMeasured{
            sIsFastScroller = computeVerticalScrollRange() > height
            if (sIsFastScroller){

            }
        }
    }
}