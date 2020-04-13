package com.droidheat.musicplayer;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.droidheat.musicplayer.activities.PlayActivity;
import com.droidheat.musicplayer.database.PlaylistSongs;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;
import java.util.Objects;

public class MusicTask extends AsyncTask<Void,Void, Void> {
    private Context context;
    private ArrayList<SongModel> songModels;
    private SongManager mSongManager;
    private PlaylistSongs db = PlaylistSongs.getInstance();
    private int pos;
    public MusicTask(Context context,ArrayList<SongModel> songModels, int pos){
        this.context = context;
        this.songModels = songModels;
        this.pos = pos;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int playlistID = Integer.parseInt(Objects.requireNonNull(mSongManager.getAllPlayLists().get(
                mSongManager.getAllPlayLists().size() - 1).get("ID")));
        for (int i = 0; i < songModels.size(); i++) {
            db.addRow(playlistID, songModels.get(i));
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(context);
        db.newRenderDB(context);
        db.open();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        db.close();
        Toast.makeText(context, "Đã Add Vào PlayList", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
