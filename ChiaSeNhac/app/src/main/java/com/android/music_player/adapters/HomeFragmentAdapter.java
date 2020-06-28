package com.android.music_player.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.HomeHolder>  {
    private HomeActivity mHomeActivity;
    private MediaManager mMediaManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private BrowseAdapter mBrowseAdapter;

    public HomeFragmentAdapter(HomeActivity activity, BrowseAdapter browseAdapter){
        mHomeActivity = activity;
        mBrowseAdapter = browseAdapter;
        mSharedPrefsUtils = new SharedPrefsUtils(activity);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(activity);
    }
    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mHomeActivity).inflate(R.layout.item_home, null);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        holder.initView();
        mBrowseAdapter.notifyDataSetChanged();
        holder.assignView(mBrowseAdapter);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTextPlayer_1, mTextPlayer_2, mTextPlayerSongs;
        private RecyclerView mRc_Recently_Add;
        private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
                mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
        private ArrayList<String> mMostPlayList;
        private MediaManager mMediaManager;
        private String mMostMusic;

        public HomeHolder(@NonNull View view) {
            super(view);
            mMediaManager = MediaManager.getInstance();
            mMediaManager.setContext(mHomeActivity);
            mTextPlayerSongs = view.findViewById(R.id.text_Player_Songs);
            mRc_Recently_Add = view.findViewById(R.id.rc_recently_add);
            mImg_Player_Songs = view.findViewById(R.id.img_Player_Songs);
            mImg_Player_1 = view.findViewById(R.id.img_player_1);
            mImg_Player_2 = view.findViewById(R.id.img_player_2);
            mImg_Most_Player = view.findViewById(R.id.img_most_player);
            mImg_Recently_Add = view.findViewById(R.id.img_Recently_Add);
            mImg_Shuffle_All = view.findViewById(R.id.img_Shuffle_All);
            mTextPlayer_1 = view.findViewById(R.id.text_Player_1);
            mTextPlayer_2 = view.findViewById(R.id.text_Player_2);
        }

        public void initView(){

            if (mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC).equals("")) {
                mTextPlayerSongs.setText("");
                mImg_Player_Songs.setImageResource(R.drawable.ic_music_notes_padded);
            }else {
                mMostMusic = mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC);
                mTextPlayerSongs.setText(mMostMusic);
                ImageHelper.getInstance(mHomeActivity).getSmallImageByPicasso(mMediaManager.getSong(mMostMusic).getAlbumID(),
                        mImg_Player_Songs);
            }

            mMostPlayList = mMediaManager.getPlayListMost();
            if (mMostPlayList != null)  {
                mTextPlayer_1.setText(mMostPlayList.get(0));
                mTextPlayer_2.setText(mMostPlayList.size() < 2 ? mMostPlayList.get(0) :
                        mMostPlayList.get(1));
            }else {
                mTextPlayer_1.setText("Play List 1");
                mTextPlayer_2.setText("Play List 2");
            }
        }

        public void assignView(BrowseAdapter mSongsAdapter){
            mImg_Player_Songs.setOnClickListener(this);
            mImg_Player_1.setOnClickListener(this);
            mImg_Player_2.setOnClickListener(this);
            mImg_Shuffle_All.setOnClickListener(this);
            mRc_Recently_Add.setAdapter(mSongsAdapter);
            mRc_Recently_Add.setNestedScrollingEnabled(false);
            mRc_Recently_Add.setLayoutManager(new LinearLayoutManager(mHomeActivity,
                    LinearLayoutManager.VERTICAL, false));
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_Shuffle_All:
                    Random random = new Random();
                    List<String> keys = new ArrayList<>(MusicLibrary.music.keySet());
                    int index = random.nextInt(keys.size());
                    mHomeActivity.getControllerActivity().getTransportControls().prepareFromMediaId(
                            keys.get(index), null
                    );
                    Log.d("CCC",
                            "Check thử: "+keys.get(index));
                    break;
                case R.id.img_player_2:

                    break;

                case R.id.img_player_1:

                    break;
                case R.id.img_most_player:
                    if (!mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC).equals("")) {
                        (mHomeActivity).setViewMusic(mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC), SlidingUpPanelLayout.PanelState.EXPANDED);
                        (mHomeActivity).mBrowserHelper.getTransportControls().playFromMediaId(mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC), null);
                    }
                    break;
            }
        }
    }
}
