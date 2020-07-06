package com.android.music_player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.adapters.HomeFragmentAdapter;
import com.android.music_player.adapters.MusicAdapter;
import com.android.music_player.interfaces.OnConnectMediaId;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.utils.SharedPrefsUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

public class HomeFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private String type;
    private FastScrollRecyclerView mRcHome;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private HomeFragmentAdapter mHomeAdapter;
    private MusicAdapter mMusicAdapter;

    private static HomeFragment fragment = null;

    public static HomeFragment newInstance(OnConnectMediaId onConnectMediaId) {
        if (fragment == null){
            fragment = new HomeFragment();
        }
        fragment.setOnConnectMediaIdListener(onConnectMediaId);
        return fragment;
    }

    private OnConnectMediaId onConnectMediaId;
    public void setOnConnectMediaIdListener(OnConnectMediaId onConnectMediaId){
        this.onConnectMediaId = onConnectMediaId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
        mMusicAdapter = new MusicAdapter(getActivity(), MusicLibrary.music, false);
        mMusicAdapter.notifyDataSetChanged();
        mMusicAdapter.setOnConnectMediaIdListener(onConnectMediaId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        mSwipeRefreshLayout.setRefreshing(false);
        mHomeAdapter = new HomeFragmentAdapter((HomeActivity)getActivity(), mMusicAdapter);
        mHomeAdapter.notifyDataSetChanged();
        mRcHome.setLayoutManager(new LinearLayoutManager(getContext()));
        mRcHome.setAdapter(mHomeAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(){
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mRcHome = view.findViewById(R.id.rc_home_fragment);
    }

    private void loadRecyclerViewData(){
//        startActivity(getActivity().getIntent());
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching data from server
                loadRecyclerViewData();
            }
        });
    }
}
