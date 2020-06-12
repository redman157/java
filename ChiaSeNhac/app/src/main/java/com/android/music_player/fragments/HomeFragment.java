package com.android.music_player.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.music_player.R;
import com.android.music_player.adapters.BrowseAdapter;
import com.android.music_player.adapters.HomeFragmentAdapter;
import com.android.music_player.interfaces.OnChangeListener;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.utils.SharedPrefsUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

public class HomeFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, OnClickItemListener {
    private View view;
    private String type;
    private FastScrollRecyclerView mRcHome;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private HomeFragmentAdapter mHomeAdapter;
    private BrowseAdapter mSongsAdapter;

    private OnChangeListener onChangeListener;

    public static HomeFragment newInstance(OnChangeListener onChangeListener) {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setOnChangeListener(onChangeListener);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener){
        this.onChangeListener = onChangeListener;
    }

    @Override
    public void onClickPosition(int pos) {

    }

    @Override
    public void onClickMusic(String musicID) {
        onChangeListener.onMusicID(musicID);
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("FFF", context.getClass().getSimpleName());
        /*if (context instanceof  OnItemSelectedListener){
            listener = (OnItemSelectedListener) context;
        }else {
            throw new ClassCastException(context.toString()
                    + " must implement HomeFragment.OnItemSelectedListener");
        }*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
        mSongsAdapter = new BrowseAdapter(getActivity(), MusicLibrary.music,
                false);
        mSongsAdapter.notifyDataSetChanged();
        mSongsAdapter.setOnClickItemListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, null);
        initView();
        mSwipeRefreshLayout.setRefreshing(false);
        mHomeAdapter = new HomeFragmentAdapter(getActivity(),mSongsAdapter);
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
