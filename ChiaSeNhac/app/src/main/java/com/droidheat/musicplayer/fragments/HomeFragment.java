package com.droidheat.musicplayer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.ViewOnClick;
import com.droidheat.musicplayer.adapters.RecentlyAdderAdapter;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private RecyclerView mRc_Recently_Add;
    private RecentlyAdderAdapter mAdderAdapter;
    private ArrayList<SongModel> mNewSongs;
    private View view;
    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private ImageUtils mImageUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUtils = new ImageUtils(getContext());
        mAdderAdapter = new RecentlyAdderAdapter(SongsManager.getInstance().getMainList(), getContext());
        mNewSongs = SongsManager.getInstance().newSongs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, null);
            initView();

            mAdderAdapter.setNewsongs(mNewSongs);
            mRc_Recently_Add.setAdapter(mAdderAdapter);
            mRc_Recently_Add.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));

        }
        return view;
    }
    private void initView(){
        mRc_Recently_Add = view.findViewById(R.id.rc_recently_add);
        mImg_Player_Songs = view.findViewById(R.id.img_Player_Songs);
        mImg_Player_1 = view.findViewById(R.id.img_Player_1);
        mImg_Player_2 = view.findViewById(R.id.img_Player_2);
        mImg_Most_Player = view.findViewById(R.id.img_Most_Player);
        mImg_Recently_Add = view.findViewById(R.id.img_Recently_Add);
        mImg_Shuffle_All = view.findViewById(R.id.img_Shuffle_All);

    }

    private void assignView(){
        mImg_Shuffle_All.setOnClickListener(new ViewOnClick(mImg_Shuffle_All.getId()));
    }

}
