package com.droidheat.musicplayer;

import android.content.Context;
import android.view.View;

import com.droidheat.musicplayer.manager.SongsUtils;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class ViewOnClick implements View.OnClickListener {
    private Context mContext;
    private SongsUtils mSongsUtils;
    private String type;
    private int resource;
    private ArrayList<SongModel> allSongs, mostSongs,favSongs, newSongs, artistsSong, albumsSongs;
    public ViewOnClick(int resource){
        this.resource = resource;
        this.allSongs = mSongsUtils.allSongs();
        this.mostSongs = mSongsUtils.mostPlayedSongs();
        this.favSongs = mSongsUtils.favouriteSongs();
        this.newSongs = mSongsUtils.newSongs();
    }
    public ViewOnClick(int resource,String type){
        this.type = type;
        this.resource = resource;
    }

    /*public static class Build{
        private Context context;
        private String type;
        private SongsUtils mSongsUtils;
        private ArrayList<SongModel> allSongs, mostSongs,favSongs, newSongs, artistsSong, albumsSongs;

        public ViewOnClick.Build setContext(Context context) {
            this.context = context;
            mSongsUtils = SongsUtils.getInstance();
            return this;
        }


        public ViewOnClick.Build setAllData() {
            this.allSongs = mSongsUtils.allSongs();
            this.mostSongs = mSongsUtils.mostPlayedSongs();
            this.favSongs = mSongsUtils.favouriteSongs();
            this.newSongs = mSongsUtils.newSongs();
            return this;
        }
        public ViewOnClick.Build setAlbum(String type){
            albumsSongs = mSongsUtils.albumSongs(type);
            return this;
        }

        public ViewOnClick.Build setArtist(String type){
            artistsSong = mSongsUtils.artistSongs(type);
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
                if (allSongs.size() > 0) {
                    Log.d("BBB", "enter");
                    mSongsUtils.shufflePlay(allSongs);
                }else {
                    Log.d("BBB", "no enter");
                }
                Log.d("BBB","k có");
                break;
        }*/
    }
}
