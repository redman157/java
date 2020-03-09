package com.droidheat.musicplayer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.droidheat.musicplayer.PerformBackgroundTasks;
import com.droidheat.musicplayer.database.Database;
import com.droidheat.musicplayer.database.Playlist;
import com.droidheat.musicplayer.manager.CommonUtils;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsManager;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "SplashActivityLog";
    private boolean sync = false;
    private ProgressBar mProgressBar;
    private TextView mTextSync;
    private SongsManager mSongsManager;
    private SharedPrefsManager mSharedPrefsManager;
    private PerformBackgroundTasks mPerformBackgroundTasks;
    /* access modifiers changed from: protected */
    @SuppressLint({"SetTextI18n"})
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        if ((getIntent().getFlags() & 4194304) != 0) {
            finish();
            return;
        }
        mProgressBar = findViewById(R.id.progressBar);
        mTextSync = findViewById(R.id.textView10);
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);

 /*       mPerformBackgroundTasks = PerformBackgroundTasks.getInstance();
        mPerformBackgroundTasks.setContext(this);*/
        mProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, (new CommonUtils(this).accentColor(mSharedPrefsManager))),
                PorterDuff.Mode.MULTIPLY);


        setTextStatus();

        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT > 22) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck == PermissionChecker.PERMISSION_DENIED ||
                    permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
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
            } else {
                new PerformBackgroundTasks().execute("tasks");
                Log.d(TAG,"no need for permissions");
            }
        }
    }

    private void setTextStatus() {
        mSongsManager = SongsManager.getInstance();
        if (getIntent().getBooleanExtra(Constants.VALUE.SYNC, false)) {

            mSongsManager.setContext(this);
            mSongsManager.sync();
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 1) {
            return;
        }
        if (grantResults.length <= 0 || grantResults[0] != 0) {
            Toast.makeText(this, "Application needs permission to run. Go to Settings > Apps > Noad Player to allow permission.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        new PerformBackgroundTasks().execute("tasks");
    }

    @SuppressLint("StaticFieldLeak")
    private class PerformBackgroundTasks extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... params) {
            SongsManager mSongsManager = SongsManager.getInstance();
            mSongsManager.setContext(SplashActivity.this);

            ArrayList<HashMap<String,String>> artists = mSongsManager.artists();
            if (artists.size() > 0) {
                SharedPrefsManager prefsManager = new SharedPrefsManager();
                prefsManager.setContext(SplashActivity.this);
                (prefsManager).getString(Constants.PREFERENCES.HOME_ARTIST,
                        artists.get((new Random()).nextInt(artists.size())).get("artist"));
            }

            try {
                // -- Creating Playlist
                Playlist playlist = Playlist.getInstance();
                playlist.newRenderDB(SplashActivity.this, Constants.VALUE.PLAYLIST_DB);
                playlist.open();
                if (playlist.getCount() == 0) {
                    mSongsManager.addPlaylist("Playlist 1");
                }
                playlist.close();

                if (sync) {

                    for (int s = 0; s < mSongsManager.getAllPlayLists().size(); s++) {
                        int playlistID = Integer.parseInt(mSongsManager.getAllPlayLists().get(s).get("ID"));
                        ArrayList<SongModel> mPlayListSongs =
                                mSongsManager.playlistSongs(playlistID);

                        if (!mPlayListSongs.isEmpty()) {
                            for (int j = 0; j < mPlayListSongs.size(); j++) {
                                Log.d(TAG, "Playlist: Search if current song " + j + " is not similar with song in new songs list");
                                if (!mSongsManager.allSongs().contains(mPlayListSongs.get(j))) {
                                    Log.d(TAG, "Playlist: current playlist song doesn't exist in allSongs," +
                                            " so lets see if only path is changed or user has moved the song");
                                    boolean isFound = false;
                                    for (int k = 0; k < mSongsManager.allSongs().size(); k++) {
                                        if ((mSongsManager.allSongs().get(k).getTitle() +
                                                mSongsManager.allSongs().get(k).getDuration())
                                                .equals(mPlayListSongs.get(j).getTitle() +
                                                        mPlayListSongs.get(j).getDuration())) {
                                            Log.d(TAG, "Playlist: song " + j + " does exist and is probably moved," +
                                                    " so lets change broken song with lasted");
                                            mPlayListSongs.remove(j);
                                            mPlayListSongs.add(j, mSongsManager.allSongs().get(k));
                                            Log.d(TAG, "Playlist: index doesn't change and we changed broken song. All good!");
                                            isFound = true;
                                            k = mSongsManager.allSongs().size();
                                        }
                                    }
                                    if (!isFound) {
                                        Log.d(TAG, "Playlist: " + j + " song is deleted from device");
                                        mPlayListSongs.remove(j);
                                        Log.d(TAG, "Playlist: since a song is removed," +
                                                " on doing next song loop will skip one song");
                                        j--;
                                        Log.d(TAG, "Playlist: j-- to ensure for loop stays on same song");
                                    }
                                } else {
                                    Log.d(TAG, "Playlist: Song " + j + " is okay");
                                }
                                if (isCancelled()) {
                                    break; // REMOVE IF NOT USED IN A FOR LOOP
                                }
                            }
                            // Update favourite songs list
                            mSongsManager.updatePlaylistSongs(playlistID,
                                    mPlayListSongs);
                            Log.d(TAG, "Playlist: done!");
                        }

                        //Todo Re-add song but change fields

                    }

                    // -- Checking Favourites
                    ArrayList<SongModel> favSongs =
                            new ArrayList<>(mSongsManager.favouriteSongs());
                    if (!favSongs.isEmpty()) {
                        Log.d(TAG, "Favourites: Search if current hashMap is not similar with song in new songs list");
                        for (int j = 0; j < favSongs.size(); j++) {
                            if (!mSongsManager.allSongs().contains(favSongs.get(j))) {
                                Log.d(TAG, "Favourites: current favourite doesn't exist in allSongs," +
                                        " so lets see if only path is changed or user has moved the song");
                                boolean isFound = false;
                                for (int i = 0; i < mSongsManager.allSongs().size(); i++) {
                                    if ((mSongsManager.allSongs().get(i).getTitle() +
                                            mSongsManager.allSongs().get(i).getDuration())
                                            .equals(favSongs.get(j).getTitle() +
                                                    favSongs.get(j).getDuration())) {
                                        Log.d(TAG, "Favourites: songs does exist and is probably moved," +
                                                " so lets change broken song with lasted");
                                        favSongs.remove(j);
                                        favSongs.add(j, mSongsManager.allSongs().get(i));
                                        Log.d(TAG, "Favourites: index doesn't change and we changed broken song. All good");
                                        isFound = true;
                                        i = mSongsManager.allSongs().size();
                                    }
                                }
                                if (!isFound) {
                                    Log.d(TAG, "Favourites: songs is deleted from device");
                                    favSongs.remove(j);
                                    Log.d(TAG, "Favourites: since a song is removed," +
                                            " on doing next song loop will skip one song");
                                    j--;
                                    Log.d(TAG, "Favourites: j-- to ensure for loop stays on same song");
                                }
                            }
                        }
                        // Update favourite songs list
                        Log.d(TAG, "Favourites: done!");
                        mSongsManager.updateFavouritesList(favSongs);
                    }

                    // -- Checking Most Played
                    ArrayList<SongModel> mostPlayed =
                            mSongsManager.mostPlayedSongs();
                    if (!mostPlayed.isEmpty()) {
                        Log.d(TAG, "MostPlayed: Search if current hashMap is not similar with song in new songs list");
                        for (int j = 0; j < mostPlayed.size(); j++) {
                            if (!mSongsManager.allSongs().contains(mostPlayed.get(j))) {
                                Log.d(TAG, "MostPlayed: current song " + j + " doesn't exist in allSongs," +
                                        " so lets see if only path is changed or user has moved the song");
                                boolean isFound = false;
                                for (int i = 0; i < mSongsManager.allSongs().size(); i++) {
                                    if ((mSongsManager.allSongs().get(i).getTitle() +
                                            mSongsManager.allSongs().get(i).getDuration())
                                            .equals(mostPlayed.get(j).getTitle() +
                                                    mostPlayed.get(j).getDuration())) {
                                        Log.d(TAG, "MostPlayed: songs does exist and is probably moved," +
                                                " so lets change broken song with lasted");
                                        mostPlayed.remove(j);
                                        mostPlayed.add(j, mSongsManager.allSongs().get(i));
                                        Log.d(TAG, "MostPlayed: index doesn't change and we changed broken song. All good!");
                                        isFound = true;
                                        i = mSongsManager.allSongs().size();
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
                        mSongsManager.updateMostPlayedList(mostPlayed);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG,"Unable to perform sync");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //setUpdatedTextView(values[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            startActivity(new Intent(SplashActivity.this,
                    HomeActivity.class));
            finish();
        }
    }
}
