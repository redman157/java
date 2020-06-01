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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.adapters.HomeFragmentAdapter;
import com.android.music_player.adapters.SongAdapter;
import com.android.music_player.interfaces.OnChangePlayListListener;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnChangePlayListListener,
        SwipeRefreshLayout.OnRefreshListener, OnClickItemListener {
    private View view;
    private String type;
    private RecyclerView mRcHome;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MusicManager mMusicManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private OnChangePlayListListener onChangePlayListListener;
    private HomeFragmentAdapter mHomeAdapter;
    private SongAdapter mSongsAdapter;
    @Override
    public void onClickPosition(int pos) {

    }

    @Override
    public void onClickType(String type, int pos) {

    }

    @Override
    public void onClickMusic(String nameChoose) {
        mMusicManager.setCurrentMusic(nameChoose);
        Log.d("FFF", "HomeFragment --- nameChoose: "+nameChoose);
        ((HomeActivity) getActivity()).mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
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

        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(getContext());
        mSongsAdapter = new SongAdapter(getActivity(), MusicManager.getInstance().newSongs(),
                Constants.VALUE.NEW_SONGS);
        mSongsAdapter.setLimit(true);
        mSongsAdapter.notifyDataSetChanged();
        mSongsAdapter.setOnClickItemListener(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, null);


        initView();
        mHomeAdapter = new HomeFragmentAdapter(getActivity(),mSongsAdapter);
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
//        newInstance(mostPlayList);
    }

    @Override
    public void onRefresh() {
        loadRecyclerViewData();
    }


}
