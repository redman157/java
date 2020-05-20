package com.android.music_player.managers;

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

import com.android.music_player.MusicLibrary;
import com.android.music_player.R;
import com.android.music_player.database.AllPlaylist;
import com.android.music_player.database.CategorySongs;
import com.android.music_player.database.RelationSongs;
import com.android.music_player.database.SongOfPlayList;
import com.android.music_player.database.Statistic;
import com.android.music_player.models.SongModel;
import com.android.music_player.tasks.RenamePlayListTask;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class SongManager {
    /*private ArrayList<HashMap<String, String>> getAlbumsList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> artists = new ArrayList<>();*/

    private Map<String, ArrayList<SongModel>> mAlbumLists = new HashMap<>();
    private Map<String, ArrayList<SongModel>> mArtistLists = new HashMap<>();
    private Map<String, ArrayList<SongModel>> mFolderLists = new HashMap<>();
    private Set<SongModel> mSongsMain = new HashSet<SongModel>();

    private ArrayList<SongModel> queue = new ArrayList<>();
    private ArrayList<SongModel> shuffleSongs = new ArrayList<>();
    private String TAG = "SongsManagerConsole";
    /* access modifiers changed from: private */
    private Context mContext;
    /* access modifiers changed from: private */
    private SharedPrefsUtils mSharedPrefsUtils;
    private int mTotalSong;
    private AllPlaylist mAllPlaylist;
    private SongOfPlayList mSongOfPlayList;
    private CategorySongs mCategorySongs;
    private Statistic mStatistic;
    private RelationSongs mRelationSongs;
    @SuppressLint("StaticFieldLeak")
    private static SongManager instance;
    private String type;
    public static SongManager getInstance() {
        if (instance == null){
            instance = new SongManager();
        }
        return instance;
    }

    private SongManager() {

    }

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context mContext) {
        this.mContext = mContext;
        initDatabase();
    }

    public void initDatabase(){
        mAllPlaylist = new AllPlaylist(mContext);
        mSongOfPlayList = new SongOfPlayList(mContext);
        mCategorySongs = new CategorySongs(mContext);
        mStatistic = new Statistic(mContext);
        mRelationSongs = new RelationSongs(mContext);
        mSharedPrefsUtils = new SharedPrefsUtils(mContext);

        mTotalSong = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.TOTAL_SONGS, -1);
    }
    public void installData(){
        // lần đầu tiên cài app
        if (mTotalSong == -1) {
            grabIfEmpty();
            if (queue.isEmpty()) {
                try {
                    // sao lưu file
                    Type type = new TypeToken<ArrayList<SongModel>>() {
                    }.getType();
                    ArrayList<SongModel> restoreData = new Gson().fromJson(mSharedPrefsUtils.getString(Constants.PREFERENCES.KEY, null), type);
                    replaceQueue(restoreData);
                    Log.d(TAG, "Retrieved queue from storage in SongManager. " + restoreData.size() + " mSongsMain!");
                } catch (Exception e) {
                    Log.d(TAG, "Unable to retrieve data while queue is empty.");
                    Log.d(TAG, e.getMessage());
                }
            }
        }else if (mSongsMain != null && mSongsMain.size() > 0){
            if (mTotalSong == mSongsMain.size()){
                return;
            }else {
                crawlData();
            }
        }
    }

    public void setType(String type){
        mSharedPrefsUtils.setString(Constants.PREFERENCES.TYPE, type);
    }

    public String getType(){
        return mSharedPrefsUtils.getString(Constants.PREFERENCES.TYPE, "");
    }

    public void setCurrentSong(String path){
        Log.d("CCC","setCurrentSong: "+path);
        mSharedPrefsUtils.setString(Constants.PREFERENCES.CURRENT_SONG, path);
    }

    public SongModel getCurrentSong(){
        String current_path = mSharedPrefsUtils.getString(Constants.PREFERENCES.CURRENT_SONG, "");
        Log.d("CCC","getCurrentSong: "+current_path);
        Random rd = new Random();
        if (current_path.equals("")){
            return allSortSongs().get(rd.nextInt(mSongsMain.size()));
        }else {
            for (SongModel songModel : mSongsMain) {
                if (songModel.getPath().equals(current_path)){
                    return songModel;
                }
            }
        }
        return allSortSongs().get(rd.nextInt(mSongsMain.size()));
    }

    public int getPosition() {
        int pos = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION, -1);
        return pos;
    }

    public void setAlbumID(String albumID){
        mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID, albumID);
    }

    public String getAlbumID(){
        return mSharedPrefsUtils.getString(Constants.PREFERENCES.SAVE_ALBUM_ID, "");
    }
    public void setPosition(int position) {
        Log.d("XXX","setPosition: "+position );
        processEndOfList(position);
    }

    public boolean isPlayCurrentSong(String path){
        if (path.equals(getCurrentSong().getPath())){
            return true;
        }else {
            return false;
        }
    }


    public ArrayList<SongModel> getListSong(){
        this.type = getType();
        Log.d("BBB", "SongManager --- getListSong: "+type);
        ArrayList<SongModel> song = new ArrayList<>();
        if (mAllPlaylist.searchPlayList(type)) {
            song = getAllSongToPlayList(type);

            return song;
        }else {
            if (type.equals(Constants.VALUE.NEW_SONGS) || type.equals(Constants.VALUE.ALL_NEW_SONGS)) {
                song = SongManager.getInstance().newSongs();
                return song;
            } else if (type.equals(Constants.VALUE.ALL_SONGS) || type.equals("")) {
                song = SongManager.getInstance().allSortSongs();
                return song;
            } else if (type.equals(Constants.VALUE.SHUFFLE)){
                song = SongManager.getInstance().getShuffleSongs();
                return song;
            }
        }
        return null;
    }

    public ArrayList<SongModel> getListSong(String type){
//        Log.d("BBB", "SongManager --- getListSong: "+type);
        ArrayList<SongModel> song = new ArrayList<>();
        if (mAllPlaylist.searchPlayList(type)) {
            song = getAllSongToPlayList(type);

            return song;
        }else {
            if (type.equals(Constants.VALUE.NEW_SONGS) || type.equals(Constants.VALUE.ALL_NEW_SONGS)) {
                song = SongManager.getInstance().newSongs();
                return song;
            } else if (type.equals(Constants.VALUE.ALL_SONGS) || type.equals("")) {
                song = SongManager.getInstance().allSortSongs();
                return song;
            } else if (type.equals(Constants.VALUE.SHUFFLE)){
                song = SongManager.getInstance().getShuffleSongs();
                return song;
            }
        }
        return null;
    }

    public void processEndOfList(int position){

        if (getListSong()!= null) {
            int size = (getListSong().size());
            if (position >= size) {

                mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION, 0);
            } else if (position < 0) {
                mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION, size - 1);
            } else {
                mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION, position);
            }
        }
    }

    public ArrayList<SongModel> queue() {
        if (queue.isEmpty()){
            ArrayList<SongModel> list = new ArrayList<>(mSongsMain);
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

        ArrayList<SongModel> newSongs = new ArrayList<>(mSongsMain);
        Collections.reverse(newSongs);
        return newSongs;
    }

    public ArrayList<SongModel> shuffleSongs(ArrayList<SongModel> songs){
        grabIfEmpty(); // If no song in list (shuffle songs)
        ArrayList<SongModel> shuffleSongs = new ArrayList<>(songs);
        Collections.shuffle(shuffleSongs);
        return shuffleSongs;
    }

    public ArrayList<SongModel> albumSongs(String album) {
        ArrayList<SongModel> songs = new ArrayList<>();
        ArrayList<SongModel> list = new ArrayList<>(mSongsMain);
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
        ArrayList<SongModel> list = new ArrayList<>(mSongsMain);
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

    public SongModel getSong(String title){
        for (SongModel model: mSongsMain) {
            if (model.getSongName().equals(title)){
                return model;
            }
        }
        return null;
    }

    public void setShuffleSongs(ArrayList<SongModel> shuffleSongs){
        this.shuffleSongs = shuffleSongs;

    }

    public ArrayList<SongModel> getShuffleSongs(){
        return shuffleSongs;
    }

    public void isSync(boolean sync) {
        if (sync) {
            mSongsMain.clear();
            mAlbumLists.clear();
            mFolderLists.clear();
            mArtistLists.clear();
            queue.clear();
        }
        grabIfEmpty(); // isSync
    }

    public void addToQueue(SongModel song) {
        queue().add(song);
        Utils.ToastLong(mContext, "Added to current queue!");
    }

    public void addToQueue(ArrayList<SongModel> arrayList) {
        ArrayList<SongModel> arrayList1 = new ArrayList<>(arrayList);
        if (arrayList1.size() > 0) {
            queue().addAll(arrayList1);
            Utils.ToastLong(mContext, "Added to current queue!");
        } else {
            Utils.ToastLong(mContext, "Nothing to add");
        }
    }

    public void playNext(SongModel song) {
        queue().add(getPosition() + 1, song);
        Utils.ToastLong(mContext, "PLAYING next: " + song.getSongName());
    }

    public  boolean replaceQueue(final ArrayList<SongModel> list) {
        if (list != null && !list.isEmpty()) {
            clearQueue();
            queue.addAll(list);
            try {
                new Thread(new Runnable() {
                    public void run() {
                        mSharedPrefsUtils.setString(Constants.PREFERENCES.KEY, new Gson().toJson(list));
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
                setPosition(id);
                Intent intent = new Intent(Constants.ACTION.PLAY);
                ContextCompat.startForegroundService(mContext, createExplicitFromImplicitIntent(intent));

            } else {
                Toast.makeText(mContext,
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
            Utils.ToastLong(mContext,"Shuffling");
        }
    }

    public void shufflePlay(ArrayList<SongModel> songs) {
        ArrayList<SongModel> data = new ArrayList<>(songs);
        if (data.size() > 0) {
            Collections.shuffle(data);
            play(0, data);
            Utils.ToastLong(mContext,"Shuffling");
        } else {
            Utils.ToastLong(mContext,"Nothing to shuffle");
        }
    }

    public AlertDialog info(SongModel songModel) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
        PackageManager pm = mContext.getPackageManager();
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

    public boolean isPlayListMost(){
        if (!mStatistic.getMost(Constants.VALUE.PLAY_LIST).equals("")){

            return true;
        }else {
            return false;
        }
    }

    public ArrayList<String> getPlayListMost(){
        if (isPlayListMost()){
            return mStatistic.getPlayListMost();
        }else {
            return  null;
        }
    }

    public ArrayList<SongModel> getAllSongToPlayList(String playListName){
        ArrayList<SongModel> songs = new ArrayList<>();
        if (mAllPlaylist.searchPlayList(playListName)){
            ArrayList<String> allSongName = mRelationSongs.getAllSongName(playListName);
            if (allSongName != null && allSongName.size() > 0 ){
                for (String songName : allSongName){
                    songs.add(getSong(songName));
                }
                return songs;
            }else {

                Log.d(TAG, "SongManager --- getAllSongToPlayList: error getAllSongToPlayList");
                return null;

            }
        }else {
            return null;
        }
    }

    public void addPlayListFirst(){
        if (mAllPlaylist.getSize() == 0) {
            mAllPlaylist.addPlayList("Play List 1");
            mAllPlaylist.addPlayList("Play List 2");

        }else {
            Log.d(TAG, "SongManager --- addPlayListFirst size > 0");
        }
    }

    public boolean addSongToPlayList(String title, SongModel song){

        if (mSongOfPlayList.searchSong(song)){

            return true;
        }else {
            mSongOfPlayList.addSong(song);
            mRelationSongs.addRow(title, mSongOfPlayList.getSongName(song));
            return false;
        }
    }

    public void increase(SongModel songModel){
        mStatistic.increase(Constants.VALUE.SONG,songModel.getSongName());

    }
    public void increase(String title){
        mStatistic.increase(Constants.VALUE.PLAY_LIST,title);

    }

    public boolean addPlayList(String namePlayList){
        if (mAllPlaylist.searchPlayList(namePlayList)){

            return false;
        }else {
            mAllPlaylist.addPlayList(namePlayList);
            return true;
        }
    }

    public ArrayList<String> getAllPlayList(){
        if (mAllPlaylist.getAllPlayList() == null){
            addPlayListFirst();
        }
        return mAllPlaylist.getAllPlayList();
    }

    public boolean delete(String namePlayList, SongModel song){
        if (namePlayList.equals("")){
            if (mSongOfPlayList.searchSong(song)){
                mSongOfPlayList.deleteSong(song);
                Utils.ToastShort(mContext, "Đã xóa bài hát thành công: "+song.getSongName());
                return true;
            }else {
                Utils.ToastShort(mContext, "Xóa bài hát không thành công: "+song.getSongName());
                return false;
            }
        }else {
            if (mAllPlaylist.searchPlayList(namePlayList) && mSongOfPlayList.searchSong(song)){
                mSongOfPlayList.deleteSong(song);
                Utils.ToastShort(mContext,
                        "Xóa bài hát trong TITLE: "+namePlayList+" thành công: "+song.getSongName());
                return true;
            }else {
                Utils.ToastShort(mContext,
                        "Xóa bài hát trong TITLE: "+namePlayList+" không thành công: "+song.getSongName());
                return false;
            }
        }
    }

    public String renamePlayList(String main, String change){
        String temp = "";
        if (main.equals("")){
            Utils.ToastShort(mContext, "TITLE không thể tìm trống");
        }else if (change.equals("")){
            Utils.ToastShort(mContext, "Vui lòng nhập tên cần thay đổi");
        }else if (main.equals(change)){
            Utils.ToastShort(mContext, "Vui lòng nhập tên không được trùng với TITLE " +
                    "ban đầu");
        }else if (!main.equals("") && !change.equals("")) {
            if (mAllPlaylist.searchPlayList(main)) {
                try {
                    temp = new RenamePlayListTask(mContext, main, change).execute().get();
                    Utils.ToastShort(mContext, "Đã đổi tên TITLE thành công ");
                    return temp;
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                Utils.ToastShort(mContext, "Không tìm thấy TITLE để đổi");
            }
        }
        return temp;
    }

    public void changeMusic(String namePlayList, SongModel song){
        if (song == null){

        }else {
            if (mSongOfPlayList.searchSong(song)){

            }else {
                Utils.ToastShort(mContext,"Không tìm thấy TITLE: "+namePlayList);
            }
        }
    }

    private void grabIfEmpty() {
        if (mSongsMain.isEmpty()) {
            crawlData();
            Log.d(TAG, "Grabbing data for player...");
        } else {
            Log.d(TAG, "Data is present. Just setting context.");
        }
    }

    private void crawlData() {
        String[] mediaProjection = {"*"};
        String[] genresProjection = {
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID
        };
        boolean excludeShortSounds = mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.EXCLUDE_SHORT_SOUNDS, false);
        boolean excludeWhatsApp = mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.EXCLUDE_WHATS_APP_SOUNDS, false);

        Uri uriMedia = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = mContext.getContentResolver()
                .query(uriMedia, mediaProjection, selection, null, null);
        Log.d(TAG, "Uri: "+uriMedia.getPath() + " ==== Selection: "+ selection+ " ====== Cursor: "+cursor.getCount());


        if (cursor.moveToFirst()) {
            do {
                SongModel.Builder builder = null;
                String duration = cursor
                        .getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DURATION));
                int currentDuration = Math.round(Integer.parseInt(duration));
                if (currentDuration > ((excludeShortSounds) ? 60000 : 0)) {
                    if (!excludeWhatsApp ||
                            !cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)).equals("WhatsApp Audio")) {



                        int id_column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

                        int musicId = Integer.parseInt(cursor.getString(id_column_index));


                        String songName = cursor
                                .getString(
                                        cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                                .replace("_", " ").trim().replaceAll(" +", " ");
                        String path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.DATA));
                        String title = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.TITLE)).replace("_", " ").trim().replaceAll(" +", " ");
                        String artistName = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        String albumName = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Audio.Media.ALBUM));

                        String albumID = cursor
                                .getString(
                                        cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                                );


                        Uri uriGenres = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                        Cursor genresCursor = mContext.getContentResolver().query(uriGenres,
                                genresProjection, null, null, null);
                        String genres = "";
                        int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                        if (genresCursor.moveToFirst()) {
                            do {
                                genres = genresCursor.getString(genre_column_index);
                            } while (genresCursor.moveToNext());
                        }


                        // Adding song to list
                        builder = new SongModel.Builder();
                        builder.setFileName(songName);
                        builder.setSongName(title);
                        builder.setArtist(artistName);
                        builder.setAlbum(albumName);
                        builder.setAlbumID(albumID);
                        builder.setGenres(genres);
                        builder.setPath(path);
                        builder.setTime(currentDuration);

                        mSongsMain.add(builder.generate());

                        MusicLibrary.createMediaMetadataCompat(builder.generate());
                    }
                }

            }
            while (cursor.moveToNext());

            setMainMusic(mSongsMain);
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.TOTAL_SONGS, mSongsMain.size());
            cursor.close();
        }

        filterData(mSongsMain);
        Log.d(TAG, "CrawlData() performed");
    }

    /*
     * Albums Data && Artist Data && folder Data
     */
    private void filterData(Set<SongModel> mainList){

        ArrayList<SongModel> allSongList = new ArrayList<>(mainList);

        for (int song = 0; song < allSongList.size(); song++) {
            String artist = allSongList.get(song).getArtist();
            String album = allSongList.get(song).getAlbum();
            String folder = allSongList.get(song).getPath();

            while (true) {
                if (mArtistLists.get(artist) != null) {
                    mArtistLists.get(artist).add(allSongList.get(song));
                    break;
                } else {
                    mArtistLists.put(artist, new ArrayList<SongModel>());
                }
            }

            while (true) {
                if (mAlbumLists.get(album) != null) {
                    mAlbumLists.get(album).add(allSongList.get(song));
                    break;
                } else {
                    mAlbumLists.put(album, new ArrayList<SongModel>());
                }
            }

            while (true) {
                if (mFolderLists.get(folder) != null) {
                    mFolderLists.get(folder).add(allSongList.get(song));
                    break;
                } else {
                    mFolderLists.put(folder, new ArrayList<SongModel>());
                }
            }
        }
    }

    public Map<String, ArrayList<SongModel>> getAlbum() {
        grabIfEmpty();
        return mAlbumLists;
    }

    public void setAlbum(Map<String, ArrayList<SongModel>> albumLists) {
        this.mAlbumLists = albumLists;
    }

    public Map<String, ArrayList<SongModel>> getArtist() {
        grabIfEmpty();
        return mArtistLists;
    }

    public void setArtist(Map<String, ArrayList<SongModel>> artistLists) {
        this.mArtistLists = artistLists;
    }

    public Map<String, ArrayList<SongModel>> getFolder() {
        grabIfEmpty();
        return mFolderLists;
    }

    public void setFolder(Map<String, ArrayList<SongModel>> folderLists) {
        this.mFolderLists = folderLists;
    }


    public void setMainMusic(Set<SongModel> mainList) {
        this.mSongsMain = mainList;
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

    public Statistic getStatistic() {
        return mStatistic;
    }

    public void setStatistic(Statistic mStatistic) {
        this.mStatistic = mStatistic;
    }

    public RelationSongs getRelationSongs() {
        return mRelationSongs;
    }

    public void setRelationSongs(RelationSongs mRelationSongs) {
        this.mRelationSongs = mRelationSongs;
    }
}
