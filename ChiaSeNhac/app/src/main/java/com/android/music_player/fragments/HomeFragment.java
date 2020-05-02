package com.android.music_player.fragments;

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

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.activities.PlayActivity;
import com.android.music_player.activities.SongActivity;
import com.android.music_player.adapters.SongsAdapter;
import com.android.music_player.interfaces.OnChangePlayListListener;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnClickItemListener,
        View.OnClickListener, OnChangePlayListListener {
    private RecyclerView mRc_Recently_Add;
    private SongsAdapter mAdderAdapter;
    private ArrayList<SongModel> mNewSongs;
    private View view;
    private Button mBtnViewAll;
    private ImageView mImg_Player_2, mImg_Player_Songs, mImg_Player_1,
            mImg_Most_Player, mImg_Shuffle_All, mImg_Recently_Add;
    private ImageUtils mImageUtils;
    private String type;


    private SharedPrefsUtils mSharedPrefsUtils;
    private ArrayList<SongModel> mSongs;
    private TextView text_Player_1, text_Player_2, text_Player_Songs;
    private SongManager mSongManager;
    private  ArrayList<String> mMostPlayList;
    private String play_list_1, play_list_2;
    private OnChangePlayListListener onChangePlayListListener;

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

        mSongManager = SongManager.getInstance();
        mSongManager.setContext(getContext());

        mMostPlayList = mSongManager.getPlayListMost();
        mAdderAdapter = new SongsAdapter(getContext(),
                SongManager.getInstance().newSongs(),
                Constants.VALUE.NEW_SONGS);
        mAdderAdapter.setLimit(true);
        mAdderAdapter.notifyDataSetChanged();
        mAdderAdapter.OnClickItem(this);
        mNewSongs = SongManager.getInstance().newSongs();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
        if (!mSongManager.getStatistic().getMost(Constants.VALUE.SONG).equals("")) {
            musicHot = mSongManager.getStatistic().getMost(Constants.VALUE.SONG);

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
    public void onClick(int pos) {

    }


    @Override
    public void onClick(String type, final int position) {
        // set switch vị trí và type music cho play activity chạy
        Log.d("XXX", "((HomeActivity)getActivity()).choosePosition: "+position);

        if (position != SongManager.getInstance().getPositionCurrent()) {
            ((HomeActivity) Objects.requireNonNull(getActivity())).mBtnPlayPause.setImageResource(R.drawable.ic_media_play_light);
            ((HomeActivity) Objects.requireNonNull(getActivity())).isContinue = false;
        }else {
            ((HomeActivity) Objects.requireNonNull(getActivity())).mBtnPlayPause.setImageResource(R.drawable.ic_media_pause_light);
            ((HomeActivity) Objects.requireNonNull(getActivity())).isContinue = true;
        }
        mSongManager.setTypeCurrent(type);
        ((HomeActivity)getActivity()).mLlPlayMedia.setVisibility(View.VISIBLE);
        ((HomeActivity)getActivity()).choosePosition = position;
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
                Intent iViewAll = new Intent(getContext(), SongActivity.class);
                iViewAll.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.NEW_SONGS);
                getActivity().finish();
                startActivity(iViewAll);
                break;
            case R.id.img_Shuffle_All:
                break;
            case R.id.img_Player_2:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent intent = new Intent(getContext(), SongActivity.class);
                intent.putExtra(Constants.INTENT.TYPE_MUSIC, text_Player_2.getText().toString());

                getActivity().finish();
                startActivity(intent);
                break;

            case R.id.img_Player_1:
                mRc_Recently_Add.setVisibility(View.GONE);
                Intent iPlayList_2 = new Intent(getContext(), SongActivity.class);
                iPlayList_2.putExtra(Constants.INTENT.TYPE_MUSIC,
                        text_Player_1.getText().toString());
                getActivity().finish();
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
    public void onClickItem(ArrayList<String> mostPlayList) {
//        mMostPlayList = mostPlayList;
        newInstance(mostPlayList);
    }
}
