package com.android.music_player.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.activities.PlayActivity;
import com.android.music_player.activities.SongActivity;
import com.android.music_player.adapters.SongAdapter;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;

import java.util.ArrayList;

public class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private TextView text_Player_1, text_Player_2, text_Player_Songs;
    private RecyclerView mRc_Recently_Add;
    private SongAdapter mSongAdapter;


    private ArrayList<SongModel> mNewSongs;
    private Button mBtnViewAll;

    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private ArrayList<String> mMostPlayList;
    private Activity activity;
    private SongManager mSongManager;



    public HomeHolder(@NonNull View view, Activity activity) {
        super(view);
        this.activity = activity;
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(activity);
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



    public void initView(){
        String musicHot = null;
        if (!mSongManager.getStatistic().getMost(Constants.VALUE.SONG).equals("")) {
            musicHot = mSongManager.getStatistic().getMost(Constants.VALUE.SONG);

            text_Player_Songs.setText(musicHot);
            ImageUtils.getInstance(activity).getSmallImageByPicasso(mSongManager.getSong(musicHot).getAlbumID(),
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
    }
    public void assignView(){
        mImg_Player_Songs.setOnClickListener(this);
        mImg_Player_1.setOnClickListener(this);
        mImg_Player_2.setOnClickListener(this);
        mImg_Shuffle_All.setOnClickListener(this);
        mBtnViewAll.setOnClickListener(this);

        mRc_Recently_Add.setAdapter(mSongAdapter);
        mRc_Recently_Add.setNestedScrollingEnabled(false);
        mRc_Recently_Add.setLayoutManager(new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ViewAll:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iViewAll = new Intent(activity, SongActivity.class);
                iViewAll.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.ALL_SONGS);
                activity.finish();
                activity.startActivity(iViewAll);
                break;
            case R.id.img_Shuffle_All:
                break;
            case R.id.img_Player_2:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent intent = new Intent(activity, SongActivity.class);
                intent.putExtra(Constants.INTENT.TYPE_MUSIC, text_Player_2.getText().toString());

                activity.finish();
                activity.startActivity(intent);
                break;

            case R.id.img_Player_1:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iPlayList_2 = new Intent(activity, SongActivity.class);
                iPlayList_2.putExtra(Constants.INTENT.TYPE_MUSIC,
                        text_Player_1.getText().toString());
                activity.finish();
                activity.startActivity(iPlayList_2);
                break;
            case R.id.img_Most_Player:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iMostPlay = new Intent(activity, PlayActivity.class);
                iMostPlay.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.NEW_SONGS);

                activity.startActivity(iMostPlay);
                break;
        }
    }
}
