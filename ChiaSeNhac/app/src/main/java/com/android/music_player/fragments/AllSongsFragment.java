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
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;

public class AllSongsFragment extends Fragment implements SongAdapter.OnClickListener, OnClickItem {
    private View view;
    private SongAdapter mSongAdapter;
    private ArrayList<SongModel> mSongs;
    private String type;
    private RecyclerView mRcSongs;
    private SongManager mSongManager;
    public int choosePosition;
    private SongActivity songActivity;
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

        songActivity = (SongActivity) getContext();

        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
        mSongAdapter = new SongAdapter(getContext(), mSongs, type);
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
        choosePosition = position;
        mSongManager.setPositionCurrent(position);
        mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, mSongs.get(position).getAlbumID());
        mSongManager.setTypeCurrent(type);
        if (songActivity.mLl_Play_Media.getVisibility() == View.GONE){
            songActivity.mLl_Play_Media.setVisibility(View.VISIBLE);
        }
        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(
                mSongs.get(position).getAlbumID(),
                songActivity.mImgAlbumId);
        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(
                mSongs.get(position).getAlbumID(),
                songActivity.mImgMedia);
        songActivity.mTextTitle.setText(mSongs.get(position).getSongName());
        songActivity.mTextArtist.setText(mSongs.get(position).getArtist());


        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(
                mSongs.get(position).getAlbumID(),
                songActivity.profileImage);
        songActivity.profileName.setText(mSongs.get(position).getSongName());
        songActivity.profileArtist.setText(mSongs.get(position).getArtist());
        songActivity.profileAlbum.setText(mSongs.get(position).getAlbum());
    }
}
