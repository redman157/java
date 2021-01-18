package com.android.music_player.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.music_player.managers.MediaManager;

public class RenamePlayListTask extends AsyncTask<Void, Void, String> {
    private Context mContext;
    private MediaManager mMediaManager;
    private String main, change;
    public RenamePlayListTask(Context context, String main, String change){
        mContext = context;
        this.main = main;
        this.change = change;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(mContext);
    }

    @Override
    protected String doInBackground(Void... voids) {
        mMediaManager.getAllPlaylistDB().updatePlayList(main, change);
        return change;
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
