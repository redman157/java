package company.ai.musicplayer.fragment

import company.ai.musicplayer.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.afollestad.recyclical.datasource.emptyDataSource
import com.google.android.material.tabs.TabLayout
import company.ai.musicplayer.MusicViewModel
import company.ai.musicplayer.activiy.ActionBarCastActivity
import company.ai.musicplayer.databinding.CollapsingProfileHeaderBinding
import company.ai.musicplayer.databinding.FragmentLibraryBinding
import company.ai.musicplayer.models.Music

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LibraryFragment : Fragment() {
    private lateinit var mBinding: FragmentLibraryBinding
    private lateinit var mViewPager: ViewPager
    private lateinit var mTabLayout: TabLayout
    private lateinit var mBackGroundHeader: ImageView
    private lateinit var mHeaderView: CollapsingProfileHeaderBinding

    // View model
    private lateinit var mMusicViewModel: MusicViewModel
    private var mAllMusic: MutableList<Music>? = null
    private val mDataSource = emptyDataSource()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentLibraryBinding.inflate(layoutInflater)
        (activity as ActionBarCastActivity).initializeToolbar(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHeaderView = mBinding.collapseActionView
        mViewPager = mBinding.viewPager
        mTabLayout = mBinding.tabController
        mBackGroundHeader = mBinding.imgAlbumId

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewPager(mViewPager)
    }

    private fun setupViewPager(viewPager: ViewPager){

    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity ): FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

        override fun createFragment(position: Int): Fragment {
            TODO("Not yet implemented")
        }

    }
}