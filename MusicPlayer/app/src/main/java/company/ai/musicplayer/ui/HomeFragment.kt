package company.ai.musicplayer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
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
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.FragmentHomeBinding
import company.ai.musicplayer.extensions.*
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.ListsHelper
import kotlinx.android.synthetic.main.custom_preference_category.*


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class HomeFragment : Fragment() {
    private lateinit var mBinding: FragmentHomeBinding
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
    
    private val isSettingsFragment get() = requireActivity().supportFragmentManager.isFragment(Constants.TAG_FRAGMENT)
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

    private fun initializeToolbar() {
        mBinding.toolbarContainer.toolbar.apply {
            popupTheme = R.style.AppTheme_CustomActionBar
            title = this@HomeFragment.getString(R.string.app_name)
            inflateMenu(R.menu.main)
            setHasOptionsMenu(true)
            (activity as AppCompatActivity).setSupportActionBar(this)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // If not handled by drawerToggle, home needs to be handled by returning to previous
        when (item.itemId) {
            android.R.id.home -> {
            }
            R.id.night_mode -> {
            }
            R.id.settings -> {

                requireActivity().supportFragmentManager.addFragment(
                    SettingsFragment.newInstance(),
                    Constants.TAG_FRAGMENT,
                    isSettingsFragment
                )
            }
            R.id.changeTheme -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentHomeBinding.inflate(layoutInflater)
        initializeToolbar()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        mMusicViewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
        mMusicViewModel.mDeviceMusic.observe(
            viewLifecycleOwner,
            Observer { returnedMusic ->
                if (!returnedMusic.isNullOrEmpty()) {
                    mAllMusic = ListsHelper.getRecentlyMusicAdd(returnedMusic)
                    Log.d("XXX", "HomeFragment: ${mAllMusic!!.size}")
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

    private fun fetchAccentColor(): Int {
        val typedValue = TypedValue()
        val a: TypedArray =
            requireActivity().obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    private fun initView(){
        mImageShuffle = mBinding.imageShuffleAll
        mImageMost = mBinding.imageMostPlayer
        mImageRecently = mBinding.imageRecentlyAdd
        mLinearMusicMost = mBinding.linearMusicMost
        mLinearPlayer1 = mBinding.linearPlayer1
        mLinearPlayer2 = mBinding.linearPlayer2
        mRecentlyAdd = mBinding.recyclerRecentlyAdd
        mBinding.refreshData.apply {
            setColorSchemeColors(fetchAccentColor())
            setOnRefreshListener {
                mBinding.refreshData.isRefreshing = false
                mMusicViewModel.syncMusic(requireActivity().application)
                setMusicDataSource(ListsHelper.recentlyMusic)
                requireActivity().supportFragmentManager.addFragment(
                    newInstance(),
                    Constants.TAG_FRAGMENT,
                    isSettingsFragment
                )

            }
        }
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
                    onBind(::MusicsViewHolder){ _, item ->
                        title.text = item.title
                        duration.text = item.duration.toFormattedDuration(
                            isAlbum = false,
                            isSeekBar = false
                        )
                        subtitle.text = getString(
                            R.string.artist_and_album,
                            item.artist,
                            item.album
                        )
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
    companion object{
        fun newInstance(): Fragment = HomeFragment()
    }
}