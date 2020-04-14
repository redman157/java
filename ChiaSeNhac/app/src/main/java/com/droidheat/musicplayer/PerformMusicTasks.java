package com.droidheat.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;

import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.activities.SplashActivity;
import com.droidheat.musicplayer.database.CategorySongs;
import com.droidheat.musicplayer.database.FavouriteSongs;
import com.droidheat.musicplayer.database.Playlist;
import com.droidheat.musicplayer.database.PlaylistSongs;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class PerformMusicTasks  extends AsyncTask<String, Integer, Long> {
    private WeakReference<Activity> weakReference;
    private Boolean sync;
    private String tag = "SplashActivityAsyncTaskLog";
    private SongManager mSongManager;
    private SharedPrefsManager mSharedPrefsManager;
    private Playlist mPlaylist;
    private PlaylistSongs mPlaylistSongs;
    private CategorySongs mCategorySongs;
    private FavouriteSongs mFavouriteSongs;
    private Activity mActivity;

    public PerformMusicTasks(Activity mActivity, Boolean sync) {
        this.mActivity = mActivity;
        this.weakReference = new WeakReference<>(mActivity);
        this.sync = sync;
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(mActivity);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(mActivity);
        mPlaylist = Playlist.getInstance();
        mCategorySongs = CategorySongs.getInstance();
        mPlaylistSongs = PlaylistSongs.getInstance();
        mFavouriteSongs = FavouriteSongs.getInstance();
    }

    private void addPlayListFirst(String name){
        mPlaylist.newRenderDB(mActivity);
        if (!mSongManager.isExistsPlayList(name)){
            if (mPlaylist.getCount() == 0){
                mSongManager.addPlaylist(name);
            }else {
                Log.d(tag, "PlayList đã tạo và có giá trị");
            }
        }else {

        }

    }
    @Override
    protected Long doInBackground(String... strings) {
        ArrayList<HashMap<String, String>> artists = mSongManager.artists();


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
