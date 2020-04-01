package com.droidheat.musicplayer;

import android.content.Context;
import android.view.View;

import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class ViewOnClick implements View.OnClickListener {
    private Context mContext;
    private SongManager mSongManager;
    private String type;
    private int resource;
    private ArrayList<SongModel> allSongs, mostSongs,favSongs, newSongs, artistsSong, albumsSongs;
    public ViewOnClick(int resource){
        this.resource = resource;
        this.allSongs = mSongManager.allSortSongs();
        this.mostSongs = mSongManager.mostPlayedSongs();
        this.favSongs = mSongManager.favouriteSongs();
        this.newSongs = mSongManager.newSongs();
    }
    public ViewOnClick(int resource,String type){
        this.type = type;
        this.resource = resource;
    }

    /*public static class Build{
        private Context context;
        private String type;
        private SongManager mSongManager;
        private ArrayList<SongModel> allSortSongs, mostSongs,favSongs, newSongs, artistsSong, albumsSongs;

        public ViewOnClick.Build setContext(Context context) {
            this.context = context;
            mSongManager = SongManager.getInstance();
            return this;
        }


        public ViewOnClick.Build setAllData() {
            this.allSortSongs = mSongManager.allSortSongs();
            this.mostSongs = mSongManager.mostPlayedSongs();
            this.favSongs = mSongManager.favouriteSongs();
            this.newSongs = mSongManager.newSongs();
            return this;
        }
        public ViewOnClick.Build setAlbum(String type){
            albumsSongs = mSongManager.albumSongs(type);
            return this;
        }

        public ViewOnClick.Build setArtist(String type){
            artistsSong = mSongManager.artistSongs(type);
            return this;
        }

        public ViewOnClick build(){
            return new ViewOnClick();
        }

        public ViewOnClick build(String type){
            return new ViewOnClick(type);
        }
    }*/


    @Override
    public void onClick(View view) {
       /* switch (){
            case R.id.img_Shuffle_All:
                if (allSortSongs.size() > 0) {

                    mSongManager.shufflePlay(allSortSongs);
                }else {

                }

                break;
        }*/
    }
}
