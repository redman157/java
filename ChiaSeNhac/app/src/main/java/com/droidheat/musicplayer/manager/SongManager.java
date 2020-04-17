package com.droidheat.musicplayer.manager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.database.AllPlaylist;
import com.droidheat.musicplayer.database.CategorySongs;
import com.droidheat.musicplayer.database.SongOfPlayList;
import com.droidheat.musicplayer.models.SongModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongManager {
    /*private ArrayList<HashMap<String, String>> getAlbumsList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> artists = new ArrayList<>();*/

    private Map<String, ArrayList<SongModel>> albumLists = new HashMap<>();
    private Map<String, ArrayList<SongModel>> artistLists = new HashMap<>();
    private Map<String, ArrayList<SongModel>> folderLists = new HashMap<>();
    private ArrayList<SongModel> mainList = new ArrayList<>();
    private ArrayList<SongModel> queue = new ArrayList<>();
    private String TAG = "SongsManagerConsole";
    /* access modifiers changed from: private */
    private Context context;
    /* access modifiers changed from: private */
    private SharedPrefsManager mSharedPrefsManager;
    private int totalSongs;
    private AllPlaylist mAllPlaylist;
    private SongOfPlayList mSongOfPlayList;
    private CategorySongs mCategorySongs;

    @SuppressLint("StaticFieldLeak")
    private static SongManager instance;

    public static SongManager getInstance() {
        if (instance == null){
            instance = new SongManager();
        }
        return instance;
    }

    private SongManager() {

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        mAllPlaylist = new AllPlaylist(context);
        mSongOfPlayList = new SongOfPlayList(context);
        mCategorySongs = new CategorySongs(context);
        this.mSharedPrefsManager = new SharedPrefsManager();
        this.mSharedPrefsManager.setContext(context);
        totalSongs = mSharedPrefsManager.getInteger(Constants.PREFERENCES.TOTAL_SONGS, -1);

    }

    public void initDatabase(){

    }
    public void installData(){
        // lần đầu tiên cài app
        if (totalSongs == -1) {
            grabIfEmpty();
            if (queue.isEmpty()) {
                try {
                    // sao lưu file
                    Type type = new TypeToken<ArrayList<SongModel>>() {
                    }.getType();
                    ArrayList<SongModel> restoreData = new Gson().fromJson(mSharedPrefsManager.getString(Constants.PREFERENCES.KEY, null), type);
                    replaceQueue(restoreData);
                    Log.d(TAG, "Retrieved queue from storage in SongManager. " + restoreData.size() + " mainList!");
                } catch (Exception e) {
                    Log.d(TAG, "Unable to retrieve data while queue is empty.");
                    Log.d(TAG, e.getMessage());
                }
            }
        }else if (mainList != null && mainList.size() > 0){
            if (totalSongs == mainList.size()){
                return;
            }else {
                crawlData();
            }
        }
    }

    public int getCurrentMusic() {
        int pos = mSharedPrefsManager.getInteger(Constants.PREFERENCES.POSITION, -1);
        return pos;
    }

    public void setCurrentMusic(int position) {
        mSharedPrefsManager.setInteger(Constants.PREFERENCES.POSITION, position);
    }

    public ArrayList<SongModel> queue() {
        if (queue.isEmpty()){
            ArrayList<SongModel> list = new ArrayList<>(mainList);
            Collections.reverse(list);
            replaceQueue(list);
        }
        return queue;
    }

    public ArrayList<SongModel> allSortSongs() {
        grabIfEmpty(); // If no song in list
        // Sorted list of 0-9 A-Z
        ArrayList<SongModel> songs = new ArrayList<>(newSongs());
        Collections.sort(songs, new Comparator<SongModel>() {
            @Override
            public int compare(SongModel song1, SongModel song2) {
                return song1.getSongName().compareTo(song2.getSongName());
            }
        });
        return songs;
    }

    public ArrayList<SongModel> newSongs() {
        grabIfEmpty(); // If no song in list (new songs)

        ArrayList<SongModel> newSongs = new ArrayList<>(mainList);
        Collections.reverse(newSongs);
        return newSongs;
    }

    public ArrayList<SongModel> shuffleSongs(){
        grabIfEmpty(); // If no song in list (shuffle songs)
        ArrayList<SongModel> shuffleSongs = new ArrayList<>(mainList);
        Collections.shuffle(shuffleSongs);

        return shuffleSongs;
    }

    /*public ArrayList<HashMap<String, String>> getAlbumsList() {
        grabIfEmpty(); // getAlbumsList
        return albums;
    }*/


/*    public ArrayList<HashMap<String, String>> artists() {
        grabIfEmpty(); // artists
        return artists;
    }
    */

    public ArrayList<SongModel> albumSongs(String album) {
        ArrayList<SongModel> songs = new ArrayList<>();
        ArrayList<SongModel> list = new ArrayList<>(mainList);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAlbum().equals(album)) {
                songs.add(list.get(i));
            }
        }
        return songs;
    }



    public List<String> getAlbumIds(String rawAlbumIds) {
        String SPLIT_EXPRESSION = ";,,;,;;";
        List<String> list = new ArrayList<>();
        String[] albumIDs = rawAlbumIds.split(SPLIT_EXPRESSION);
        Collections.addAll(list, albumIDs);
        return list;
    }


    public ArrayList<SongModel> artistSongs(String artist) {
        ArrayList<SongModel> songs = new ArrayList<>();
        ArrayList<SongModel> list = new ArrayList<>(mainList);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getArtist().contains(artist)) {
                songs.add(list.get(i));
            }
        }
        return songs;
    }


    /*
     * Actions
     */

    public void isSync(boolean sync) {
        if (sync) {
            mainList.clear();
            albumLists.clear();
            folderLists.clear();
            artistLists.clear();
            queue.clear();
        }
        grabIfEmpty(); // isSync
    }

    public void addToQueue(SongModel song) {
        queue().add(song);
        (new CommonUtils(context)).showTheToast("Added to current queue!");
    }

    public void addToQueue(ArrayList<SongModel> arrayList) {
        ArrayList<SongModel> arrayList1 = new ArrayList<>(arrayList);
        if (arrayList1.size() > 0) {
            queue().addAll(arrayList1);
            (new CommonUtils(context)).showTheToast("Added to current queue!");
        } else {
            (new CommonUtils(context)).showTheToast("Nothing to add");
        }
    }

    public void playNext(SongModel song) {
        queue().add(getCurrentMusic() + 1, song);
        (new CommonUtils(context)).showTheToast("Playing next: " + song.getSongName());
    }

    public  boolean replaceQueue(final ArrayList<SongModel> list) {
        if (list != null && !list.isEmpty()) {
            clearQueue();
            queue.addAll(list);
            try {
                new Thread(new Runnable() {
                    public void run() {
                        mSharedPrefsManager.setString(Constants.PREFERENCES.KEY, new Gson().toJson(list));
                    }
                }).start();
            } catch (Exception e) {
                e.getStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    public void generateMenu(PopupMenu popupMenus, int[] options) {
        int i = 0;
        while (i < options.length) {
            String name = "INVALID";
            if (options[i] == R.id.play_musicUtils) {
                name = "Play";
            } else if (options[i] == R.id.play_next_musicUtils) {
                name = "Play NEXT";
            } else if (options[i] == R.id.add_to_queue_musicUtils) {
                name = "Add to Queue";
            } else if (options[i] == R.id.add_to_playlist_musicUtils) {
                name = "Add to AllPlaylist";
            } else if (options[i] == R.id.shuffle_play_musicUtils) {
                name = "Shuffle Play";
            } else if (options[i] == R.id.use_as_ringtone_musicUtils) {
                name = "Use as Ringtone";
            } else if (options[i] == R.id.remove_musicUtils) {
                name = "Remove";
            } else if (options[i] == R.id.info_musicUtils) {
                name = "Track Info";
            } else if (options[i] == R.id.goto_album_musicUtils) {
                name = "Go to Album";
            } else if (options[i] == R.id.goto_artist_musicUtils) {
                name = "Go to Artist";
            }
            popupMenus.getMenu().add(0, options[i], 1, name);
            MenuItem menuItem = popupMenus.getMenu().getItem(i);
            CharSequence menuTitle = menuItem.getTitle();
            SpannableString styledMenuTitle = new SpannableString(menuTitle);
            //styledMenuTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, menuTitle.length(), 0);
            menuItem.setTitle(styledMenuTitle);
            i++;
        }
    }

    public void play(int id, ArrayList<SongModel> array) {
        Log.d("MusicUtilsConsole", "Initiating the play request to MusicPlayback Service");
        if (!array.isEmpty()) {
            File file = new File(array.get(id).getPath());
            if (file.exists()) {
                replaceQueue(array);
                setCurrentMusic(id);
                Intent intent = new Intent(Constants.ACTION.PLAY);
                ContextCompat.startForegroundService(context, createExplicitFromImplicitIntent(intent));

            } else {
                Toast.makeText(context,
                        "Unable to play the song! Try syncing the library!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public  void shufflePlay(int id, ArrayList<SongModel> array) {
        ArrayList<SongModel> arrayList = new ArrayList<>(array);
        if (arrayList.size() > 0) {
            SongModel songModel = arrayList.get(id);
            arrayList.remove(id);
            Collections.shuffle(arrayList);
            arrayList.add(0, songModel);
            play(0, arrayList);
            (new CommonUtils(context)).showTheToast("Shuffling");
        }
    }

    public void shufflePlay(ArrayList<SongModel> songs) {
        ArrayList<SongModel> data = new ArrayList<>(songs);
        if (data.size() > 0) {
            Collections.shuffle(data);
            play(0, data);
            (new CommonUtils(context)).showTheToast("Shuffling");
        } else {
            (new CommonUtils(context)).showTheToast("Nothing to shuffle");
        }
    }

    public AlertDialog info(SongModel songModel) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(songModel.getSongName());
        builder.setMessage("\nFile Name: " + songModel.getFileName() + "\n\n" +
                "Song Title: " + songModel.getSongName() + "\n\n" +
                "Album: " + songModel.getAlbum() + "\n\n" +
                "Artist: " + songModel.getArtist() + "\n\n" +
                "File location: " + songModel.getPath());
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }

    public Intent createExplicitFromImplicitIntent(Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /*
     * Helper Functions. Keep them private or public as required
     */

    private void clearQueue() {
        queue.clear();
    }

    private int getIndex(String itemName, ArrayList<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            String auction = list.get(i).get("NAME_PLAYLIST");
            if (itemName.equals(auction)) {
                return i;
            }
        }
        return -1;
    }

    private int getIndexAlbum(String itemName, ArrayList<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            String auction = list.get(i).get("album");
            if (itemName.equals(auction)) {
                return i;
            }
        }

        return -1;
    }

    private int getIndexArtist(String itemName, ArrayList<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            String auction = list.get(i).get("artist");
            if (itemName.equals(auction)) {
                return i;
            }
        }
        return -1;
    }

    private void grabIfEmpty() {
        if (mainList.isEmpty()) {
            crawlData();
            Log.d(TAG, "Grabbing data for player...");
        } else {
            Log.d(TAG, "Data is present. Just setting context.");
        }
    }

    private void crawlData() {
        String[] STAR = {"*"};

        boolean excludeShortSounds = mSharedPrefsManager.getBoolean(Constants.PREFERENCES.excludeShortSounds, false);
        boolean excludeWhatsApp = mSharedPrefsManager.getBoolean(Constants.PREFERENCES.excludeWhatsAppSounds, false);

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor musicCursor = context.getContentResolver().query(uri, STAR, selection, null, null);
        Log.d(TAG, "Uri: "+uri.getPath() + " ==== Selection: "+ selection+ " ====== Cursor: "+musicCursor.getCount());

        if (musicCursor != null) {
            if (musicCursor.moveToFirst()) {
                do {
                    String duration = musicCursor
                            .getString(musicCursor
                                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
                    int currentDuration = Math.round(Integer.parseInt(duration));

                    if (currentDuration > ((excludeShortSounds) ? 60000 : 0)) {
                        if (!excludeWhatsApp || !musicCursor.getString(musicCursor
                                .getColumnIndex(MediaStore.Audio.Media.ALBUM)).equals("WhatsApp Audio")) {

                            String songName = musicCursor
                                    .getString(
                                            musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                                    .replace("_", " ").trim().replaceAll(" +", " ");
                            String path = musicCursor.getString(musicCursor
                                    .getColumnIndex(MediaStore.Audio.Media.DATA));
                            String title = musicCursor.getString(musicCursor
                                    .getColumnIndex(MediaStore.Audio.Media.TITLE)).replace("_", " ").trim().replaceAll(" +", " ");
                            String artistName = musicCursor.getString(musicCursor
                                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            String albumName = musicCursor.getString(musicCursor
                                    .getColumnIndex(MediaStore.Audio.Media.ALBUM));

                            long id = musicCursor.getColumnIndex
                                    (MediaStore.Audio.Media._ID);

                            String albumID = musicCursor
                                    .getString(
                                            musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                                    );

                            // Adding song to list
                            SongModel.Builder builder = new SongModel.Builder();
                            builder.setFileName(songName);
                            builder.setSongName(title);
                            builder.setArtist(artistName);
                            builder.setAlbum(albumName);
                            builder.setAlbumID(albumID);
                            builder.setPath(path);
                            builder.setTime(currentDuration);
                            builder.setID(String.valueOf(id));

                            mainList.add(builder.generate());
                        }
                    }
                }
                while (musicCursor.moveToNext());
            }

            setMainMusic(mainList);
            mSharedPrefsManager.setInteger(Constants.PREFERENCES.TOTAL_SONGS, mainList.size());
            musicCursor.close();
        }

        filterData(mainList);
        Log.d(TAG, "crawlData() performed");
    }

    /*
     * Albums Data && Artist Data && folder Data
     */
    private void filterData(ArrayList<SongModel> mainList){

        ArrayList<SongModel> allSongList = new ArrayList<>(mainList);

        for (int song = 0; song < allSongList.size(); song++) {
            String artist = allSongList.get(song).getArtist();
            String album = allSongList.get(song).getAlbum();
            String folder = allSongList.get(song).getPath();

            while (true) {
                if (artistLists.get(artist) != null) {
                    artistLists.get(artist).add(allSongList.get(song));
                    break;
                } else {
                    artistLists.put(artist, new ArrayList<SongModel>());
                }
            }

            while (true) {
                if (albumLists.get(album) != null) {
                    albumLists.get(album).add(allSongList.get(song));
                    break;
                } else {
                    albumLists.put(album, new ArrayList<SongModel>());
                }
            }

            while (true) {
                if (folderLists.get(folder) != null) {
                    folderLists.get(folder).add(allSongList.get(song));
                    break;
                } else {
                    folderLists.put(folder, new ArrayList<SongModel>());
                }
            }
        }
    }

    public Map<String, ArrayList<SongModel>> getAlbum() {
        grabIfEmpty();
        return albumLists;
    }

    public void setAlbum(Map<String, ArrayList<SongModel>> albumLists) {
        this.albumLists = albumLists;
    }

    public Map<String, ArrayList<SongModel>> getArtist() {
        grabIfEmpty();
        return artistLists;
    }

    public void setArtist(Map<String, ArrayList<SongModel>> artistLists) {
        this.artistLists = artistLists;
    }

    public Map<String, ArrayList<SongModel>> getFolder() {
        grabIfEmpty();
        return folderLists;
    }

    public void setFolder(Map<String, ArrayList<SongModel>> folderLists) {
        this.folderLists = folderLists;
    }

    public ArrayList<SongModel> getMainMusic() {
        return mainList;
    }

    public void setMainMusic(ArrayList<SongModel> mainList) {
        this.mainList = mainList;
    }

    public ArrayList<SongModel> getQueue() {
        return queue;
    }

    public AllPlaylist getAllPlaylistDB() {
        return mAllPlaylist;
    }

    public void setAllPlaylistDB(AllPlaylist allPlaylistDB) {
        this.mAllPlaylist = allPlaylistDB;
    }

    public SongOfPlayList getSongOfPlayListDB() {
        return mSongOfPlayList;
    }

    public void setSongOfPlayListDB(SongOfPlayList songOfPlayListDB) {
        this.mSongOfPlayList = songOfPlayListDB;
    }

    public CategorySongs getCategorySongsDB() {
        return mCategorySongs;
    }

    public void setCategorySongsDB(CategorySongs categorySongsDB) {
        this.mCategorySongs = categorySongsDB;
    }

}
