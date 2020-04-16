package com.droidheat.musicplayer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.PerformMusicTasks;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.database.Playlist;
import com.droidheat.musicplayer.manager.CommonUtils;
import com.droidheat.musicplayer.manager.ImageUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongManager;
import com.droidheat.musicplayer.models.SongModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "SplashActivityLog";
    private boolean sync = false;
    private ProgressBar mProgressBar;
    public TextView mTextSync;
    private SongManager mSongManager;
    private SharedPrefsManager mSharedPrefsManager;

    /* access modifiers changed from: protected */
    //Binding this Client to the AudioPlayer Service

    @SuppressLint({"SetTextI18n"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        mProgressBar = findViewById(R.id.progressBar1);
        mTextSync = findViewById(R.id.textView10);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        setTextStatus();

        checkPermission();
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT > 22) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                // No explanation needed, we can request the permission.

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Request for permissions");
                alertDialog.setMessage("For music player to work we need your permission to access" +
                        " files on your device.");
                alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    }
                });
                alertDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alertDialog.show();
                Log.d(TAG, "asking permission");
            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                new PerformMusicTasks(this, sync).execute("tasks");
            } else {
                (new CommonUtils(this)).showTheToast("Please enable permission from " +
                        "Settings > Apps > Noad Player > Permissions.");
            }
        } else {
            new PerformMusicTasks(this, sync).execute("tasks");
        }

    }

    private void setTextStatus() {
        mSongManager = SongManager.getInstance();
        if (getIntent().getBooleanExtra(Constants.VALUE.SYNC, false)) {

            mSongManager.setContext(this);
            mSongManager.sync();
            mTextSync.setText("Syncing..");
            this.sync = true;
        } else {
            mTextSync.setText("Initiating..");
        }

    }

    private void setDialogBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request for permissions");
        builder.setMessage("For music player to work we need your permission to access files on your device.");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 1);
            }
        });
        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                SplashActivity.this.finish();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                new PerformMusicTasks(this, sync).execute("tasks");
                //weGotPermissions();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Toast.makeText(this, "Application needs permission to run. Go to Settings > Apps > " +
                        "Noad Player to allow permission.", Toast.LENGTH_SHORT).show();
                finish();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PerformBackgroundTasks extends AsyncTask<String, Integer, Long> {
        private WeakReference<Activity> weakReference;
        private Boolean sync;
        private String TAG = "SplashActivityAsyncTaskLog";
        private SongManager mSongManager;
        private SharedPrefsManager sharedPrefsManager;
        private Playlist playlist;

        PerformBackgroundTasks(Activity activity, Boolean sync) {
            this.weakReference = new WeakReference<>(activity);
            this.sync = sync;
            this.mSongManager = SongManager.getInstance();
            this.mSongManager.setContext(activity);
            this.sharedPrefsManager = new SharedPrefsManager();
            this.sharedPrefsManager.setContext(activity);
            this.playlist = Playlist.getInstance();
            this.playlist.newRenderDB(activity);
        }
        @Override
        protected Long doInBackground(String... params) {

            SongManager mSongManager = SongManager.getInstance();
            mSongManager.setContext(SplashActivity.this);

            ArrayList<HashMap<String,String>> artists = mSongManager.artists();
            if (artists.size() > 0) {
                SharedPrefsManager prefsManager = new SharedPrefsManager();
                prefsManager.setContext(SplashActivity.this);
                (prefsManager).getString(Constants.PREFERENCES.HOME_ARTIST,
                        artists.get((new Random()).nextInt(artists.size())).get("artist"));
            }

            try {
                // -- Creating Playlist
                Playlist mPlaylist = Playlist.getInstance();

                mPlaylist.newRenderDB(SplashActivity.this);

                if (mPlaylist.getCount() == 0) {
                    mSongManager.addPlaylist("Playlist 1");

                }

                mPlaylist.close();

                if (sync) {
                    for (int song = 0; song < mSongManager.getAllPlayLists().size(); song++) {
                        int playlistID = Integer
                                .parseInt(Objects.requireNonNull
                                        (mSongManager.getAllPlayLists()
                                                .get(song).get("ID")));

                        ArrayList<SongModel> mPlayListSongs = mSongManager.playlistSongs(playlistID);

                        if (!mPlayListSongs.isEmpty()) {
                            for (int item = 0; item < mPlayListSongs.size(); item++) {
                                Log.d(TAG, "Playlist: Search if current song " + item + " is not similar with song in new songs list");
                                if (!mSongManager.allSortSongs().contains(mPlayListSongs.get(item))) {
                                    Log.d(TAG, "Playlist: current playlist song doesn't exist in allSortSongs," +
                                            " so lets see if only path is changed or user has moved the song");
                                    boolean isFound = false;
                                    for (int fItem = 0; fItem < mSongManager.allSortSongs().size(); fItem++) {
                                        if ((mSongManager.allSortSongs().get(fItem).getTitle() +
                                                mSongManager.allSortSongs().get(fItem).getDuration())
                                                .equals(mPlayListSongs.get(item).getTitle() +
                                                        mPlayListSongs.get(item).getDuration())) {
                                            Log.d(TAG, "Playlist: song " + item + " does exist and is probably moved," +
                                                    " so lets change broken song with lasted");
                                            mPlayListSongs.remove(item);
                                            mPlayListSongs.add(item, mSongManager.allSortSongs().get(fItem));
                                            Log.d(TAG, "Playlist: position doesn't change and we changed broken song. All good!");
                                            isFound = true;
                                            fItem = mSongManager.allSortSongs().size();
                                        }
                                    }
                                    if (!isFound) {
                                        Log.d(TAG, "Playlist: " + item + " song is deleted from device");
                                        mPlayListSongs.remove(item);
                                        Log.d(TAG, "Playlist: since a song is removed," +
                                                " on doing next song loop will skip one song");
                                        item--;
                                        Log.d(TAG, "Playlist: j-- to ensure for loop stays on same song");
                                    }
                                } else {
                                    Log.d(TAG, "Playlist: Song " + item + " is okay");
                                }
                                if (isCancelled()) {
                                    break; // REMOVE IF NOT USED IN A FOR LOOP
                                }
                            }
                            // Update favourite songs list
                            mSongManager.updatePlaylistSongs(playlistID, mPlayListSongs);
                            Log.d(TAG, "Playlist: done!");
                        }

                        //Todo Re-add song but change fields

                    }

                    // -- Checking Favourites
                    ArrayList<SongModel> mFavSongs =
                            new ArrayList<>(mSongManager.favouriteSongs());
                    if (!mFavSongs.isEmpty()) {
                        Log.d(TAG, "Favourites: Search if current hashMap is not similar with song in new songs list");
                        for (int j = 0; j < mFavSongs.size(); j++) {
                            if (!mSongManager.allSortSongs().contains(mFavSongs.get(j))) {
                                Log.d(TAG, "Favourites: current favourite doesn't exist in allSortSongs," +
                                        " so lets see if only path is changed or user has moved the song");
                                boolean isFound = false;
                                for (int i = 0; i < mSongManager.allSortSongs().size(); i++) {
                                    if ((mSongManager.allSortSongs().get(i).getTitle() +
                                            mSongManager.allSortSongs().get(i).getDuration())
                                            .equals(mFavSongs.get(j).getTitle() +
                                                    mFavSongs.get(j).getDuration())) {
                                        Log.d(TAG, "Favourites: songs does exist and is probably moved," +
                                                " so lets change broken song with lasted");
                                        mFavSongs.remove(j);
                                        mFavSongs.add(j, mSongManager.allSortSongs().get(i));
                                        Log.d(TAG, "Favourites: position doesn't change and we changed broken song. All good");
                                        isFound = true;
                                        i = mSongManager.allSortSongs().size();
                                    }
                                }
                                if (!isFound) {
                                    Log.d(TAG, "Favourites: songs is deleted from device");
                                    mFavSongs.remove(j);
                                    Log.d(TAG, "Favourites: since a song is removed," +
                                            " on doing next song loop will skip one song");
                                    j--;
                                    Log.d(TAG, "Favourites: j-- to ensure for loop stays on same song");
                                }
                            }
                        }
                        // Update favourite songs list
                        Log.d(TAG, "Favourites: done!");
                        mSongManager.updateFavouritesList(mFavSongs);
                    }

                    // -- Checking Most Played
                    ArrayList<SongModel> mostPlayed = mSongManager.mostPlayedSongs();
                    if (!mostPlayed.isEmpty()) {
                        Log.d(TAG, "MostPlayed: Search if current hashMap is not similar with song in new songs list");
                        for (int j = 0; j < mostPlayed.size(); j++) {
                            if (!mSongManager.allSortSongs().contains(mostPlayed.get(j))) {
                                Log.d(TAG, "MostPlayed: current song " + j + " doesn't exist in allSortSongs," +
                                        " so lets see if only path is changed or user has moved the song");
                                boolean isFound = false;
                                for (int i = 0; i < mSongManager.allSortSongs().size(); i++) {
                                    if ((mSongManager.allSortSongs().get(i).getTitle() +
                                            mSongManager.allSortSongs().get(i).getDuration())
                                            .equals(mostPlayed.get(j).getTitle() +
                                                    mostPlayed.get(j).getDuration())) {
                                        Log.d(TAG, "MostPlayed: songs does exist and is probably moved," +
                                                " so lets change broken song with lasted");
                                        mostPlayed.remove(j);
                                        mostPlayed.add(j, mSongManager.allSortSongs().get(i));
                                        Log.d(TAG, "MostPlayed: position doesn't change and we changed broken song. All good!");
                                        isFound = true;
                                        i = mSongManager.allSortSongs().size();
                                    }
                                }
                                if (!isFound) {
                                    Log.d(TAG, "MostPlayed: songs is deleted from device");
                                    mostPlayed.remove(j);
                                    Log.d(TAG, "MostPlayed: since a song is removed," +
                                            " on doing next song loop will skip one song");
                                    j--;
                                    Log.d(TAG, "MostPlayed: j-- to ensure for loop stays on same song");
                                }
                            }
                        }
                        // Update favourite songs list
                        Log.d(TAG, "MostPlayed: done!");
                        mSongManager.updateMostPlayedList(mostPlayed);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG,"Unable to perform sync");
                e.printStackTrace();
                Log.d(TAG, e.getMessage());

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mTextSync.setText("Loading: "+values);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            mTextSync.setText("Done");
            CountDownTimer count = new CountDownTimer(1000,3000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Bitmap bitmap =
                            ImageUtils.getInstance(SplashActivity.this).getBitmapIntoPicasso(mSharedPrefsManager.getString(Constants.PREFERENCES.SaveAlbumID,"0"));
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);

                    intent.putExtra("SendAlbumId", bitmap);
                    finish();
                    startActivity(intent);


                }
            };
            count.start();

        }
    }
}
