package com.droidheat.musicplayer;

import android.content.Context;

import com.droidheat.musicplayer.fragments.MusicDockFragment;
import com.droidheat.musicplayer.manager.ImageManager;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class ChangeMusic {
    private static ChangeMusic instance;
    private String type;
    private int position;
    private Context context;
    private SharedPrefsManager prefsManager;
    private MusicDockFragment mMusicDockFragment;
    public static ChangeMusic getInstance(){
        if (instance == null){
            instance = new ChangeMusic();
        }
        return instance;
    }
    public ChangeMusic(){
        // TODO something code
    }

    public void setPosition(String type, int index){
        this.position = index;
        this.type = type;
        prefsManager.setInteger(Constants.PREFERENCES.POSITION,position);
        prefsManager.setString(Constants.PREFERENCES.TYPE, type);
    }

    public void setContext(Context context) {
        this.context = context;
        prefsManager = new SharedPrefsManager();
        prefsManager.setContext(context);
    }

    public Context getContext() {
        return context;
    }

    public MusicDockFragment getFragment() {
        return mMusicDockFragment;
    }

    public void setFragment(MusicDockFragment musicDockFragment) {
        this.mMusicDockFragment = musicDockFragment;
    }


    public void switchMusic(){
        switch (type){
            case Constants.VALUE.ALL_SONGS:
                mMusicDockFragment.mTextTitle.setText(
                        SongsManager.getInstance().allSongs().get(position).getTitle());
                mMusicDockFragment.mTextArtists.setText(
                        SongsManager.getInstance().allSongs().get(position).getArtist());

                (new ImageManager(getContext())).getBitmapImageByPicasso(
                        SongsManager.getInstance().allSongs().get(position).getAlbumID(),
                        mMusicDockFragment.mImgArt);
                mMusicDockFragment.setType(type);
                mMusicDockFragment.setPosition(position);
                break;
            case Constants.VALUE.NEW_SONGS:
                mMusicDockFragment.mTextTitle.setText(
                        SongsManager.getInstance().newSongs().get(position).getTitle());
                mMusicDockFragment.mTextArtists.setText(
                        SongsManager.getInstance().newSongs().get(position).getArtist());

                (new ImageManager(getContext())).getBitmapImageByPicasso(
                        SongsManager.getInstance().newSongs().get(position).getAlbumID(),
                        mMusicDockFragment.mImgArt);
                mMusicDockFragment.setType(type);
                mMusicDockFragment.setPosition(position);
                break;

        }
    }

    public ArrayList<SongModel> switchMusic(String type){
        switch (type){
            case Constants.VALUE.ALL_SONGS:
                return SongsManager.getInstance().newSongs();

            case Constants.VALUE.NEW_SONGS:
                return SongsManager.getInstance().newSongs();
            default:
                return null;
        }
    }




}
