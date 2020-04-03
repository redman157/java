package com.droidheat.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.droidheat.musicplayer.fragments.MusicDockFragment;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
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
//        Log.d("KKK", "ChangeMusic --- setPosition:"+position + " === type: "+type);
        prefsManager.setInteger(Constants.PREFERENCES.POSITION,this.position);
        prefsManager.setString(Constants.PREFERENCES.TYPE, this.type);
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
                        SongManager.getInstance().allSortSongs().get(position).getTitle());
                mMusicDockFragment.mTextArtists.setText(
                        SongManager.getInstance().allSortSongs().get(position).getArtist());

                ImageUtils.getInstance(getContext()).getBitmapImageByPicasso(
                        SongManager.getInstance().allSortSongs().get(position).getAlbumID(),
                        mMusicDockFragment.mImgArt);
                mMusicDockFragment.setType(type);
                mMusicDockFragment.setPosition(position);
                break;
            case Constants.VALUE.NEW_SONGS:
                mMusicDockFragment.mTextTitle.setText(
                        SongManager.getInstance().newSongs().get(position).getTitle());
                mMusicDockFragment.mTextArtists.setText(
                        SongManager.getInstance().newSongs().get(position).getArtist());

                ImageUtils.getInstance(getContext()).getBitmapImageByPicasso(
                        SongManager.getInstance().newSongs().get(position).getAlbumID(),
                        mMusicDockFragment.mImgArt);
                mMusicDockFragment.setType(type);
                mMusicDockFragment.setPosition(position);
                break;

        }
    }

    public ArrayList<SongModel> switchMusic(String type){
        switch (type){
            case Constants.VALUE.ALL_SONGS:
                return SongManager.getInstance().allSortSongs();

            case Constants.VALUE.NEW_SONGS:
                return SongManager.getInstance().newSongs();
            default:
                return null;
        }
    }

}
