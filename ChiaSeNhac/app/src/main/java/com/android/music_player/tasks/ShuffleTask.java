package com.android.music_player.tasks;

import android.os.AsyncTask;

public class ShuffleTask extends AsyncTask<String, Void, Void> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... strings) {
        if (strings[0].equals("run")){

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}

