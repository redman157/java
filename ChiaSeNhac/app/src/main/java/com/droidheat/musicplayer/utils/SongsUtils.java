package com.droidheat.musicplayer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore.Audio.Media;

import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.database.CategorySongs;
import com.droidheat.musicplayer.database.FavouriteList;
import com.droidheat.musicplayer.database.Playlist;
import com.droidheat.musicplayer.database.PlaylistSongs;
import com.droidheat.musicplayer.adapters.PlaylistFragmentAdapterSimple;
import com.droidheat.musicplayer.models.SongModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;

public class SongsUtils {
    private static ArrayList<HashMap<String, String>> albums = new ArrayList<>();
    private static ArrayList<HashMap<String, String>> artists = new ArrayList<>();
    private static ArrayList<SongModel> mainList = new ArrayList<>();
    private static ArrayList<SongModel> queue = new ArrayList<>();
    private String TAG = "SongsManagerConsole";
    /* access modifiers changed from: private */
    private Context context;
    /* access modifiers changed from: private */
    public SharedPrefsUtils sharedPrefsUtils;
    private PlaylistSongs playlistSongs;
    private FavouriteList favouriteList;
    private CategorySongs categorySongs;
    private Playlist playlist;


    @SuppressLint("StaticFieldLeak")
    private static SongsUtils instance;

    public static SongsUtils getInstance() {
        if (instance == null){
            instance = new SongsUtils();
        }
        return instance;
    }

    private SongsUtils() {

        this.sharedPrefsUtils = new SharedPrefsUtils(getContext());
        grabIfEmpty();
        if (queue.isEmpty()) {
            try {
                Type type = new TypeToken<ArrayList<SongModel>>() {
                }.getType();
                ArrayList<SongModel> restoreData = new Gson().fromJson(sharedPrefsUtils.readSharedPrefsString("key", null), type);
                replaceQueue(restoreData);
                Log.d(TAG, "Retrieved queue from storage in SongsUtils. " + restoreData.size() + " mainList!");
            } catch (Exception e) {
                Log.d(TAG, "Unable to retrieve data while queue is empty.");
            }
        }
    }
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public int getCurrentMusicID() {
        int musicID = this.sharedPrefsUtils.readSharedPrefsInt(Constants.PREFERENCES.MUSIC_ID, 0);
        return (musicID > -1) ? musicID: 0;
    }

    public void setCurrentMusicID(int id) {
        this.sharedPrefsUtils.writeSharedPrefs(Constants.PREFERENCES.MUSIC_ID, id);
    }

    public ArrayList<SongModel> queue() {
        if (queue.isEmpty()) {
            ArrayList<SongModel> models = new ArrayList<>();
            Collections.reverse(models);
            replaceQueue(models);
        }
        return queue;
    }

    public ArrayList<SongModel> allSongs() {
        grabIfEmpty();
        ArrayList<SongModel> songs = new ArrayList<>(SongsUtils.mainList);
        Collections.sort(songs, new Comparator<SongModel>() {
            public int compare(SongModel songModel, SongModel songModel2) {
                return songModel.getTitle().compareTo(songModel2.getTitle());
            }
        });
        return songs;
    }

    public ArrayList<SongModel> newSongs() {
        grabIfEmpty();
        ArrayList<SongModel> songs = new ArrayList<>(SongsUtils.mainList);
        Collections.reverse(songs);
        return songs;
    }

    public ArrayList<HashMap<String, String>> albums() {
        grabIfEmpty();
        return albums;
    }

    public ArrayList<SongModel> albumSongs(String nameSong) {
        ArrayList<SongModel> songs = new ArrayList<>();
        ArrayList<SongModel> list = new ArrayList<>(songs);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAlbum().equals(albums)) {
                songs.add(list.get(i));
            }
        }
        return songs;
    }

    public ArrayList<HashMap<String, String>> artists() {
        grabIfEmpty();
        return artists;
    }

    public List<String> getAlbumIds(String rawAlbumIds) {
        String SPLIT_EXPRESSION = ";,,;,;;";
        List<String> list = new ArrayList<>();
        String[] albumIDs = rawAlbumIds.split(SPLIT_EXPRESSION);
        Collections.addAll(list, albumIDs);
        return list;
    }

    public ArrayList<SongModel> artistSongs(String str) {
        ArrayList<SongModel> songModels = new ArrayList<>();
        ArrayList list = new ArrayList(mainList);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (((SongModel) list.get(i)).getArtist().contains(str)) {
                songModels.add((SongModel) list.get(i));
            }
        }
        return songModels;
    }

    public ArrayList<HashMap<String, String>> getAllPlayLists() {
        playlist = Playlist.getInstance();
        playlist.newRenderDB(getContext(),Constants.VALUE.PLAYLIST_DB);
        playlist.open();
        ArrayList<HashMap<String, String>> allPlayList = new ArrayList<>();
        if (playlist.getCount() > 0) {
            allPlayList = playlist.getAllRows();
        }
        playlist.close();
        return allPlayList;
    }

    public HashMap<String, String> getPlaylist(int i) {
        playlist = Playlist.getInstance();
        playlist.newRenderDB(getContext(),Constants.VALUE.PLAYLIST_DB);
        playlist.open();
        HashMap<String, String> row = playlist.getRow((long) i);
        playlist.close();
        return row;
    }

    public void addPlaylist(String name) {
        playlist = Playlist.getInstance();
        playlist.newRenderDB(getContext(),Constants.VALUE.PLAYLIST_DB);
        playlist.open();
        playlist.addRow(name);
        playlist.close();
    }

    public boolean ifPlaylistPresent(String name) {
        playlist = Playlist.getInstance();
        playlist.newRenderDB(getContext(),Constants.VALUE.PLAYLIST_DB);
        playlist.open();
        boolean searchPlaylist = playlist.searchPlaylist(name);
        playlist.close();
        return searchPlaylist;
    }

    public void deletePlaylist(int id) {
        playlist = Playlist.getInstance();
        playlist.newRenderDB(getContext(),Constants.VALUE.PLAYLIST_DB);
        playlist.open();
        playlist.deleteRow((long) id);
        playlist.close();
    }

    public void removePlaylistSong(int i, ArrayList<SongModel> arrayList) {
        playlistSongs = PlaylistSongs.getInstance();
        playlistSongs.newRenderDB(getContext() ,Constants.VALUE.PLAYLISTSONGS_DB);
        playlistSongs.open();
        playlistSongs.deleteAll(i);
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            playlistSongs.addRow((long) i, (SongModel) arrayList.get(i2));
        }
        playlistSongs.close();
    }

    public ArrayList<SongModel> playlistSongs(int i) {
        ArrayList<SongModel> arrayList = new ArrayList<>();
        playlistSongs = PlaylistSongs.getInstance();
        playlistSongs.newRenderDB(getContext() ,Constants.VALUE.PLAYLISTSONGS_DB);;
        playlistSongs.open();
        if (playlistSongs.getCount((long) i) > 0) {
            arrayList = playlistSongs.getAllRows(i);
        }
        playlistSongs.close();
        return arrayList;
    }

    public void updatePlaylistSongs(int id, ArrayList<SongModel> models) {
        playlistSongs = PlaylistSongs.getInstance();

        playlistSongs.newRenderDB(getContext() ,Constants.VALUE.PLAYLISTSONGS_DB);
        playlistSongs.open();
        playlistSongs.deleteAll(id);
        for (int index = 0; index < models.size(); index++) {
            playlistSongs.addRow((long) id,  models.get(index));
        }
        playlistSongs.close();
    }

    public ArrayList<SongModel> favouriteSongs() {
        favouriteList = FavouriteList.getInstance();
        favouriteList.newRenderDB(getContext(),Constants.VALUE.FAVS_DB);
        favouriteList.open();
        ArrayList<SongModel> arrayList = new ArrayList<>(favouriteList.getAllRows());
        favouriteList.close();
        return arrayList;
    }

    public boolean addToFavouriteSongs(SongModel songModel) {
        favouriteList = FavouriteList.getInstance();
        favouriteList.newRenderDB(getContext(),Constants.VALUE.FAVS_DB);
        favouriteList.open();
        favouriteList.addRow(songModel);
        favouriteList.close();
        return true;
    }

    public void updateFavouritesList(ArrayList<SongModel> models) {
        favouriteList = FavouriteList.getInstance();
        favouriteList.newRenderDB(getContext(),Constants.VALUE.FAVS_DB);

        favouriteList.open();
        favouriteList.deleteAll();
        Collections.reverse(models);
        for (int INDEX = 0; INDEX < models.size(); INDEX++) {
            favouriteList.addRow((SongModel) models.get(INDEX));
        }
        favouriteList.close();
    }

    public ArrayList<SongModel> mostPlayedSongs() {
        categorySongs = CategorySongs.getInstance();
        categorySongs.newRenderDB(getContext(), Constants.VALUE.CATEGORIES_DB);
        categorySongs.open();
        ArrayList<SongModel> allRows = categorySongs.getAllRows(1);
        categorySongs.close();
        return allRows;
    }

    public void updateMostPlayedList(ArrayList<SongModel> arrayList) {
        categorySongs = CategorySongs.getInstance();
        categorySongs.newRenderDB(getContext(), Constants.VALUE.CATEGORIES_DB);
        categorySongs.open();
        categorySongs.deleteAll(1);
        for (int i = 0; i < arrayList.size(); i++) {
            categorySongs.addRow(1, (SongModel) arrayList.get(i));
        }
        categorySongs.close();
    }

    public void sync() {
        mainList.clear();
        albums.clear();
        artists.clear();
        grabIfEmpty();
    }

    public void addToQueue(SongModel songModel) {
        queue().add(songModel);
        new CommonUtils(this.getContext()).showTheToast("Added to current queue!");
    }

    public void addToQueue(ArrayList<SongModel> arrayList) {
        ArrayList arrayList2 = new ArrayList(arrayList);
        if (arrayList2.size() > 0) {
            queue().addAll(arrayList2);
            new CommonUtils(this.getContext()).showTheToast("Added to current queue!");
            return;
        }
        new CommonUtils(this.getContext()).showTheToast("Nothing to add");
    }

    public void playNext(SongModel songModel) {
        queue().add(getCurrentMusicID() + 1, songModel);
        CommonUtils commonUtils = new CommonUtils(this.getContext());
        StringBuilder sb = new StringBuilder();
        sb.append("Playing next: ");
        sb.append(songModel.getTitle());
        commonUtils.showTheToast(sb.toString());
    }

    public boolean replaceQueue(final ArrayList<SongModel> list) {
        if (list != null && !list.isEmpty()) {
            clearQueue();
            queue.addAll(list);
            try {
                new Thread(new Runnable() {
                    public void run() {
                        sharedPrefsUtils.writeSharedPrefs("key", new Gson().toJson(list));
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
        String name = "INVALID";
        for (int i =0 ; i< options.length; i++){
            switch (options[i]){
                case R.id.play_musicUtils:
                    name = "Play";
                    break;

                case R.id.play_next_musicUtils:
                    name = "Play Next";
                    break;

                case R.id.add_to_queue_musicUtils:
                    name = "Add to Queue";
                    break;

                case R.id.add_to_playlist_musicUtils:
                    name = "Add to Playlist";
                    break;

                case R.id.shuffle_play_musicUtils:
                    name = "Shuffle Play";
                    break;

                case R.id.use_as_ringtone_musicUtils:
                    name = "Use as Ringtone";
                    break;

                case R.id.remove_musicUtils:
                    name = "Remove";
                    break;

                case R.id.info_musicUtils:
                    name = "Track Info";
                    break;

                case R.id.goto_album_musicUtils:
                    name = "Go to Album";
                    break;

                case R.id.goto_artist_musicUtils:
                    name = "Go to Artist";
                    break;
            }
            popupMenus.getMenu().add(0, options[i], 1, name);
            MenuItem menuItem = popupMenus.getMenu().getItem(i);
            CharSequence menuTitle = menuItem.getTitle();
            SpannableString styledMenuTitle = new SpannableString(menuTitle);
            //styledMenuTitle.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 0, menuTitle.length(), 0);
            menuItem.setTitle(styledMenuTitle);
        }
    }

    public void play(int id, ArrayList<SongModel> list) {
        Log.d("MusicUtilsConsole", "Initiating the play request to MusicPlayback Service");

        if (list.isEmpty()) {
            return;
        }
        if (new File(( list.get(id)).getPath()).exists()) {
            replaceQueue(list);
            setCurrentMusicID(id);
            ContextCompat.startForegroundService(
                    this.getContext(),
                    createExplicitFromImplicitIntent(new Intent(Constants.ACTION.ACTION_PLAY)));
            return;
        }
        Toast.makeText(this.getContext(), "Unable to play the song! Try syncing the library!", Toast.LENGTH_LONG).show();
    }

    public void playNext(ArrayList<SongModel> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            playNext(list.get(size));
        }
        new CommonUtils(this.getContext()).showTheToast("Playing this list next!");
    }

    public void addToPlaylist(final SongModel hash) {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_addtoplaylist);

        ListView listView =  dialog.findViewById(R.id.listView);
        ImageView imageView =  dialog.findViewById(R.id.add_playlist);
        final PlaylistFragmentAdapterSimple playlistFragmentAdapterSimple = new PlaylistFragmentAdapterSimple(this.getContext());

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                playlistSongs = PlaylistSongs.getInstance();
                playlistSongs.newRenderDB(getContext(), Constants.VALUE.PLAYLISTSONGS_DB);
                playlistSongs.open();
                int playListID = Integer.parseInt(Objects.requireNonNull(getAllPlayLists().get(position).get("ID")));
                if (!playlistSongs.getAllRows(playListID).contains(hash)) {
                    playlistSongs.addRow(playListID, hash);
                    (new CommonUtils(getContext())).showTheToast(hash.getTitle() + " is added to playlist! ");
                } else {
                    (new CommonUtils(getContext())).showTheToast("Error: Song is already in Playlist!");
                }
                playlistSongs.close();
                if (dialog.isShowing()) {
                    dialog.cancel();
                }
            }
        });
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SongsUtils.this.addPlayListDialog(playlistFragmentAdapterSimple);
            }
        });
        listView.setAdapter(playlistFragmentAdapterSimple);
        dialog.show();
    }


    /* access modifiers changed from: private */
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addPlayListDialog(final PlaylistFragmentAdapterSimple playlistFragmentAdapterSimple) {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_add_playlist);

        final EditText editText = dialog.findViewById(R.id.editText);
        Context context2 = this.getContext();
        editText.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context2, new CommonUtils(context2).accentColor(this.sharedPrefsUtils))));
        ((InputMethodManager) this.getContext().getSystemService(Constants.VALUE.INPUT_METHOD)).showSoftInput(editText, 1);
        Button button = dialog.findViewById(R.id.btnCreate);
        Context context3 = this.getContext();
        button.setTextColor(ContextCompat.getColor(context3, new CommonUtils(context3).accentColor(this.sharedPrefsUtils)));
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String obj = editText.getText().toString();
                if (!obj.isEmpty()) {
                    SongsUtils.this.addPlaylist(obj);
                    playlistFragmentAdapterSimple.notifyDataSetChanged();
                    dialog.cancel();
                    return;
                }
                Toast.makeText(SongsUtils.this.getContext(), "Please enter playlist NAME.", 0).show();
            }
        });
        ( dialog.findViewById(R.id.btnCancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void shufflePlay(int i, ArrayList<SongModel> arrayList) {
        ArrayList arrayList2 = new ArrayList(arrayList);
        if (arrayList2.size() > 0) {
            SongModel songModel = (SongModel) arrayList2.get(i);
            arrayList2.remove(i);
            Collections.shuffle(arrayList2);
            arrayList2.add(0, songModel);
            play(0, arrayList2);
            new CommonUtils(this.getContext()).showTheToast("Shuffling");
        }
    }

    public void shufflePlay(ArrayList<SongModel> arrayList) {
        ArrayList arrayList2 = new ArrayList(arrayList);
        if (arrayList2.size() > 0) {
            Collections.shuffle(arrayList2);
            play(0, arrayList2);
            new CommonUtils(this.getContext()).showTheToast("Shuffling");
            return;
        }
        new CommonUtils(this.getContext()).showTheToast("Nothing to shuffle");
    }

    public AlertDialog info(SongModel songModel) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(songModel.getTitle());
        builder.setMessage("\nFile Name: " + songModel.getFileName() + "\n\n" +
                "Song Title: " + songModel.getTitle() + "\n\n" +
                "Album: " + songModel.getAlbum() + "\n\n" +
                "Artist: " + songModel.getArtist() + "\n\n" +
                "Length: " + songModel.getDuration() + "\n\n" +
                "File location: " + songModel.getPath());
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    private Intent createExplicitFromImplicitIntent(Intent implicitIntent) {
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryBroadcastReceivers(implicitIntent,0);

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

    private void clearQueue() {
        queue.clear();
    }

    private int getIndexAlbum(String albumName, ArrayList<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            if (albumName.equals((list.get(i)).get("ALBUM"))) {
                return i;
            }
        }
        return -1;
    }

    private int getIndexArtist(String artistName, ArrayList<HashMap<String, String>> list) {
        for (int i = 0; i < list.size(); i++) {
            if (artistName.equals((list.get(i)).get("ARTIST"))) {
                return i;
            }
        }
        return -1;
    }

    private void grabIfEmpty() {
        if (mainList.isEmpty()) {
            grabData();
            Log.d(this.TAG, "Grabbing data for player...");
            return;
        }
        Log.d(this.TAG, "Data is present. Just setting activity.");
    }

    private void grabData() {
        String[] STAR = {"*"};

        boolean excludeShortSounds = sharedPrefsUtils.readSharedPrefsBoolean(Constants.PREFERENCES.excludeShortSounds, false);
        boolean excludeWhatsApp = sharedPrefsUtils.readSharedPrefsBoolean(Constants.PREFERENCES.excludeWhatsAppSounds, false);

        Cursor cursor;
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        String selection = Media.IS_MUSIC+" != 0";

        cursor = getContext().getContentResolver().query(uri, STAR, selection, null, null);

        if (cursor != null){
            if (cursor.moveToFirst()) {
                do {
                    String duration = cursor
                            .getString(cursor
                                    .getColumnIndex(Media.DURATION));
                    int currentDuration = Math.round(Integer
                            .parseInt(duration));
                    Log.d("BBB", "currentDuration: " + currentDuration);
                    if (currentDuration > ((excludeShortSounds) ? 60000 : 0)) {
                        if (!excludeWhatsApp || !cursor.getString(cursor
                                .getColumnIndex(Media.ALBUM)).equals("WhatsApp Audio")) {
                            String songName = cursor
                                    .getString(
                                            cursor.getColumnIndex(Media.DISPLAY_NAME))
                                    .replace("_", " ").trim().replaceAll(" +", " ");
                            Log.d("BBB", "Song Name: " + songName);
                            String path = cursor.getString(cursor
                                    .getColumnIndex(Media.DATA));
                            Log.d("BBB", "PATH: " + path);
                            String title = cursor.getString(cursor
                                    .getColumnIndex(Media.TITLE)).replace("_", " ").trim().replaceAll(" +", " ");
                            Log.d("BBB", "title: " + title);
                            String artistName = cursor.getString(cursor
                                    .getColumnIndex(Media.ARTIST));
                            Log.d("BBB", "ARTIST Name: " + artistName);
                            String albumName = cursor.getString(cursor
                                    .getColumnIndex(Media.ALBUM));
                            Log.d("BBB", "ALBUM Name: " + albumName);
                            String albumID = cursor
                                    .getString(
                                            cursor.getColumnIndex(Media.ALBUM_ID)
                                    );
                            Log.d("BBB", "ALBUM ID: " + albumID);

                            TimeZone tz = TimeZone.getTimeZone("UTC");
                            SimpleDateFormat df = new SimpleDateFormat("mm:ss", Locale.getDefault());
                            df.setTimeZone(tz);
                            String time = String.valueOf(df.format(currentDuration));

                            // Adding song to list
                            SongModel songModel = new SongModel();
                            songModel.setFileName(songName);
                            songModel.setTitle(title);
                            songModel.setArtist(artistName);
                            songModel.setAlbum(albumName);
                            songModel.setAlbumID(albumID);
                            songModel.setPath(path);
                            songModel.setDuration(time);

                            mainList.add(songModel);
                        }
                    }
                }while (cursor.moveToFirst()) ;
            }
                cursor.close();
        }

        /*
         * Albums Data
         */

        ArrayList<SongModel> allSongList = new ArrayList<>(mainList);
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        for (int i = 0; i < allSongList.size(); i++) {
            String name = allSongList.get(i).getAlbum();
            String artist = allSongList.get(i).getArtist();
            int albumIndex = -1;
            if (list.size() > 0) {
                for (int j = 0; j < list.size(); j++) {
                    String auction = list.get(j).get("ALBUM");
                    if (name.equals(auction)) {
                        albumIndex = j;
                    }
                }
            }
            if (albumIndex == -1) {
                HashMap<String, String> song = new HashMap<>();
                song.put("ALBUM", name);
                song.put("ARTIST", artist);
                list.add(song);
            }

        }

        ArrayList<HashMap<String, String>> list2 = new ArrayList<>();
        Map<String, String> sortedMap = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            sortedMap.put(Objects.requireNonNull(list.get(i).get("ALBUM")),
                    Objects.requireNonNull(list.get(i).get("ALBUM")));
        }
        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            HashMap<String, String> song = new HashMap<>();
            String title = entry.getValue();
            int index = getIndexAlbum(title, list);
            song.put("ALBUM", list.get(index).get("ALBUM"));
            song.put("ARTIST", list.get(index).get("ARTIST"));
            list2.add(song);
        }

        albums.addAll(list2);

    }
}
