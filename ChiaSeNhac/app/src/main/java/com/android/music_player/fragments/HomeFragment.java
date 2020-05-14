package com.android.music_player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.music_player.R;
import com.android.music_player.adapters.HomeFragmentAdapter;
import com.android.music_player.interfaces.OnChangePlayListListener;
import com.android.music_player.managers.SongManager;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnChangePlayListListener, SwipeRefreshLayout.OnRefreshListener {

    private View view;

    private String type;
    private RecyclerView mRcHome;
    private SharedPrefsUtils mSharedPrefsUtils;

    private SongManager mSongManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OnChangePlayListListener onChangePlayListListener;
    private HomeFragmentAdapter mHomeAdapter;
    public void setOnChangePlayListListener(OnChangePlayListListener onChangePlayListListener){
        this.onChangePlayListListener = onChangePlayListListener;
    }

    public static HomeFragment newInstance(ArrayList<String> strings) {

        Bundle args = new Bundle();
        args.putStringArrayList("most", strings);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, null);

        mSongManager = SongManager.getInstance();
        mSongManager.setContext(getContext());
        initView();
        mHomeAdapter = new HomeFragmentAdapter(getActivity());
        mHomeAdapter.notifyDataSetChanged();
        mRcHome.setLayoutManager(new LinearLayoutManager(getContext()));
        mRcHome.setAdapter(mHomeAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                loadRecyclerViewData();
            }
        });
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
        mSwipeRefreshLayout.setRefreshing(true);
        getActivity().getIntent();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClickItem(ArrayList<String> mostPlayList) {
        newInstance(mostPlayList);
    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }
}
