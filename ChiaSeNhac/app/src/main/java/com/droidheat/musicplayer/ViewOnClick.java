package com.droidheat.musicplayer;

import android.content.Context;
import android.view.View;

import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class ViewOnClick implements View.OnClickListener {
    private Context mContext;
    private SongsManager mSongsManager;
    private String type;
    private int resource;
    private ArrayList<SongModel> allSongs, mostSongs,favSongs, newSongs, artistsSong, albumsSongs;
    public ViewOnClick(int resource){
        this.resource = resource;
        this.allSongs = mSongsManager.allSortSongs();
        this.mostSongs = mSongsManager.mostPlayedSongs();
        this.favSongs = mSongsManager.favouriteSongs();
        this.newSongs = mSongsManager.newSongs();
    }
    public ViewOnClick(int resource,String type){
        this.type = type;
        this.resource = resource;
    }

    /*public static class Build{
        private Context context;
        private String type;
        private SongsManager mSongsManager;
        private ArrayList<SongModel> allSortSongs, mostSongs,favSongs, newSongs, artistsSong, albumsSongs;

        public ViewOnClick.Build setContext(Context context) {
            this.context = context;
            mSongsManager = SongsManager.getInstance();
            return this;
        }


        public ViewOnClick.Build setAllData() {
            this.allSortSongs = mSongsManager.allSortSongs();
            this.mostSongs = mSongsManager.mostPlayedSongs();
            this.favSongs = mSongsManager.favouriteSongs();
            this.newSongs = mSongsManager.newSongs();
            return this;
        }
        public ViewOnClick.Build setAlbum(String type){
            albumsSongs = mSongsManager.albumSongs(type);
            return this;
        }

        public ViewOnClick.Build setArtist(String type){
            artistsSong = mSongsManager.artistSongs(type);
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
                    Log.d("BBB", "enter");
                    mSongsManager.shufflePlay(allSortSongs);
                }else {
                    Log.d("BBB", "no enter");
                }
                Log.d("BBB","k có");
                break;
        }*/
    }
}
