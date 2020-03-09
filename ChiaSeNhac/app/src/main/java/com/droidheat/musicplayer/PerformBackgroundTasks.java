package com.droidheat.musicplayer;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.droidheat.musicplayer.database.Playlist;
import com.droidheat.musicplayer.models.SongModel;
import com.droidheat.musicplayer.utils.SharedPrefsUtils;
import com.droidheat.musicplayer.utils.SongsUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class PerformBackgroundTasks extends AsyncTask<String, Integer, Long> {
    private String TAG = "SplashActivityAsyncTaskLog";
    private Playlist playlist;
    private SharedPrefsUtils sharedPrefsUtils;
    private SongsUtils songsUtils;
    private Boolean sync;
    private WeakReference<Activity> weakReference;


    private static PerformBackgroundTasks instance;

    public static PerformBackgroundTasks getInstance() {
        if (instance == null){
            instance = new PerformBackgroundTasks();
        }
        return instance;
    }

    /* access modifiers changed from: protected */
    public void onProgressUpdate(Integer... numArr) {
    }

    private PerformBackgroundTasks() {

    }

    public void initTask(Activity activity, boolean bool){
        this.weakReference = new WeakReference<>(activity);
        this.sync = bool;
        this.songsUtils.setContext(activity);
        this.songsUtils = new SongsUtils(activity);

        this.sharedPrefsUtils = new SharedPrefsUtils(activity);
        this.playlist = Playlist.getInstance();
        this.playlist.newRenderDB(activity, Constants.VALUE.PLAYLIST_DB);
    }
    /* access modifiers changed from: protected */
    public Long doInBackground(String... strArr) {
        ArrayList artists = this.songsUtils.artists();
        if (artists.size() > 0) {
            this.sharedPrefsUtils.writeSharedPrefs(
                    Constants.PREFERENCES.HOME_ARTIST,
                    (String) ((HashMap) artists.get(new Random().nextInt(artists.size()))).get(Constants.PREFERENCES.ARTIST));
        }
        try {
            this.playlist.open();
            if (this.playlist.getCount() == 0) {
                this.songsUtils.addPlaylist("Playlist 1");
            }
            this.playlist.close();
            if (sync) {
                for (int index = 0; index < this.songsUtils.getAllPlayLists().size(); index++) {
                    int parseInt = Integer.parseInt((String) Objects.requireNonNull(((HashMap) this.songsUtils.getAllPlayLists().get(index)).get("ID")));
                    ArrayList playlistSongs = this.songsUtils.playlistSongs(parseInt);
                    if (!playlistSongs.isEmpty()) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= playlistSongs.size()) {
                                break;
                            }
                            String str = this.TAG;

                            Log.d(str,"Playlist: Search if current song "+i2 +" is not similar with song in new songs list");
                            if (!this.songsUtils.allSongs().contains(playlistSongs.get(i2))) {
                                Log.d(this.TAG, "Playlist: current playlist song doesn't exist in allSongs, so lets see if only PATH is changed or user has moved the song");
                                int i3 = 0;
                                boolean z = false;
                                while (i3 < this.songsUtils.allSongs().size()) {
                                    StringBuilder sb2 = new StringBuilder();
                                    sb2.append((this.songsUtils.allSongs().get(i3)).getTitle());
                                    sb2.append((this.songsUtils.allSongs().get(i3)).getDuration());
                                    String sb3 = sb2.toString();
                                    StringBuilder sb4 = new StringBuilder();
                                    sb4.append(((SongModel) playlistSongs.get(i2)).getTitle());
                                    sb4.append(((SongModel) playlistSongs.get(i2)).getDuration());
                                    if (sb3.equals(sb4.toString())) {
                                        String str2 = this.TAG;
                                        StringBuilder sb5 = new StringBuilder();
                                        sb5.append("Playlist: song ");
                                        sb5.append(i2);
                                        sb5.append(" does exist and is probably moved, so lets change broken song with lasted");
                                        Log.d(str2, sb5.toString());
                                        playlistSongs.remove(i2);
                                        playlistSongs.add(i2, this.songsUtils.allSongs().get(i3));
                                        Log.d(this.TAG, "Playlist: index doesn't change and we changed broken song. All good!");
                                        i3 = this.songsUtils.allSongs().size();
                                        z = true;
                                    }
                                    i3++;
                                }
                                if (!z) {
                                    String str3 = this.TAG;
                                    StringBuilder sb6 = new StringBuilder();
                                    sb6.append("Playlist: ");
                                    sb6.append(i2);
                                    sb6.append(" song is deleted from device");
                                    Log.d(str3, sb6.toString());
                                    playlistSongs.remove(i2);
                                    Log.d(this.TAG, "Playlist: since a song is removed, on doing next song loop will skip one song");
                                    i2--;
                                    Log.d(this.TAG, "Playlist: j-- to ensure for loop stays on same song");
                                }
                            } else {
                                String str4 = this.TAG;
                                StringBuilder sb7 = new StringBuilder();
                                sb7.append("Playlist: Song ");
                                sb7.append(i2);
                                sb7.append(" is okay");
                                Log.d(str4, sb7.toString());
                            }
                            if (isCancelled()) {
                                break;
                            }
                            i2++;
                        }
                        this.songsUtils.updatePlaylistSongs(parseInt, playlistSongs);
                        Log.d(this.TAG, "Playlist: done!");
                    }
                }
                ArrayList arrayList = new ArrayList(this.songsUtils.favouriteSongs());
                if (!arrayList.isEmpty()) {
                    Log.d(this.TAG, "Favourites: Search if current hashMap is not similar with song in new songs list");
                    int i4 = 0;
                    while (i4 < arrayList.size()) {
                        if (!this.songsUtils.allSongs().contains(arrayList.get(i4))) {
                            Log.d(this.TAG, "Favourites: current favourite doesn't exist in allSongs, so lets see if only PATH is changed or user has moved the song");
                            int i5 = 0;
                            boolean z2 = false;
                            while (i5 < this.songsUtils.allSongs().size()) {
                                StringBuilder sb8 = new StringBuilder();
                                sb8.append(((SongModel) this.songsUtils.allSongs().get(i5)).getTitle());
                                sb8.append(((SongModel) this.songsUtils.allSongs().get(i5)).getDuration());
                                String sb9 = sb8.toString();
                                StringBuilder sb10 = new StringBuilder();
                                sb10.append(((SongModel) arrayList.get(i4)).getTitle());
                                sb10.append(((SongModel) arrayList.get(i4)).getDuration());
                                if (sb9.equals(sb10.toString())) {
                                    Log.d(this.TAG, "Favourites: songs does exist and is probably moved, so lets change broken song with lasted");
                                    arrayList.remove(i4);
                                    arrayList.add(i4, this.songsUtils.allSongs().get(i5));
                                    Log.d(this.TAG, "Favourites: index doesn't change and we changed broken song. All good");
                                    i5 = this.songsUtils.allSongs().size();
                                    z2 = true;
                                }
                                i5++;
                            }
                            if (!z2) {
                                Log.d(this.TAG, "Favourites: songs is deleted from device");
                                arrayList.remove(i4);
                                Log.d(this.TAG, "Favourites: since a song is removed, on doing next song loop will skip one song");
                                i4--;
                                Log.d(this.TAG, "Favourites: j-- to ensure for loop stays on same song");
                            }
                        }
                        i4++;
                    }
                    Log.d(this.TAG, "Favourites: done!");
                    this.songsUtils.updateFavouritesList(arrayList);
                }
                ArrayList mostPlayedSongs = this.songsUtils.mostPlayedSongs();
                if (!mostPlayedSongs.isEmpty()) {
                    Log.d(this.TAG, "MostPlayed: Search if current hashMap is not similar with song in new songs list");
                    int i6 = 0;
                    while (i6 < mostPlayedSongs.size()) {
                        if (!this.songsUtils.allSongs().contains(mostPlayedSongs.get(i6))) {
                            String str5 = this.TAG;
                            StringBuilder sb11 = new StringBuilder();
                            sb11.append("MostPlayed: current song ");
                            sb11.append(i6);
                            sb11.append(" doesn't exist in allSongs, so lets see if only PATH is changed or user has moved the song");
                            Log.d(str5, sb11.toString());
                            int i7 = 0;
                            boolean z3 = false;
                            while (i7 < this.songsUtils.allSongs().size()) {
                                StringBuilder sb12 = new StringBuilder();
                                sb12.append(((SongModel) this.songsUtils.allSongs().get(i7)).getTitle());
                                sb12.append(((SongModel) this.songsUtils.allSongs().get(i7)).getDuration());
                                String sb13 = sb12.toString();
                                StringBuilder sb14 = new StringBuilder();
                                sb14.append(((SongModel) mostPlayedSongs.get(i6)).getTitle());
                                sb14.append(((SongModel) mostPlayedSongs.get(i6)).getDuration());
                                if (sb13.equals(sb14.toString())) {
                                    Log.d(this.TAG, "MostPlayed: songs does exist and is probably moved, so lets change broken song with lasted");
                                    mostPlayedSongs.remove(i6);
                                    mostPlayedSongs.add(i6, this.songsUtils.allSongs().get(i7));
                                    Log.d(this.TAG, "MostPlayed: index doesn't change and we changed broken song. All good!");
                                    i7 = this.songsUtils.allSongs().size();
                                    z3 = true;
                                }
                                i7++;
                            }
                            if (!z3) {
                                Log.d(this.TAG, "MostPlayed: songs is deleted from device");
                                mostPlayedSongs.remove(i6);
                                Log.d(this.TAG, "MostPlayed: since a song is removed, on doing next song loop will skip one song");
                                i6--;
                                Log.d(this.TAG, "MostPlayed: j-- to ensure for loop stays on same song");
                            }
                        }
                        i6++;
                    }
                    Log.d(this.TAG, "MostPlayed: done!");
                    this.songsUtils.updateMostPlayedList(mostPlayedSongs);
                }
            }
        } catch (Exception e) {
            Log.d(this.TAG, "Unable to perform sync");
            e.printStackTrace();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Long l) {
       /* ( this.weakReference.get()).startActivity(new Intent((Context) this.weakReference.get(), HomeActivity.class));
        ( this.weakReference.get()).finish();*/
        Log.d("BBB", "Đã Xong :))))");
    }
}