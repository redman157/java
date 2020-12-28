package company.ai.musicplayer.ui

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.afollestad.recyclical.datasource.emptyDataSource
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import company.ai.musicplayer.*
import company.ai.musicplayer.controller.LibrarySelectInterface
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.databinding.FragmentLibraryBinding
import company.ai.musicplayer.dialog_custom.ListDialog
import company.ai.musicplayer.extensions.imageByPicasso
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.ThemeHelper


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LibraryFragment : Fragment(), LibrarySelectInterface {
    private lateinit var mBinding: FragmentLibraryBinding
    private lateinit var mViewPager: ViewPager
    private lateinit var mTabLayout: TabLayout
    private lateinit var mBackGroundHeader: ImageView
    private lateinit var mHeaderView: CollapsingProfileHeaderView
    private lateinit var mFragments: ArrayList<MusicControllerListFragment>
    // View model
    private lateinit var mMusicViewModel: MusicViewModel
    private var mAllMusic: MutableList<Music>? = null
    private val mDataSource = emptyDataSource()

    private lateinit var mContext: Context
    private lateinit var mUIControlInterface: UIControlInterface

    private lateinit var onSelectMusic: LibrarySelectInterface

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
            onSelectMusic = this
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
        mBinding = FragmentLibraryBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHeaderView = mBinding.collapseActionView
//        mHeaderView.layoutParams.behavior = ScrollingViewBehavior()
        mHeaderView.requestLayout()
        mViewPager = mBinding.viewPager
        mTabLayout = mBinding.tabController
        mBackGroundHeader = mBinding.imgAlbumId
        (activity as HomeActivity).songOri.let {
            mHeaderView.applyAttributes(it)
            mBinding.imgAlbumId.imageByPicasso(it.albumID)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViewPager(mViewPager)
    }

    private fun setupViewPager(viewPager: ViewPager){
        setActivityFragment()
        val pagerAdapter = ScreenSlidePagerAdapter((activity as FragmentActivity).supportFragmentManager)
        viewPager.apply {
            offscreenPageLimit = mFragments.size.minus(1)
            adapter = pagerAdapter
            currentItem = 0
        }

        mTabLayout.apply {
            tabIconTint = ColorStateList.valueOf(mResolvedAlphaAccentColor)
            setupWithViewPager(viewPager)
            for (index in 0 until this.tabCount) {
                this.getTabAt(index)?.customView = viewPager.selectTab(index);
            }
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                   for (i in 0 until mTabLayout.tabCount) {
                       val view: View = mTabLayout.getTabAt(i)?.customView!!
                       val title = view.findViewById<TextView>(R.id.item_tl_text_home)
                       val color = if (i == tab.position) mResolvedAccentColor else mResolvedAlphaAccentColor
                       title.setTextColor(color)
                   }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {

                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            })

        }
    }

    private fun ViewPager.selectTab(position: Int): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_tablayout_home, null)
        val title = view.findViewById<TextView>(R.id.item_tl_text_home)
        if (position == 0)
            title.setTextColor(mResolvedAccentColor)
        else
            title.setTextColor(mResolvedAlphaAccentColor)
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


    private fun setActivityFragment(){
        mFragments = ArrayList()
        if (::mFragments.isInitialized) {
            mFragments.add(MusicControllerListFragment.newInstance(Constants.ALL_MUSIC_VIEW, mUIControlInterface, onSelectMusic))
            mFragments.add(MusicControllerListFragment.newInstance(Constants.ARTIST_VIEW, mUIControlInterface, onSelectMusic))
            mFragments.add(MusicControllerListFragment.newInstance(Constants.ALBUM_VIEW, mUIControlInterface, onSelectMusic))
            mFragments.add(MusicControllerListFragment.newInstance(Constants.FOLDER_VIEW, mUIControlInterface, onSelectMusic))

            }
        }

    private inner class ScreenSlidePagerAdapter(fa: FragmentManager): FragmentStatePagerAdapter(fa, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        override fun getCount(): Int = mFragments.size

        override fun getItem(position: Int): Fragment = mFragments[position]

    }

    override fun onSelectMusic(song: Music?) {
        Log.d("NNN", "LibraryFragment --- onChoose: ${song!!.displayName}")
        song.let {
            mHeaderView.applyAttributes(song)
            mBinding.imgAlbumId.imageByPicasso(it.albumID)
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