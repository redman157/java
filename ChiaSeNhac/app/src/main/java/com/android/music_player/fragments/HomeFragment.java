package com.android.music_player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.adapters.HomeFragmentAdapter;
import com.android.music_player.adapters.SongsAdapter;
import com.android.music_player.interfaces.OnChangePlayListListener;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements OnChangePlayListListener {
    private RecyclerView mRc_Recently_Add;
    private SongsAdapter mAdderAdapter;
    private ArrayList<SongModel> mNewSongs;
    private View view;
    private Button mBtnViewAll;
    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private ImageUtils mImageUtils;
    private String type;
    private RecyclerView rc_main;
    private SharedPrefsUtils mSharedPrefsUtils;
    private ArrayList<SongModel> mSongs;
    private TextView text_Player_1, text_Player_2, text_Player_Songs;
    private SongManager mSongManager;
    private  ArrayList<String> mMostPlayList;
    private String play_list_1, play_list_2;
    private OnChangePlayListListener onChangePlayListListener;
    private HomeFragmentAdapter adapter;
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
        adapter = new HomeFragmentAdapter(getActivity());
        adapter.notifyDataSetChanged();
        rc_main.setLayoutManager(new LinearLayoutManager(getContext()));
        rc_main.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(){
        rc_main = view.findViewById(R.id.rc_home_fragment);
    }



    @Override
    public void onClickItem(ArrayList<String> mostPlayList) {
//        mMostPlayList = mostPlayList;
        newInstance(mostPlayList);
    }
}
