package com.android.music_player.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.android.music_player.OnChangePlayList;
import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.activities.PlayActivity;
import com.android.music_player.activities.RecentlyAllMusicActivity;
import com.android.music_player.adapters.RecentlyAdderAdapter;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.services.MediaPlayerService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecentlyAdderAdapter.OnClickItem,
        View.OnClickListener, OnChangePlayList {
    private RecyclerView mRc_Recently_Add;
    private RecentlyAdderAdapter mAdderAdapter;
    private ArrayList<SongModel> mNewSongs;
    private View view;
    private Button mBtnViewAll;
    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private ImageUtils mImageUtils;
    private String type;
    private int position;
    private Activity mActivity;
    private SharedPrefsUtils mSharedPrefsUtils;
    private ArrayList<SongModel> mSongs;
    private TextView text_Player_1, text_Player_2, text_Player_Songs;
    private SongManager mSongManager;
    private  ArrayList<String> mMostPlayList;
    private String play_list_1, play_list_2;
    private OnChangePlayList onChangePlayList;
    public void setOnChangePlayList(OnChangePlayList onChangePlayList){
        this.onChangePlayList = onChangePlayList;
    }

    public static HomeFragment newInstance(ArrayList<String> strings) {
        Log.d("XXX", "HomeFragment newInstance: "+strings.size());
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

        mSongManager = SongManager.getInstance();
        mSongManager.setContext(getContext());

        mMostPlayList = mSongManager.getPlayListMost();
        mAdderAdapter = new RecentlyAdderAdapter(getContext(),
                SongManager.getInstance().newSongs(),
                Constants.VALUE.NEW_SONGS);
        mAdderAdapter.OnClickItem(this);
        mNewSongs = SongManager.getInstance().newSongs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("XXX", "HomeFragment -- onCreateView");
        view = inflater.inflate(R.layout.fragment_home, null);

        mSongManager = SongManager.getInstance();
        mSongManager.setContext(getContext());
        initView();

        assignView();

        mRc_Recently_Add.setAdapter(mAdderAdapter);
        mRc_Recently_Add.setNestedScrollingEnabled(false);
        mRc_Recently_Add.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(){
        text_Player_Songs = view.findViewById(R.id.text_Player_Songs);
        mRc_Recently_Add = view.findViewById(R.id.rc_recently_add);
        mImg_Player_Songs = view.findViewById(R.id.img_Player_Songs);
        mImg_Player_1 = view.findViewById(R.id.img_Player_1);
        mImg_Player_2 = view.findViewById(R.id.img_Player_2);
        mImg_Most_Player = view.findViewById(R.id.img_Most_Player);
        mImg_Recently_Add = view.findViewById(R.id.img_Recently_Add);
        mImg_Shuffle_All = view.findViewById(R.id.img_Shuffle_All);
        mBtnViewAll = view.findViewById(R.id.btn_ViewAll);
        text_Player_1 = view.findViewById(R.id.text_Player_1);
        text_Player_2 = view.findViewById(R.id.text_Player_2);
    }

    private void assignView(){
        String musicHot = null;
        if (!mSongManager.getStatistic().getMost().equals("")) {
            musicHot = mSongManager.getStatistic().getMost();
            text_Player_Songs.setText(musicHot);
            ImageUtils.getInstance(getContext()).getSmallImageByPicasso(mSongManager.getSong(musicHot).getAlbumID(),
                    mImg_Player_Songs);
        }else {
            text_Player_Songs.setText("");
            mImg_Player_Songs.setImageResource(R.drawable.ic_music_notes_padded);
        }
        if (mMostPlayList != null) {
            text_Player_1.setText(mMostPlayList.get(0));
            text_Player_2.setText(mMostPlayList.size() < 2 ? mMostPlayList.get(0) :
                    mMostPlayList.get(1));
        }else {
            text_Player_1.setText("Play List 1");
            text_Player_2.setText("Play List 2");
        }
        mImg_Player_Songs.setOnClickListener(this);
        mImg_Player_1.setOnClickListener(this);
        mImg_Player_2.setOnClickListener(this);
        mImg_Shuffle_All.setOnClickListener(this);
        mBtnViewAll.setOnClickListener(this);
    }

    @Override
    public void onClick(String type, int position) {
        // set switch vị trí và type music cho play activity chạy

        if (MediaPlayerService.mMediaPlayer!= null){
            if (MediaPlayerService.mMediaPlayer.isPlaying()){
                Intent intent = new Intent(getActivity(), MediaPlayerService.class);
                intent.setAction(Constants.ACTION.PAUSE);
                intent.putExtra(Constants.INTENT.IS_PLAY_ACTIVITY,false);
                getActivity().startService(intent);

                ((HomeActivity)getActivity()).mBtnPlay.setImageResource(R.drawable.ic_media_pause_light);
            }
        }
        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION,position);
        mSharedPrefsUtils.setString(Constants.PREFERENCES.TYPE, type);

        ((HomeActivity)getActivity()).mTextTitle.setText(mNewSongs.get(position).getSongName());
        ((HomeActivity)getActivity()).mTextArtist.setText(mNewSongs.get(position).getArtist());
        ImageUtils.getInstance(getContext()).getBitmapImageByPicasso(
                mNewSongs.get(position).getAlbumID(),((HomeActivity)getActivity()).mImgMedia);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ViewAll:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iViewAll = new Intent(getContext(), RecentlyAllMusicActivity.class);
                iViewAll.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.NEW_SONGS);
                startActivity(iViewAll);
                break;
            case R.id.img_Shuffle_All:
                break;
            case R.id.img_Player_2:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent intent = new Intent(getContext(), RecentlyAllMusicActivity.class);
                intent.putExtra(Constants.INTENT.TYPE_MUSIC, text_Player_2.getText().toString());
                startActivity(intent);
                break;

            case R.id.img_Player_1:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iPlayList_2 = new Intent(getContext(), RecentlyAllMusicActivity.class);
                iPlayList_2.putExtra(Constants.INTENT.TYPE_MUSIC,
                        text_Player_1.getText().toString());

                startActivity(iPlayList_2);
                break;
            case R.id.img_Most_Player:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iMostPlay = new Intent(getContext(), PlayActivity.class);
                iMostPlay.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.NEW_SONGS);

                startActivity(iMostPlay);
                break;
        }
    }

    @Override
    public void onChange(ArrayList<String> mostPlayList) {
//        mMostPlayList = mostPlayList;
        newInstance(mostPlayList);
    }
}
