package com.android.music_player.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.R;
import com.android.music_player.activities.EqualizerActivity;
import com.android.music_player.activities.SearchActivity;
import com.android.music_player.activities.SettingsActivity;
import com.android.music_player.activities.SplashActivity;
import com.android.music_player.activities.TimerActivity;
import com.android.music_player.adapters.ViewPagerAdapter;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.google.android.material.tabs.TabLayout;

public class AllMusicFragment extends Fragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener {
    private MusicManager mMusicManager;
    private ImageButton mBtnPlayPause;
    private Toolbar mToolBar;
    public LinearLayout mLl_Play_Media;
    public ImageView mImgAlbumId;
    private SharedPrefsUtils mSharedPrefsUtils;
    public ImageView profileImage;
    public TextView profileName, profileArtist, profileAlbum;
    private TabLayout mTabLayoutSong;
    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPagerSong;
    private View collapsingProfileHeaderView;
    private Context context;

    public static AllMusicFragment newInstance() {
        Bundle args = new Bundle();
        AllMusicFragment fragment = new AllMusicFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        this.context = context;

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
        return inflater.inflate(R.layout.fragment_all_song, container, false);
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


//        mBtnPlayPause.setOnClickListener(this);
//        mLl_Play_Media.setOnClickListener(this);
        setupViewPager(mViewPagerSong);
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

     /*   mBtnPlayPause = view.findViewById(R.id.imbt_Play_media);
        mBtnTitle = view.findViewById(R.id.btn_title_media);
        mLl_Play_Media = view.findViewById(R.id.ll_play_media);
        mImgAlbumId = view.findViewById(R.id.img_AlbumId);*/
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ((AppCompatActivity)getActivity()).getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("AAA", "ASdasdaksjhdkjasnd");
               /* ((HomeActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
                 Fragment allMusicFragment =
                        ((FragmentActivity)getActivity()).getSupportFragmentManager().findFragmentByTag(
                                "MainFragment");

                FragmentTransaction transaction =
                        ((FragmentActivity)getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(((HomeActivity)getActivity()).mLayoutPlaceHolder.getId(),
                        allMusicFragment);
                transaction.commit();*/
                break;
            case R.id.action_searchBtn:

                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            case R.id.sleep_timer:

                startActivity(new Intent(getActivity(), TimerActivity.class));
                break;
            case R.id.sync:
                Intent intent = new Intent(getActivity(), SplashActivity.class).putExtra(Constants.VALUE.SYNC,
                        true);
                startActivity(intent);
                break;
            case R.id.settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.id.equalizer:
                startActivity(new Intent(getActivity(), EqualizerActivity.class));
                break;
            case R.id.changeTheme:
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_choose_accent_color);
                dialog.findViewById(R.id.orange).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(
                                Constants.PREFERENCES.ACCENT_COLOR, Constants.COLOR.ORANGE);
                        dialog.cancel();

                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.findViewById(R.id.cyan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.CYAN);
                        dialog.cancel();
                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.findViewById(R.id.green).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.GREEN);
                        dialog.cancel();
                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.findViewById(R.id.yellow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.YELLOW);
                        dialog.cancel();
                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.findViewById(R.id.pink).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.PINK);
                        dialog.cancel();

                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.findViewById(R.id.purple).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.PURPLE);
                        dialog.cancel();
                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.findViewById(R.id.grey).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.GREY);
                        dialog.cancel();
                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.findViewById(R.id.red).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.ACCENT_COLOR,
                                Constants.COLOR.RED);
                        dialog.cancel();
                        startActivity(getActivity().getIntent());
                    }
                });
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
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
