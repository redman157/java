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

import com.android.music_player.R;
import com.android.music_player.activities.SongActivity;
import com.android.music_player.adapters.SongAdapter;
import com.android.music_player.interfaces.OnClickItem;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;

public class AllSongsFragment extends Fragment implements SongAdapter.OnClickListener, OnClickItem {
    private View view;
    private SongAdapter mSongAdapter;
    private ArrayList<SongModel> mSongs;
    private String type;
    private RecyclerView mRcSongs;
    private SongManager mSongManager;
    private SongActivity mSongActivity;
    private SharedPrefsUtils mSharedPrefsUtils;
    public AllSongsFragment(ArrayList<SongModel> songs, String type){
        mSongs = songs;
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(getContext());

        mSongActivity = (SongActivity) getContext();

        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
        mSongAdapter = new SongAdapter(getActivity(), mSongs, type);
        mSongAdapter.notifyDataSetChanged();
        mSongAdapter.setLimit(false);
        mSongAdapter.setOnClickItem(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_allsong, null);
            initView(view);
        }
        initData();
        return view;
    }

    private void initView(View view){
        mRcSongs = view.findViewById(R.id.rc_recently_add);
    }

    private void initData(){

        mRcSongs.setHasFixedSize(true);

        mRcSongs.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL, false));
        mRcSongs.setAdapter(mSongAdapter);
    }

    @Override
    public void onClick(int pos) {

    }

    @Override
    public void onClick(String type, int position) {
        // khi click vào 1 item nào đó, phải lưu type, vị trí chọn lại
        if (getActivity() instanceof SongActivity) {
            mSongActivity.chooseSong = position;

            mSongActivity.setSongCurrent(mSongs, position);
            mSongManager.setType(type);
        }
    }
}
