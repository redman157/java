package com.android.music_player.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.music_player.managers.SongManager;

public class RenamePlayListTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private SongManager mSongManager;
    private String main;
    public RenamePlayListTask(Context context, String main){
        mContext = context;
        this.main = main;
    }
    
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(mContext);
    }

    @Override
    protected String doInBackground(String... strings) {
        mSongManager.getAllPlaylistDB().updatePlayList(main, strings[0]);
        return strings[0];
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
