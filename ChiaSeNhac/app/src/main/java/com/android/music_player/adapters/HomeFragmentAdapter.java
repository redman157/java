package com.android.music_player.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.activities.PlayActivity;
import com.android.music_player.activities.SongActivity;
import com.android.music_player.interfaces.OnChangePlayListListener;
import com.android.music_player.interfaces.OnClickItem;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.services.MediaPlayerService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.ViewHolder> {
    private Activity activity;
    private SongManager mSongManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private TextView text_Player_1, text_Player_2, text_Player_Songs;
    private RecyclerView mRc_Recently_Add;
    private SongAdapter mSongAdapter;


    private ArrayList<SongModel> mNewSongs;
    private Button mBtnViewAll;

    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private ArrayList<String> mMostPlayList;

    public HomeFragmentAdapter(Activity activity){
        this.activity = activity;
        mSharedPrefsUtils = new SharedPrefsUtils(activity);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(activity);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_home, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.initView();
        holder.assignView();

    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            OnChangePlayListListener, SongAdapter.OnClickListener, OnClickItem {
        private TextView text_Player_1, text_Player_2, text_Player_Songs;
        private RecyclerView mRc_Recently_Add;
        private SongAdapter mSongsAdapter;
//        private OptionAdapter mOptionAdapter;

        private ArrayList<SongModel> mNewSongs;
        private Button mBtnViewAll;
        private RecyclerView rc_setting;
        private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
                mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
        private  ArrayList<String> mMostPlayList;

        public ViewHolder(@NonNull View view) {
            super(view);
//            initData();
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
//            rc_setting = view.findViewById(R.id.rc_settings);
        }

      /*  public void assignView(){
            mMostPlayList = mSongManager.getPlayListMost();
            mSongsAdapter = new SongAdapter(activity,
                    SongManager.getInstance().newSongs(),
                    Constants.VALUE.NEW_SONGS);

//            mOptionAdapter = new OptionAdapter(activity, mOptionItems);
//            mOptionAdapter.setOnClickItem(this);
            mSongsAdapter.setLimit(true);
            mSongsAdapter.notifyDataSetChanged();
            mSongsAdapter.setOnClickItem(this);
//            mSongsAdapter.setOnClickItem(this);
            mNewSongs = SongManager.getInstance().newSongs();
        }*/

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

            mSongsAdapter = new SongAdapter(activity, SongManager.getInstance().newSongs(),
                    Constants.VALUE.NEW_SONGS);
            mSongsAdapter.setLimit(true);
            mSongsAdapter.notifyDataSetChanged();
            mSongsAdapter.setOnClickItem(this);
            mNewSongs = SongManager.getInstance().newSongs();
            mRc_Recently_Add.setAdapter(mSongsAdapter);
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

        @Override
        public void onClickItem(ArrayList<String> mostPlayList) {

        }

        @Override
        public void onClick(int pos) {

        }

        @Override
        public void onClick(String type, int position) {
            if (position != SongManager.getInstance().getPositionCurrent()) {
                ((HomeActivity) Objects.requireNonNull(activity)).mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
                ((HomeActivity) Objects.requireNonNull(activity)).isContinue = false;
            }else {
                if (MediaPlayerService.mMediaPlayer!= null) {
                    if (MediaPlayerService.mMediaPlayer.isPlaying()) {
                        ((HomeActivity) Objects.requireNonNull(activity)).mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
                        ((HomeActivity) Objects.requireNonNull(activity)).isContinue = true;
                    } else {
                        ((HomeActivity) Objects.requireNonNull(activity)).mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
//
                    }
                }else {
                    ((HomeActivity) Objects.requireNonNull(activity)).mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
//
                }
            }
            mSongManager.setTypeCurrent(type);
//            ((HomeActivity)activity).mLlPlayMedia.setVisibility(View.VISIBLE);
            ((HomeActivity)activity).choosePosition = position;
            ((HomeActivity)activity).mTextTitle.setText(mNewSongs.get(position).getSongName());
            ((HomeActivity)activity).mTextArtist.setText(mNewSongs.get(position).getArtist());
            ImageUtils.getInstance(activity).getBitmapImageByPicasso(
                    mNewSongs.get(position).getAlbumID(),((HomeActivity)activity).mImgMedia);
        }


       /* @Override
        public void onClick(int position) {
            switch (position){
                case 0:
                    mRc_Recently_Add.setVisibility(View.GONE);
                    Intent iViewAll = new Intent(activity, SongActivity.class);
                    iViewAll.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.ALL_SONGS);
                    activity.finish();
                    activity.startActivity(iViewAll);
                    break;
            }
        }*/
    }

}
