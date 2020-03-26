package com.droidheat.musicplayer.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.droidheat.musicplayer.ChangeMusic;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.PlayActivity;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class MusicDockFragment extends Fragment implements View.OnClickListener{
    private View view;
    private Button mBtnTitle;
    public ImageView mImgArt;
    private ImageButton mImbPlay;
    public TextView mTextTitle, mTextArtists;
//    private PlayMusic mPlayMusic;
    private SongsManager mSongsManager;
    private MediaBrowserCompat mMediaBrowser;
    public String type = Constants.VALUE.NEW_SONGS;
    public int position = 0;
    private SharedPrefsManager prefsManager;
    private ArrayList<SongModel> mMusicSongs;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = new SharedPrefsManager();
        prefsManager.setContext(getActivity());
        position = prefsManager.getInteger(Constants.PREFERENCES.POSITION, 0);

        Log.d("OOO", "MusicDockFragment position: "+position);

        type = prefsManager.getString(Constants.PREFERENCES.TYPE, Constants.VALUE.NEW_SONGS);

//        mPlayMusic = PlayMusic.getInstance();
        mSongsManager = SongsManager.getInstance();
        mSongsManager.setContext(getActivity());
//        mPlayMusic.setActivity(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
//        mPlayMusic.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mPlayMusic.disconnect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_music_dock, null);
            initView();
            assignView();
        }

/*        mPlayMusic.setCallBack(this);
        mPlayMusic.initMediaBrowser();*/



        return view;
    }

    private void initView() {
        mTextTitle = view.findViewById(R.id.fm_text_title);
        mImbPlay = view.findViewById(R.id.fm_btn_Play);
        mBtnTitle = view.findViewById(R.id.fm_btn_title);
        mTextArtists = view.findViewById(R.id.fm_text_artists);
        mImgArt = view.findViewById(R.id.fm_img_albumArt);

        ImageUtils.getInstance(getContext()).getBitmapImageByPicasso(
                SongsManager.getInstance().newSongs().get(position).getAlbumID(), mImgArt);
    }

    private void assignView(){
        mBtnTitle.setOnClickListener(this);
        ChangeMusic.getInstance().setContext(getContext());
        ChangeMusic.getInstance().setFragment(this);
        ChangeMusic.getInstance().setPosition(type, position);
        ChangeMusic.getInstance().switchMusic();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fm_btn_title:
                if (!SongsManager.getInstance().queue().isEmpty()) {

                    Intent intent = new Intent(getActivity(), PlayActivity.class);
                    intent.putExtra(Constants.VALUE.TYPE, type);
                    intent.putExtra(Constants.VALUE.POSITION, position);
                    startActivity(intent);
                }
                break;
        }
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setMusicSong(String type){
        switch (type){
            case Constants.VALUE.NEW_SONGS:
                mMusicSongs = SongsManager.getInstance().newSongs();
                break;
            case Constants.VALUE.ALL_SONGS:
                mMusicSongs = SongsManager.getInstance().allSortSongs();
                break;
        }
    }
    public void getMusicSong(ArrayList<SongModel> musicSongs){
        mMusicSongs = musicSongs;

    }

}
