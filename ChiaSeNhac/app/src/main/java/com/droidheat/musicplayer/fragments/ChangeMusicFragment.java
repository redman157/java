package com.droidheat.musicplayer.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.OnMusicChange;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.PlayActivity;
import com.droidheat.musicplayer.adapters.MusicAdapter;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.models.SongModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ChangeMusicFragment extends Fragment implements View.OnClickListener {
    private SongModel mSongModel;
    private ImageView mImgAlbumArt, mImgShowList, mImgAddPlayList;
    private TextView mTextTittle, mTextArtists, text_leftTime, text_rightTime;
    private SeekBar sb_leftTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private View view;
    public TextView mTextPlaying;
    private Dialog mDlOptionMusic;
    private MusicAdapter mMusicAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<SongModel> musicMain;
    private SharedPrefsManager sharedPrefsManager;
    private static ArrayList<SongModel> mSongModels;
    public static void newInstance(ArrayList<SongModel> songModels) {
        mSongModels = songModels;
    }

    public ArrayList<SongModel> getMusicMain() {
        return musicMain;
    }

    public void setMusicMain(ArrayList<SongModel> musicMain) {
        this.musicMain = musicMain;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefsManager = new SharedPrefsManager();
        sharedPrefsManager.setContext(getContext());

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.item_change_music, null);
        initView();
        assignView();

        mTextPlaying.setText(mSongModel.getAlbum());
        mTextArtists.setText(mSongModel.getArtist());
        mTextTittle.setText(mSongModel.getTitle());
        ImageUtils.getInstance(getContext()).getSmallImageByPicasso(mSongModel.getAlbumID(), mImgAlbumArt);

        return view;
    }

    private void initView(){
        mDlOptionMusic = new Dialog(getContext());
        mTextPlaying = view.findViewById(R.id.item_text_playing);
        mTextTittle = view.findViewById(R.id.item_text_title);
        mTextArtists = view.findViewById(R.id.item_text_album);
        mImgAlbumArt = view.findViewById(R.id.item_img_ChangeMusic);
        mImgShowList = view.findViewById(R.id.item_img_viewQueue);
        mImgAddPlayList = view.findViewById(R.id.item_img_addToPlayListImageView);
    }

    private void assignView(){
        mImgShowList.setOnClickListener(this);
        mImgAddPlayList.setOnClickListener(this);
    }

    public void setSongModel(SongModel songModel) {
        mSongModel = songModel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_img_viewQueue:
                if (mSongModels == null){
                    mSongModels = musicMain;
                }
                mMusicAdapter = new MusicAdapter(getContext());
                int pos = sharedPrefsManager.getInteger(Constants.PREFERENCES.POSITION, -1);
                for (int i = 0; i < mSongModels.size(); i++) {
                    if (mSongModels.get(i).getTitle().equals(musicMain.get(pos).getTitle())) {
                        mMusicAdapter.setPosition(i);

                        showOptionMusic(mSongModels, i);
                        mDlOptionMusic.show();
                    }
                }


                break;
            case R.id.item_img_addToPlayListImageView:
                break;
        }
    }
    private void showOptionMusic(ArrayList<SongModel> songModels, int pos){
        mDlOptionMusic.setContentView(R.layout.dialog_option_music);
        RecyclerView mRcOptionMusic = mDlOptionMusic.findViewById(R.id.rc_OptionMusic);

        mMusicAdapter.setListMusic(songModels);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRcOptionMusic.setAdapter(mMusicAdapter);
        mRcOptionMusic.setLayoutManager(layoutManager);
        mRcOptionMusic.getLayoutManager().scrollToPosition(pos);
    }


}
