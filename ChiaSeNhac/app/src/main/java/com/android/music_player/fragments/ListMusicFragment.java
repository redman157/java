package com.android.music_player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.adapters.SongAdapter;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

public class ListMusicFragment extends Fragment implements SongAdapter.OnClickListener, OnClickItemListener {
    private View view;
    private SongAdapter mSongAdapter;
    private ArrayList<SongModel> mSongs;
    private String type;
    private FastScrollRecyclerView mRcSongs;
    private MusicManager mMusicManager;
    private HomeActivity mHomeActivity;
    private SharedPrefsUtils mSharedPrefsUtils;

    public ListMusicFragment(ArrayList<SongModel> songs){
        mSongs = songs;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(getContext());

        mHomeActivity = (HomeActivity) getContext();

        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
        mSongAdapter = new SongAdapter(getActivity(), mSongs, Constants.VALUE.ALL_NEW_SONGS);
        mSongAdapter.notifyDataSetChanged();
        mSongAdapter.setLimit(false);
        mSongAdapter.setOnClickItemListener(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_list_music, null);
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
    public void onClickPosition(int pos) {

    }

    @Override
    public void onClickMusic(String nameChoose) {

    }

    @Override
    public void onClick(String type, int position) {

    }

}
