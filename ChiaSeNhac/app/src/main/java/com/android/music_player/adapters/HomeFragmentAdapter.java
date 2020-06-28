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
    private MusicAdapter mMusicAdapter;

    public HomeFragmentAdapter(HomeActivity activity, MusicAdapter musicAdapter){
        mHomeActivity = activity;
        mMusicAdapter = musicAdapter;
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
        mMusicAdapter.notifyDataSetChanged();
        holder.assignView(mMusicAdapter);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTextPlayer_1, mTextPlayer_2, mTextPlayerSongs;
        private RecyclerView mRelativeRecentlyAdd;
        private ImageView mImgPlayer_2, mImgPlayerMusic, mImgPlayer_1,
                mImgMostPlayer, mImgShuffleAll, mImgRecentlyAdd;
        private ArrayList<String> mMostPlayList;
        private MediaManager mMediaManager;
        private String mMostMusic;

        public HomeHolder(@NonNull View view) {
            super(view);
            mMediaManager = MediaManager.getInstance();
            mMediaManager.setContext(mHomeActivity);
            mTextPlayerSongs = view.findViewById(R.id.text_Player_Songs);
            mRelativeRecentlyAdd = view.findViewById(R.id.rc_recently_add);
            mImgPlayerMusic = view.findViewById(R.id.img_Player_Songs);
            mImgPlayer_1 = view.findViewById(R.id.img_player_1);
            mImgPlayer_2 = view.findViewById(R.id.img_player_2);
            mImgMostPlayer = view.findViewById(R.id.img_most_player);
            mImgRecentlyAdd = view.findViewById(R.id.img_Recently_Add);
            mImgShuffleAll = view.findViewById(R.id.img_Shuffle_All);
            mTextPlayer_1 = view.findViewById(R.id.text_Player_1);
            mTextPlayer_2 = view.findViewById(R.id.text_Player_2);
        }

        public void initView(){

            if (mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC).equals("")) {
                mTextPlayerSongs.setText("");
                mImgPlayerMusic.setImageResource(R.drawable.ic_music_notes_padded);
            }else {
                mMostMusic = mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC);
                mTextPlayerSongs.setText(mMostMusic);
                ImageHelper.getInstance(mHomeActivity).getSmallImageByPicasso(mMediaManager.getSong(mMostMusic).getAlbumID(),
                        mImgPlayerMusic);
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

        public void assignView(MusicAdapter mSongsAdapter){
            mImgPlayerMusic.setOnClickListener(this);
            mImgPlayer_1.setOnClickListener(this);
            mImgPlayer_2.setOnClickListener(this);
            mImgShuffleAll.setOnClickListener(this);
            mRelativeRecentlyAdd.setAdapter(mSongsAdapter);
            mRelativeRecentlyAdd.setNestedScrollingEnabled(false);
            mRelativeRecentlyAdd.setLayoutManager(new LinearLayoutManager(mHomeActivity,
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
