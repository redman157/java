package com.android.music_player.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.R;
import com.android.music_player.adapters.ViewPagerAdapter;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.google.android.material.tabs.TabLayout;

public class AllMusicFragment extends Fragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
    private MusicManager mMusicManager;

    private Toolbar mToolBar;
    private SharedPrefsUtils mSharedPrefsUtils;
    public ImageView profileImage;
    public TextView profileName, profileArtist, profileAlbum;
    private TabLayout mTabLayoutSong;
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPagerSong;
    private View collapsingProfileHeaderView;
    private String songName;
    private View view;
    public static AllMusicFragment newInstance() {
        Bundle args = new Bundle();
        AllMusicFragment fragment = new AllMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("AAA","AllMusicFragment: context: "+context.getClass().getSimpleName());
        //is called when a fragment is connected to an activity.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(getContext());
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_all_song, container, false);
        }
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // is called after onCreateView()
        // and ensures that the fragment's root view is non-null. Any view setup should happen here. E.g., view lookups, attaching listeners.
        // Setup any handles to view objects here
        initView(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // is called when host activity has completed its onCreate() method.
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolBar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViewPager(mViewPagerSong);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setupViewPager(ViewPager viewPager){
        mViewPagerAdapter = new ViewPagerAdapter(getContext(),getActivity().getSupportFragmentManager());
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));
        mViewPagerAdapter.addFragment(new ListMusicFragment(mMusicManager.allSortSongs()));

        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOnClickListener(this);

        mTabLayoutSong.setupWithViewPager(viewPager);

        for (int i = 0; i < mTabLayoutSong.getTabCount(); i++) {
            mTabLayoutSong.getTabAt(i).setCustomView(mViewPagerAdapter.getTabSong(i));
        }
        mTabLayoutSong.addOnTabSelectedListener(this);
    }


    private void initView(View view) {
        collapsingProfileHeaderView = view.findViewById(R.id.collapseActionView);
        mViewPagerSong = view.findViewById(R.id.vp_AllMusic);
        mTabLayoutSong = view.findViewById(R.id.tab_AllMusic);
        profileAlbum = collapsingProfileHeaderView.findViewById(R.id.profileMisc);
        profileImage = collapsingProfileHeaderView.findViewById(R.id.profileImage);
        profileName = collapsingProfileHeaderView.findViewById(R.id.profileName);
        profileArtist = collapsingProfileHeaderView.findViewById(R.id.profileSubtitle);
        mToolBar = view.findViewById(R.id.tb_AllMusic);

        if(!mMusicManager.getCurrentMusic().equals("")){
            MediaMetadataCompat metadataCompat = MusicLibrary.getMetadata(getContext(),
                    mMusicManager.getCurrentMusic());
            setTitle(metadataCompat);
        }
    }

    public void setTitle(MediaMetadataCompat metadataCompat){
        profileAlbum.setText(metadataCompat.getString(Constants.METADATA.Album));
        profileName.setText(metadataCompat.getString(Constants.METADATA.Title));
        profileArtist.setText(metadataCompat.getString(Constants.METADATA.Artist));
        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(
                String.valueOf(MusicLibrary.getAlbumRes(mMusicManager.getCurrentMusic())),profileImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_title_media:
                break;
            case R.id.imbt_Play_media:
//                Utils.isPlayMediaService(this, mMusicManager.getType(), mMusicManager.getPosition());
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayoutSong.getTabCount(); i ++){
            View view = mTabLayoutSong.getTabAt(i).getCustomView();
            TextView title = view.findViewById(R.id.item_tl_text_home);
            int color = (i == tab.getPosition()) ? getResources().getColor(R.color.red) :
                    getResources().getColor(R.color.white);
            title.setTextColor(color);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
