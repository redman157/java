package com.droidheat.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.activities.SplashActivity;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;
import java.util.Map;

public class PerformMusicTasks  extends AsyncTask<String, Integer, Long> {
    private boolean sync;
    private String tag = "PerformMusicTasksLog";
    private SongManager mSongManager;
    private SharedPrefsManager mSharedPrefsManager;

    private Activity mActivity;

    public PerformMusicTasks(Activity activity, Boolean sync) {
        mActivity = activity;

        this.sync = sync;
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(mActivity);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(mActivity);


    }

    private void addPlayListFirst(){
        if (mSongManager.getAllPlaylistDB().getSize() == 0){
            mSongManager.getAllPlaylistDB().addPlayList("PlayList 1");
        }
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSongManager.installData();

    }

    @Override
    protected Long doInBackground(String... strings) {

        Map<String, ArrayList<SongModel>> artists = mSongManager.getArtist();
        Map<String, ArrayList<SongModel>> albums = mSongManager.getAlbum();
        Map<String, ArrayList<SongModel>> folders = mSongManager.getFolder();
        if (artists.size() > 0 && albums.size() > 0 && folders.size() > 0) {
            Log.d(tag, "Done filter into data");
        }

        try {
            addPlayListFirst();

            Log.d(tag, "Sync: "+sync);
            if (sync) {

            }

        } catch (Exception e) {
            Log.d(tag, "Unable to perform isSync");
            e.printStackTrace();
            Log.d(tag, e.getMessage());

        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        ((SplashActivity) mActivity).mTextSync.setText("Loading: "+values);
    }

    @Override
    protected void onPostExecute(Long aLong) {
        ((SplashActivity) mActivity).mTextSync.setText("Done");
        CountDownTimer count = new CountDownTimer(1000,3000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                Bitmap bitmap =
                        ImageUtils.getInstance(mActivity).getBitmapIntoPicasso(mSharedPrefsManager.getString(Constants.PREFERENCES.SaveAlbumID,"0"));
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.putExtra("SendAlbumId", bitmap);
                mActivity.finish();
                mActivity.startActivity(intent);
            }
        };
        count.start();

    }
}
