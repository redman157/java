package com.android.music_player.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.R;
import com.android.music_player.adapters.SongAdapter;
import com.android.music_player.adapters.ViewPagerAdapter;
import com.android.music_player.interfaces.OnChangeListener;
import com.android.music_player.interfaces.OnClickItemListener;
import com.google.android.material.tabs.TabLayout;

public class MainFragment extends Fragment implements TabLayout.OnTabSelectedListener,
        OnClickItemListener {
    private View view;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;
    private OnChangeListener onChangeListener;
    private Context mContext;
    private FragmentActivity listener;
    private SongAdapter mSongsAdapter;

    public static MainFragment newInstance(OnChangeListener onChangeListener) {
        MainFragment fragmentDemo = new MainFragment(onChangeListener);
        Bundle args = new Bundle();
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public MainFragment(OnChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //is called when a fragment is connected to an activity.
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            Log.d("FFF","MainFragment --- onCreate: enter");
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // is called after onCreateView()
        // and ensures that the fragment's root view is non-null. Any view setup should happen here. E.g., view lookups, attaching listeners.
        // Setup any handles to view objects here
        mTabLayout = view.findViewById(R.id.tab_MainFragment);
        mViewPager = view.findViewById(R.id.vp_MainFragment);

        setupViewPager(mViewPager);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // is called when host activity has completed its onCreate() method.

    }
    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onDestroyView() {
     /*   if (view != null) {
            ViewGroup parentViewGroup =
                    (ViewGroup) view.getParent();
            parentViewGroup.removeAllViews();
        }*/
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayout.getTabCount(); i ++){
            View view = mTabLayout.getTabAt(i).getCustomView();
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

    private void setupViewPager(ViewPager viewPager){
        mViewPagerAdapter = new ViewPagerAdapter(getContext() ,
                getChildFragmentManager());

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setOnChangeListener(onChangeListener);
        LibraryFragment libraryFragment = new LibraryFragment();

        mViewPagerAdapter.addFragment(homeFragment);
        mViewPagerAdapter.addFragment(libraryFragment);

        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.setCurrentItem(0);

        mTabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setCustomView(mViewPagerAdapter.getTabHome(i));
        }
        mTabLayout.addOnTabSelectedListener(this);
    }


    @Override
    public void onClickPosition(int pos) {

    }

    @Override
    public void onClickType(String type, int pos) {

    }

    @Override
    public void onClickMusic(String nameChoose) {
        Log.d("CCC","MainFragment --- onClickMusic: "+nameChoose);
    }
}
