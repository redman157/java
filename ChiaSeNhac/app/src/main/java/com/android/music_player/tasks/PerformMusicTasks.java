package com.android.music_player.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.activities.SplashActivity;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;
import java.util.Map;

public class PerformMusicTasks  extends AsyncTask<String, Integer, Integer> {
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

    @SuppressLint("WrongThread")
    @Override
    protected Integer doInBackground(String... strings) {
        Log.d("VVV","doInBackground: "+strings[0]);

        Map<String, ArrayList<String>> artists = mMediaManager.getArtist();
        Map<String, ArrayList<String>> albums = mMediaManager.getAlbum();
        Map<String, ArrayList<String>> folders = mMediaManager.getFolder();
        if (artists.size() > 0 && albums.size() > 0 && folders.size() > 0) {
            Log.d(tag, "Done filter into data");
            for (Map.Entry<String, ArrayList<String>> entry : artists.entrySet()) {
                String k = entry.getKey();
                ArrayList<String> v = entry.getValue();
                Log.d(tag, "artist name: "+ k +" MusicModel: "+v.size());
            }
        }
        try {
            // lần đầu tiên cài app
            Log.d(tag, "Sync: "+sync);
            if (sync) {

            }else {
                return 15;
            }

        } catch (Exception e) {
            Log.d(tag, "Unable to perform isSync");
            e.printStackTrace();
            Log.d(tag, e.getMessage());
        }finally {
        }
        return 100;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        ((SplashActivity) mActivity).mTextSync.setText(R.string.updating_songs);
        ((SplashActivity) mActivity).mTextSync.setText("Loading: "+values[0]+" %");
    }

    @Override
    protected void onPostExecute(Integer aLong) {
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
