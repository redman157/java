package com.android.music_player.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.SharedPrefsUtils;

public class AddMusicTask extends AsyncTask<Void, Void, Void>{
    private Context mContext;
    private SharedPrefsUtils mSharedPrefsUtils;
    private SongManager mSongManager;
    private String namePlayList;

    private SongModel mSong;
    public AddMusicTask(Context context, String namePlayList, SongModel song){
        mContext = context;
        mSong = song;
        this.namePlayList = namePlayList;

    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(mContext);
        mSharedPrefsUtils = new SharedPrefsUtils(mContext);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (!mSongManager.getAllPlaylistDB().searchPlayList(namePlayList)) {
            mSongManager.getAllPlaylistDB().addPlayList(namePlayList);
        }
        // add bài hát
        mSongManager.getSongOfPlayListDB().addSong(mSong);
        // add id bài hát và name playlist tương ứng
        mSongManager.getRelationSongs().addRow(namePlayList,
                mSongManager.getSongOfPlayListDB().getId(mSong));

        mSongManager.getStatistic().addFileName(namePlayList);


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
