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
import com.droidheat.musicplayer.models.SongModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class PerformMusicTasks  extends AsyncTask<String, Integer, Long> {
    private WeakReference<Activity> weakReference;
    private Boolean sync;
    private String tag = "PerformMusicTasksLog";
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
            Log.d(tag, "PlayList chưa có tạo");
            mSongManager.addPlaylist("Playlist 1");
        }
        mPlaylist.close();
    }

    @Override
    protected Long doInBackground(String... strings) {
        ArrayList<HashMap<String, String>> artists = mSongManager.artists();
        if (artists.size() > 0) {
            SharedPrefsManager prefsManager = new SharedPrefsManager();
            prefsManager.setContext(mActivity);
            (prefsManager).getString(Constants.PREFERENCES.HOME_ARTIST,
                    artists.get((new Random()).nextInt(artists.size())).get("artist"));
        }

        try {
//            addPlayListFirst("Playlist 1");
            mPlaylist.newRenderDB(mActivity);
            if (mPlaylist.getCount() == 0){
                mPlaylist.addPlayList("Playlist 1");
            }
            Log.d(tag, "Sync: "+sync);
            if (sync) {
                for (int song = 0; song < mSongManager.getAllPlayLists().size(); song++) {
                    int playlistID = Integer
                            .parseInt(Objects.requireNonNull
                                    (mSongManager.getAllPlayLists()
                                            .get(song).get(Constants.VALUE.ID)));
                    // -- Checking PlayListSong
                    ArrayList<SongModel> mPlayListSongs = mSongManager.playlistSongs(playlistID);
                    if (!mPlayListSongs.isEmpty()) {
                        for (int item = 0; item < mPlayListSongs.size(); item++) {
                            Log.d(tag, "Playlist: Search if current song " + item + " is not similar " +
                                    "with song in new songs list");
                            if (!mSongManager.allSortSongs().contains(mPlayListSongs.get(item))) {
                                Log.d(tag, "Playlist: current playlist song doesn't exist in " +
                                        "allSortSongs," +
                                        " so lets see if only path is changed or user has moved the song");
                                boolean isFound = false;
                                for (int index = 0; index < mSongManager.allSortSongs().size(); index++) {
                                    if ((mSongManager.allSortSongs().get(index).getTitle() +
                                            mSongManager.allSortSongs().get(index).getDuration())
                                            .equals(mPlayListSongs.get(item).getTitle() +
                                                    mPlayListSongs.get(item).getDuration())) {
                                        Log.d(tag, "Playlist: song " + item + " does exist and is probably moved," +
                                                " so lets change broken song with lasted");
                                        mPlayListSongs.remove(item);
                                        mPlayListSongs.add(item, mSongManager.allSortSongs().get(index));
                                        Log.d(tag, "Playlist: position doesn't change and we changed broken song. All good!");
                                        isFound = true;
                                        index = mSongManager.allSortSongs().size();
                                    }
                                }

                                if (!isFound) {
                                    Log.d(tag, "Playlist: " + item + " song is deleted from device");
                                    mPlayListSongs.remove(item);
                                    Log.d(tag, "Playlist: since a song is removed," +
                                            " on doing next song loop will skip one song");
                                    item--;
                                    Log.d(tag, "Playlist: j-- to ensure for loop stays on same song");
                                }

                            } else {
                                Log.d(tag, "Playlist: Song " + item + " is okay");
                            }
                            if (isCancelled()) {
                                break; // REMOVE IF NOT USED IN A FOR LOOP
                            }

                        }
                        // Update favourite songs list
                        mSongManager.updatePlaylistSongs(playlistID, mPlayListSongs);
                        Log.d(tag, "Playlist: done!");
                    }
                    //Todo Re-add song but change fields
                }
                // -- Checking Favourites
                ArrayList<SongModel> mFavSongs = new ArrayList<>(mSongManager.favouriteSongs());
                if (!mFavSongs.isEmpty()) {
                    Log.d(tag, "Favourites: Search if current hashMap is not similar with song in new " +
                            "songs list");
                    for (int item = 0; item < mFavSongs.size(); item++) {
                        if (!mSongManager.allSortSongs().contains(mFavSongs.get(item))) {
                            Log.d(tag, "Favourites: current favourite doesn't exist in allSortSongs," +
                                    " so lets see if only path is changed or user has moved the song");
                            boolean isFound = false;
                            for (int i = 0; i < mSongManager.allSortSongs().size(); i++) {
                                if ((mSongManager.allSortSongs().get(i).getTitle() +
                                        mSongManager.allSortSongs().get(i).getDuration())
                                        .equals(mFavSongs.get(item).getTitle() +
                                                mFavSongs.get(item).getDuration())) {
                                    Log.d(tag, "Favourites: songs does exist and is probably moved," +
                                            " so lets change broken song with lasted");
                                    mFavSongs.remove(item);
                                    mFavSongs.add(item, mSongManager.allSortSongs().get(i));
                                    Log.d(tag, "Favourites: position doesn't change and we changed " +
                                            "broken song. All good");
                                    isFound = true;
                                    i = mSongManager.allSortSongs().size();
                                }
                            }

                            if (!isFound) {
                                Log.d(tag, "Favourites: songs is deleted from device");
                                mFavSongs.remove(item);
                                Log.d(tag, "Favourites: since a song is removed," +
                                        " on doing next song loop will skip one song");
                                item--;
                                Log.d(tag, "Favourites: j-- to ensure for loop stays on same song");
                            }
                        }
                    }
                    // Update favourite songs list
                    Log.d(tag, "Favourites: done!");
                    mSongManager.updateFavouritesList(mFavSongs);
                }

                // -- Checking Most Played
                ArrayList<SongModel> mostPlayed = mSongManager.mostPlayedSongs();
                if (!mostPlayed.isEmpty()) {
                    Log.d(tag, "MostPlayed: Search if current hashMap is not similar with song in new " +
                            "songs list");
                    for (int item = 0; item < mostPlayed.size(); item++) {
                        if (!mSongManager.allSortSongs().contains(mostPlayed.get(item))) {
                            Log.d(tag, "MostPlayed: current song " + item + " doesn't exist in " +
                                    "allSortSongs," +
                                    " so lets see if only path is changed or user has moved the song");
                            boolean isFound = false;
                            for (int i = 0; i < mSongManager.allSortSongs().size(); i++) {
                                if ((mSongManager.allSortSongs().get(i).getTitle() +
                                        mSongManager.allSortSongs().get(i).getDuration())
                                        .equals(mostPlayed.get(item).getTitle() +
                                                mostPlayed.get(item).getDuration())) {
                                    Log.d(tag, "MostPlayed: songs does exist and is probably moved," +
                                            " so lets change broken song with lasted");
                                    mostPlayed.remove(item);
                                    mostPlayed.add(item, mSongManager.allSortSongs().get(i));
                                    Log.d(tag, "MostPlayed: position doesn't change and we changed " +
                                            "broken song. All good!");
                                    isFound = true;
                                    i = mSongManager.allSortSongs().size();
                                }
                            }
                            if (!isFound) {
                                Log.d(tag, "MostPlayed: songs is deleted from device");
                                mostPlayed.remove(item);
                                Log.d(tag, "MostPlayed: since a song is removed," +
                                        " on doing next song loop will skip one song");
                                item--;
                                Log.d(tag, "MostPlayed: j-- to ensure for loop stays on same song");
                            }
                        }
                    }
                    // Update favourite songs list
                    Log.d(tag, "MostPlayed: done!");
                    mSongManager.updateMostPlayedList(mostPlayed);
                }

            }

        } catch (Exception e) {
            Log.d(tag, "Unable to perform sync");
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
