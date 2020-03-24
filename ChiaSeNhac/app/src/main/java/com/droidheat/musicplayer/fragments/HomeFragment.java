package com.droidheat.musicplayer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.ChangeMusic;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.IndexMusic;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.activities.RecentlyAllMusicActivity;
import com.droidheat.musicplayer.adapters.RecentlyAdderAdapter;
import com.droidheat.musicplayer.manager.ImageManager;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecentlyAdderAdapter.OnClickItem, View.OnClickListener{
    private RecyclerView mRc_Recently_Add;
    private RecentlyAdderAdapter mAdderAdapter;
    private ArrayList<SongModel> mNewSongs;
    private View view;

    private Button btn_ViewAll;
    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private ImageManager mImageManager;
    private IndexMusic indexMusic;
    private Activity mActivity;
    private SharedPrefsManager prefsManager;
    private Fragment MusicDock;

    public HomeFragment(Activity activity){
        mActivity = activity;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = new SharedPrefsManager();
        prefsManager.setContext(getContext());
        MusicDock = ((HomeActivity) mActivity).getSupportFragmentManager().findFragmentById(R.id.fm_music_dock);



        mImageManager = new ImageManager(getContext());
        ChangeMusic.getInstance().setContext(getContext());

        mAdderAdapter = new RecentlyAdderAdapter(getContext(),
                SongsManager.getInstance().newSongs(),
                Constants.VALUE.NEW_SONGS );
        mAdderAdapter.SetOnClickItem(this);
        mNewSongs = SongsManager.getInstance().newSongs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, null);
            initView();
            assignView();
            mRc_Recently_Add.setAdapter(mAdderAdapter);
            mRc_Recently_Add.setNestedScrollingEnabled(false);
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
        btn_ViewAll= view.findViewById(R.id.btn_ViewAll);
    }

    private void assignView(){
        mImg_Shuffle_All.setOnClickListener(this);
        btn_ViewAll.setOnClickListener(this);
    }

    @Override
    public void onClick(String type, int index) {
        // set switch vị trí và type music cho play activity chạy

        ChangeMusic.getInstance().setFragment((MusicDockFragment) MusicDock);
        ChangeMusic.getInstance().setPosition(type , index);
        ChangeMusic.getInstance().switchMusic();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ViewAll:

                startActivity(new Intent(getContext(), RecentlyAllMusicActivity.class));
                getActivity().finish();
                break;
            case R.id.img_Shuffle_All:
                break;
        }
    }
}
