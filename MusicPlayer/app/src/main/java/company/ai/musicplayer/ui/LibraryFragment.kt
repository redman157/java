package company.ai.musicplayer.ui

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.recyclical.datasource.emptyDataSource
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import company.ai.musicplayer.*
import company.ai.musicplayer.controller.LibrarySelectInterface
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.database.MusicViewModel
import company.ai.musicplayer.databinding.FragmentLibraryBinding
import company.ai.musicplayer.dialog_custom.ListDialog
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.extensions.toMusic
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.MusicOrg
import company.ai.musicplayer.utils.ThemeHelper


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LibraryFragment : Fragment(), LibrarySelectInterface {
    internal class MyLocationListener(
        private val context: Context,
        private val callback: (Location) -> Unit) {

        fun start() {
            // connect to system location service
            Log.d("AAA", "Start Activity: ${(context as Activity).javaClass.simpleName}")
        }

        fun stop() {
            // disconnect from system location service
            Log.d("AAA", "Stop Activity: ${(context as Activity).javaClass.simpleName}")
        }
    }

    private lateinit var mBinding: FragmentLibraryBinding
    private lateinit var mViewPager: ViewPager2
    private lateinit var mTabLayout: TabLayout
    private lateinit var mHeaderView: CollapsingProfileHeaderView
    private lateinit var mFragments: ArrayList<MusicControllerListFragment>

    private var mAllMusic: MutableList<Music>? = null
    private val mDataSource = emptyDataSource()

    private lateinit var mContext: Context
    private lateinit var mUIControlInterface: UIControlInterface

    private lateinit var mSelectMusicInterface: LibrarySelectInterface

    private var mFoldersFragment: MusicControllerListFragment? = null
    private var mAlbumsFragment: MusicControllerListFragment? = null
    private var mMusicsFragment: MusicControllerListFragment? = null
    private var mArtistsFragment: MusicControllerListFragment? = null

    // View model
    private val mMusicViewModel: MusicViewModel by viewModels()

    // Colors
    private val mResolvedAccentColor by lazy { ThemeHelper.resolveThemeAccent(mContext) }
    private val mResolvedAlphaAccentColor by lazy {
        ThemeHelper.getAlphaAccent(
            mContext,
            ThemeHelper.getAlphaForAccent()
        )
    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        mContext = context
        try {
            mSelectMusicInterface = this
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
        mBinding = FragmentLibraryBinding.inflate(layoutInflater, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHeaderView = mBinding.collapseActionView
        mHeaderView.requestLayout()
        mViewPager = mBinding.viewPager
        mTabLayout = mBinding.tabController
        addFragment()
        val music = (activity as HomeActivity).mMediaPlayerHolder.currentSong.first!!
        setUpHeader(music)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager(mViewPager)
    }

    fun setUpHeader(music: Music){
        mHeaderView.applyAttributes(music)
        mBinding.imgAlbumId.setImageBitmap(music.getAlbumArt(requireContext()))
    }

    private fun setupViewPager(viewPager: ViewPager2){
        val pagerAdapter = ScreenSlidePagerAdapter((activity as FragmentActivity))
        viewPager.apply {

            offscreenPageLimit = 4.minus(1)
            adapter = pagerAdapter
            currentItem = 0
        }

        mTabLayout.apply {
            tabIconTint = ColorStateList.valueOf(mResolvedAlphaAccentColor)
//            setupWithViewPager(viewPager)
           /* for (index in 0 until this.tabCount) {
                this.getTabAt(index)?.customView = viewPager.selectTab(index);
            }*/
            TabLayoutMediator(this, viewPager){
                tab, position ->
                    tab.customView = viewPager.selectTab(position)
                    viewPager.setCurrentItem(tab.position, true)

            }.attach()

//            setTabLayoutEnabled(pagerAdapter)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    Log.d("XXX", "vị trí: "+tab.position.toString())
                    val view: View = tab.customView!!
                    val title = view.findViewById<TextView>(R.id.item_tl_text_home)
                    val color = mResolvedAccentColor
                    title.setTextColor(color)
/*                   for (i in 0 until mTabLayout.tabCount) {
                       val view: View = mTabLayout.getTabAt(i)?.customView!!
                       val title = view.findViewById<TextView>(R.id.item_tl_text_home)
                       val color = if (i == tab.position) mResolvedAccentColor else ContextCompat.getColor(requireContext(),R.color.windowBackground)
                       title.setTextColor(color)
                   }*/
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    val view: View = tab.customView!!
                    val title = view.findViewById<TextView>(R.id.item_tl_text_home)
                    val color = ContextCompat.getColor(requireContext(),R.color.windowBackground)
                    title.setTextColor(color)

                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            })

        }
    }


    private fun ViewPager2.selectTab(position: Int): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_tablayout_home, null)
        val title = view.findViewById<TextView>(R.id.item_tl_text_home)
        if (position == 0)
            title.setTextColor(mResolvedAccentColor)
        else
            title.setTextColor(ContextCompat.getColor(requireContext(), R.color.windowBackground))
        title.text = titleSongList()[position]
        return view
    }

    private fun titleSongList(): ArrayList<String> {
        val titles = ArrayList<String>()
        titles.add("AllSong")
        titles.add("Artist")
        titles.add("Album")
        titles.add("Folder")
        return titles
    }

 /*  private inner class ScreenSlidePagerAdapter(fa: FragmentManager): FragmentStateAdapter(fa, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = 4

        override fun getItem(position: Int): Fragment {

            return initFragmentAtPosition(position)
        }

        private fun initFragmentAtPosition(fragmentIndex: Int): Fragment {
            when (fragmentIndex) {
                0 -> {
                    return BlankFragment.newInstance(Constants.ALL_MUSIC_VIEW)
                    if (mMusicsFragment == null) {
                        mMusicsFragment =
                            MusicControllerListFragment.newInstance(
                                Constants.ALL_MUSIC_VIEW,
                                mUIControlInterface,
                                mSelectMusicInterface
                            )
                    }
                    return mMusicsFragment as MusicControllerListFragment
                }
                1 -> {
                    return BlankFragment.newInstance(Constants.ALBUM_VIEW)
                    if (mAlbumsFragment == null) {
                        mAlbumsFragment =
                            MusicControllerListFragment.newInstance(
                                Constants.ALBUM_VIEW,
                                mUIControlInterface,
                                mSelectMusicInterface
                            )
                    }
                    return mAlbumsFragment as MusicControllerListFragment
                }
                2 -> {
                    return BlankFragment.newInstance(Constants.ARTIST_VIEW)
                    if (mArtistsFragment == null) {
                        mArtistsFragment =
                            MusicControllerListFragment.newInstance(
                                Constants.ARTIST_VIEW,
                                mUIControlInterface,
                                mSelectMusicInterface
                            )
                    }
                    return mArtistsFragment as MusicControllerListFragment
                }
                3 -> {
                    return BlankFragment.newInstance(Constants.FOLDER_VIEW)
                    if (mFoldersFragment == null) {
                        mFoldersFragment =
                            MusicControllerListFragment.newInstance(
                                Constants.FOLDER_VIEW,
                                mUIControlInterface,
                                mSelectMusicInterface
                            )
                    }
                    return mFoldersFragment as MusicControllerListFragment
                }
                else -> {
                    return  MusicControllerListFragment.newInstance(Constants.ALL_MUSIC_VIEW)
                    if (mMusicsFragment == null) {
                        mMusicsFragment =
                            MusicControllerListFragment.newInstance(
                                Constants.ALL_MUSIC_VIEW,
                                mUIControlInterface,
                                mSelectMusicInterface
                            )
                    }
                    return mMusicsFragment as MusicControllerListFragment
                }
            }
        }
    }*/
    private lateinit var mActiveFragments: ArrayList<Fragment>
    private fun addFragment(){

        mActiveFragments = ArrayList()
        mActiveFragments.add(BlankFragment.newInstance(Constants.ALL_MUSIC_VIEW))
        mActiveFragments.add(BlankFragment.newInstance(Constants.ALBUM_VIEW))
        mActiveFragments.add(BlankFragment.newInstance(Constants.ARTIST_VIEW))
        mActiveFragments.add(BlankFragment.newInstance(Constants.FOLDER_VIEW))

    }
    // ViewPager2 adapter class
    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment =
            initFragmentAtPosition(position)


        private fun initFragmentAtPosition(fragmentIndex: Int): Fragment {
            when (fragmentIndex) {
                0 -> {
                    return mActiveFragments[0]
                }

                1 -> {
                    return mActiveFragments[1]
                }
                2 -> {
                    return mActiveFragments[2]
                }
                3 -> {
                    return mActiveFragments[3]
                }
                else -> {
                    return mActiveFragments[0]
                }
            }
        }
    }

    override fun onSelectMusic(song: Music?) {
        Log.d("NNN", "LibraryFragment --- onChoose: ${song!!.displayName}")
        song.let {
            mHeaderView.applyAttributes(song)
            mBinding.imgAlbumId.setImageBitmap(it.getAlbumArt(requireContext()))
        }

    }

    override fun onSelectGroup(songs: List<Music>?, launchedBy: String) {
        val dialog = ListDialog.newInstance(launchedBy, songs)
        dialog.show(
            (activity as HomeActivity).supportFragmentManager,
            Constants.LIST_DIALOG_FRAGMENT
        )

    }
    companion object{
        fun newInstance(): Fragment = LibraryFragment()
    }
}