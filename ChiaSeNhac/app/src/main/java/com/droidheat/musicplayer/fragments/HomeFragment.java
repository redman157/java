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

import com.droidheat.musicplayer.Constants;


import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.activities.RecentlyAllMusicActivity;
import com.droidheat.musicplayer.adapters.RecentlyAdderAdapter;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
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
    private ImageUtils mImageUtils;
    private String type;
    private int position;
    private Activity mActivity;
    private SharedPrefsManager mSharedPrefsManager;
    private ArrayList<SongModel> mSongs;

    public HomeFragment(Activity activity){
        mActivity = activity;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(getContext());


        mAdderAdapter = new RecentlyAdderAdapter(getContext(),
                SongManager.getInstance().newSongs(),
                Constants.VALUE.NEW_SONGS );
        mAdderAdapter.OnClickItem(this);
        mNewSongs = SongManager.getInstance().newSongs();
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
        ((HomeActivity)getActivity()).mTextTitle.setText(mNewSongs.get(position).getTitle());
        ((HomeActivity)getActivity()).mTextArtist.setText(mNewSongs.get(position).getArtist());
        ImageUtils.getInstance(getContext()).getBitmapImageByPicasso(
                mNewSongs.get(position).getAlbumID(),((HomeActivity)getActivity()).mImgMedia);

        mImg_Shuffle_All.setOnClickListener(this);
        btn_ViewAll.setOnClickListener(this);
    }

    @Override
    public void onClick(String type, int position) {
        // set switch vị trí và type music cho play activity chạy

        mSharedPrefsManager.setInteger(Constants.PREFERENCES.POSITION,position);
        mSharedPrefsManager.setString(Constants.PREFERENCES.TYPE, type);

        ((HomeActivity)getActivity()).mTextTitle.setText(mNewSongs.get(position).getTitle());
        ((HomeActivity)getActivity()).mTextArtist.setText(mNewSongs.get(position).getArtist());
        ImageUtils.getInstance(getContext()).getBitmapImageByPicasso(
                mNewSongs.get(position).getAlbumID(),((HomeActivity)getActivity()).mImgMedia);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ViewAll:
                Intent intent = new Intent(getContext(), RecentlyAllMusicActivity.class);
                intent.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.NEW_SONGS);

                startActivity(intent);
                break;
            case R.id.img_Shuffle_All:
                break;
        }
    }
}
