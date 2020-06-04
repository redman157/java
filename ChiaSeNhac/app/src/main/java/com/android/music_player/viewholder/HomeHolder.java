package com.android.music_player.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.activities.PlayActivity;
import com.android.music_player.activities.SongActivity;
import com.android.music_player.adapters.SongAdapter;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;

import java.util.ArrayList;

public class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private TextView mTextPlayer_1, mTextPlayer_2, mTextPlayerSongs;
    private RecyclerView mRc_Recently_Add;
    private SongAdapter mSongsAdapter;
    private ArrayList<SongModel> mNewSongs;
    private Button mBtnViewAll;
    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private  ArrayList<String> mMostPlayList;
    private MusicManager mMusicManager;
    private Activity mActivity;
    private HomeActivity mHomeActivity;
    private String mMostMusic;

    public HomeHolder(@NonNull View view, Activity mActivity) {
        super(view);
        this.mActivity = mActivity;
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(mActivity);
        mTextPlayerSongs = view.findViewById(R.id.text_Player_Songs);
        mRc_Recently_Add = view.findViewById(R.id.rc_recently_add);
        mImg_Player_Songs = view.findViewById(R.id.img_Player_Songs);
        mImg_Player_1 = view.findViewById(R.id.img_Player_1);
        mImg_Player_2 = view.findViewById(R.id.img_Player_2);
        mImg_Most_Player = view.findViewById(R.id.img_Most_Player);
        mImg_Recently_Add = view.findViewById(R.id.img_Recently_Add);
        mImg_Shuffle_All = view.findViewById(R.id.img_Shuffle_All);
        mBtnViewAll = view.findViewById(R.id.btn_ViewAll);
        mTextPlayer_1 = view.findViewById(R.id.text_Player_1);
        mTextPlayer_2 = view.findViewById(R.id.text_Player_2);
    }
    public void initView(){

        if (!mMusicManager.getStatistic().getMost(Constants.VALUE.SONG).equals("")) {
            mMostMusic = mMusicManager.getStatistic().getMost(Constants.VALUE.SONG);
            mTextPlayerSongs.setText(mMostMusic);
            ImageUtils.getInstance(mActivity).getSmallImageByPicasso(mMusicManager.getSong(mMostMusic).getAlbumID(),
                    mImg_Player_Songs);
        }else {
            mTextPlayerSongs.setText("");
            mImg_Player_Songs.setImageResource(R.drawable.ic_music_notes_padded);
        }
        if (mMostPlayList != null) {
            mTextPlayer_1.setText(mMostPlayList.get(0));
            mTextPlayer_2.setText(mMostPlayList.size() < 2 ? mMostPlayList.get(0) :
                    mMostPlayList.get(1));
        }else {
            mTextPlayer_1.setText("Play List 1");
            mTextPlayer_2.setText("Play List 2");
        }
    }
    public void assignView(SongAdapter mSongsAdapter){
        mImg_Player_Songs.setOnClickListener(this);
        mImg_Player_1.setOnClickListener(this);
        mImg_Player_2.setOnClickListener(this);
        mImg_Shuffle_All.setOnClickListener(this);
        mBtnViewAll.setOnClickListener(this);


        mRc_Recently_Add.setAdapter(mSongsAdapter);
        mRc_Recently_Add.setNestedScrollingEnabled(false);
        mRc_Recently_Add.setLayoutManager(new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ViewAll:


                Fragment allMusicFragment =
                        ((FragmentActivity)mActivity).getSupportFragmentManager().findFragmentByTag("AllMusicFragment");

                FragmentTransaction transaction =
                        ((FragmentActivity)mActivity).getSupportFragmentManager().beginTransaction();
                transaction.replace(((HomeActivity)mActivity).mLayoutPlaceHolder.getId(),
                        allMusicFragment);
                transaction.commit();
//                Log.d("QQQ", "HomeHolder: "+((HomeActivity)mActivity).mLayoutPlaceHolder.getId());

                break;
            case R.id.img_Shuffle_All:
                break;
            case R.id.img_Player_2:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent intent = new Intent(mActivity, SongActivity.class);
                intent.putExtra(Constants.INTENT.TYPE_MUSIC, mTextPlayer_2.getText().toString());

                mActivity.finish();
                mActivity.startActivity(intent);
                break;

            case R.id.img_Player_1:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iPlayList_2 = new Intent(mActivity, SongActivity.class);
                iPlayList_2.putExtra(Constants.INTENT.TYPE_MUSIC,
                        mTextPlayer_1.getText().toString());
                mActivity.finish();
                mActivity.startActivity(iPlayList_2);
                break;
            case R.id.img_Most_Player:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iMostPlay = new Intent(mActivity, PlayActivity.class);
                iMostPlay.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.NEW_SONGS);

                mActivity.startActivity(iMostPlay);
                break;
        }
    }
}