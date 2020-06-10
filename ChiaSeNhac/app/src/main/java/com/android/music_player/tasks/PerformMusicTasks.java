package com.android.music_player.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.android.music_player.activities.HomeActivity;
import com.android.music_player.activities.SplashActivity;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Map;

public class PerformMusicTasks  extends AsyncTask<String, Integer, Long> {
    private boolean sync;
    private String tag = "PerformMusicTasksLog";
    private MediaManager mMediaManager;
    private SharedPrefsUtils mSharedPrefsUtils;

    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;

    public PerformMusicTasks(Activity activity, Boolean sync) {
        mActivity = activity;
        
        this.sync = sync;
        mMediaManager = MediaManager.getInstance();


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
        mMediaManager.setContext(mActivity);
        mMediaManager.installData();

    }

    @Override
    protected Long doInBackground(String... strings) {

        Map<String, ArrayList<SongModel>> artists = mMediaManager.getArtist();
        Map<String, ArrayList<SongModel>> albums = mMediaManager.getAlbum();
        Map<String, ArrayList<SongModel>> folders = mMediaManager.getFolder();
        if (artists.size() > 0 && albums.size() > 0 && folders.size() > 0) {
            Log.d(tag, "Done filter into data");
//            Log.d(tag,
//                    "artist : "+ artists.size() +" albums : "+ albums.size() +" folders : "+ folders.size() );
            for (Map.Entry<String, ArrayList<SongModel>> entry : artists.entrySet()) {
                String k = entry.getKey();
                ArrayList<SongModel> v = entry.getValue();
                Log.d(tag, "artist name: "+ k +" SongModel: "+v.size());
            }

        }

        try {
            // lần đầu tiên cài app
            if(mMediaManager.isPlayListMost()){
                mMediaManager.addPlayListFirst();
            }
            Log.d(tag, "Sync: "+sync);
            if (sync) {

            }else {
                return null;
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
                        ImageHelper.getInstance(mActivity).getBitmapIntoPicasso(mSharedPrefsUtils.getString(Constants.PREFERENCES.SAVE_ALBUM_ID,"0"));
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.putExtra("SendAlbumId", bitmap);
                mActivity.finish();
                mActivity.startActivity(intent);
            }
        };
        count.start();

    }
}
