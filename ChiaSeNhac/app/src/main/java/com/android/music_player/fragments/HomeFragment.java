package com.android.music_player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.adapters.ChooseMusicAdapter;
import com.android.music_player.adapters.MusicAdapter;
import com.android.music_player.interfaces.DialogType;
import com.android.music_player.interfaces.OnConnectMediaId;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.utils.BottomSheetHelper;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View view;
    private String type;
    private FastScrollRecyclerView mRcHome;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private HomeFragmentAdapter mHomeAdapter;
    private MusicAdapter mMusicAdapter;
    private static HomeFragment fragment = null;

    public static HomeFragment newInstance(OnConnectMediaId onConnectMediaId) {
        if (fragment == null){
            fragment = new HomeFragment();
        }
        fragment.setOnConnectMediaIdListener(onConnectMediaId);
        return fragment;
    }

    private OnConnectMediaId onConnectMediaId;
    public void setOnConnectMediaIdListener(OnConnectMediaId onConnectMediaId){
        this.onConnectMediaId = onConnectMediaId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
        mMusicAdapter = new MusicAdapter(getActivity(), MusicLibrary.music, false);
        mMusicAdapter.notifyDataSetChanged();
        mMusicAdapter.setOnConnectMediaIdListener(onConnectMediaId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        mSwipeRefreshLayout.setRefreshing(false);
        mHomeAdapter = new HomeFragmentAdapter((HomeActivity)getActivity(), mMusicAdapter);
        mHomeAdapter.notifyDataSetChanged();
        mRcHome.setLayoutManager(new LinearLayoutManager(getContext()));
        mRcHome.setAdapter(mHomeAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryDark);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(){
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mRcHome = view.findViewById(R.id.rc_home_fragment);
    }

    public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeHolder>  {
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
        public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                 int viewType) {
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
    }

     @Override
    public void onRefresh() {
        mMusicAdapter.notifyDataSetChanged();
        mHomeAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);

    }
    public class HomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTextPlayer_1, mTextPlayer_2, mTextPlayerSongs;
        private RecyclerView mRelativeRecentlyAdd;
        private ImageView mImgPlayer_2, mImgPlayerMusic, mImgPlayer_1,
                mImgMostPlayer, mImgShuffleAll, mImgRecentlyAdd;
        private ArrayList<String> mMostPlayList;
        private MediaManager mMediaManager;
        private LinearLayout mLinearRecently, mLinearMost, mLinearShuffle,
                mLinearMediaMost;
        private ChooseMusicAdapter mChooseMusicAdapter;
        private HomeActivity mHomeActivity = (HomeActivity) getContext();
        public HomeHolder(@NonNull View view) {
            super(view);
            mMediaManager = MediaManager.getInstance();
            mMediaManager.setContext(mHomeActivity);

            mTextPlayerSongs = view.findViewById(R.id.text_player_music);
            mRelativeRecentlyAdd = view.findViewById(R.id.recycler_recently_add);
            mImgPlayerMusic = view.findViewById(R.id.image_player_music);
            mImgPlayer_1 = view.findViewById(R.id.image_player_1);
            mImgPlayer_2 = view.findViewById(R.id.image_player_2);
            mImgMostPlayer = view.findViewById(R.id.image_most_player);
            mImgRecentlyAdd = view.findViewById(R.id.img_Recently_Add);
            mImgShuffleAll = view.findViewById(R.id.image_shuffle_all);
            mTextPlayer_1 = view.findViewById(R.id.text_player_1);
            mTextPlayer_2 = view.findViewById(R.id.text_Player_2);
            mLinearMost = view.findViewById(R.id.linear_most_music);
            mLinearRecently = view.findViewById(R.id.linear_recently_music);
            mLinearShuffle = view.findViewById(R.id.linear_shuffle_music);
            mLinearMediaMost = view.findViewById(R.id.linear_media_most);
        }

        public void initView(){
            if (mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC).equals("")) {
                mTextPlayerSongs.setText("");
                mImgPlayerMusic.setImageResource(R.drawable.ic_music_notes_padded);
            }else {
                mTextPlayerSongs.setText(mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC));
                ImageHelper.getInstance(mHomeActivity).getSmallImageByPicasso(mMediaManager.getSong(mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC)).getAlbumID(),
                        mImgPlayerMusic);
            }
            mMostPlayList = mMediaManager.getListMost(Constants.VALUE.MOST_PLAY_LIST);

            mTextPlayer_1.setText(mMostPlayList.get(0));
            mTextPlayer_2.setText(mMostPlayList.size() < 2 ? mMostPlayList.get(0) :
                    mMostPlayList.get(1));
        }

        public void assignView(MusicAdapter musicAdapter){
            mImgPlayerMusic.setOnClickListener(this);
            mLinearMost.setOnClickListener(this);
            mLinearRecently.setOnClickListener(this);
            mImgPlayer_1.setOnClickListener(this);
            mImgPlayer_2.setOnClickListener(this);
            mLinearShuffle.setOnClickListener(this);
            mRelativeRecentlyAdd.setAdapter(musicAdapter);
            mRelativeRecentlyAdd.setNestedScrollingEnabled(false);
            mRelativeRecentlyAdd.setLayoutManager(new LinearLayoutManager(mHomeActivity,
                    LinearLayoutManager.VERTICAL, false));
            if (mChooseMusicAdapter == null) {
                mChooseMusicAdapter = new ChooseMusicAdapter(mHomeActivity);
            }
//            mLinearMediaMost.setOnClickListener(this);
        }

        public void setupAdapter(String title){
            mChooseMusicAdapter.setOnConnectMediaIdListener(new OnConnectMediaId() {
                @Override
                public void onChangeMediaId(String mediaID) {
                    if (mHomeActivity.bottomSheetHelper.getShowsDialog()){
                        mHomeActivity.bottomSheetHelper.dismiss();
                    }
                    mHomeActivity.getControllerActivity().getTransportControls().prepareFromMediaId(
                            mediaID, null);
                    mHomeActivity.setViewMusic(mediaID, SlidingUpPanelLayout.PanelState.EXPANDED);
                }

                @Override
                public void onChangeFlowType(String type, String title) {
                }
            });

            try {
                mChooseMusicAdapter.setQueueMediaID(
                        MusicLibrary.getUpdateQueueUI(
                                mMediaManager.getAllMusicOfPlayList(title)));

                mChooseMusicAdapter.notifyDataSetChanged();
                mHomeActivity.bottomSheetHelper = new BottomSheetHelper(DialogType.CHOOSE_MUSIC,
                        mChooseMusicAdapter);
                mHomeActivity.bottomSheetHelper.setTitle(title);
                mHomeActivity.bottomSheetHelper.show(mHomeActivity.getSupportFragmentManager(),
                        HomeActivity.FRAGMENT_TAG);
            }catch (NullPointerException e){
                Utils.ToastShort(getContext(), "Play List chưa có bài hát");
            }
        }

        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.INTENT.AUTO_PLAY, true);
            switch (v.getId()){
                case R.id.linear_shuffle_music:
                    Random random = new Random();
                    List<String> keys = new ArrayList<>(MusicLibrary.music.keySet());
                    int index = random.nextInt(keys.size());
                    mHomeActivity.getControllerActivity().getTransportControls().prepareFromMediaId(
                            keys.get(index), null);
                    break;
                case R.id.image_player_2:
                    ((HomeActivity)getActivity()).getQueueManager().setupPlayList(mTextPlayer_2.getText().toString());
                    setupAdapter(mTextPlayer_2.getText().toString());
                    break;

                case R.id.image_player_1:
                    ((HomeActivity)getActivity()).getQueueManager().setupPlayList(mTextPlayer_1.getText().toString());
                    setupAdapter(mTextPlayer_1.getText().toString());

                    break;
                case R.id.linear_recently_music:
                    (mHomeActivity).getControllerActivity().getTransportControls().prepareFromMediaId(mMusicAdapter.getMusicList().get(0), bundle);
                    break;
                case R.id.linear_most_music:
                    try {
                        (mHomeActivity).getControllerActivity().getTransportControls().prepareFromMediaId(mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC), bundle);
                    }catch (IllegalArgumentException e){
                        Utils.ToastShort(getContext(), "Chưa có bài hát thịnh hành");
                    }

                    break;
                case R.id.image_player_music:
                    ((HomeActivity)getActivity()).getQueueManager().setupAllMusic();
                    if (mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC).length() > 0) {
                        mHomeActivity.setViewMusic(mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC), SlidingUpPanelLayout.PanelState.EXPANDED);
                        (mHomeActivity).getControllerActivity().getTransportControls().prepareFromMediaId(mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC), bundle);
                    }else {
                        Utils.ToastShort(mHomeActivity, "Chưa Có bài hát nghe nhiều");
                    }
                    break;
            }
        }
    }

}
