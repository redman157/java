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
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.android.music_player.R;
import com.android.music_player.database.AllPlaylist;
import com.android.music_player.database.CategorySongs;
import com.android.music_player.database.RelationSongs;
import com.android.music_player.database.SongOfPlayList;
import com.android.music_player.database.Statistic;
import com.android.music_player.media.MediaBrowserConnection;
import com.android.music_player.models.SongModel;
import com.android.music_player.tasks.RenamePlayListTask;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MediaManager {
    private MediaBrowserConnection mMediaBrowserConnection;
    private ArrayList<MediaBrowserCompat.MediaItem> queue = new ArrayList<>();
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
    private static MediaManager instance;
    private String type;
    private String mediaId;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        Log.d("VVV", "setMediaId " + mediaId);
//        Log.d("VVV", Log.getStackTraceString(new Exception()));

        this.mediaId = mediaId;
    }

    public static MediaManager getInstance() {
        if (instance == null){
            instance = new MediaManager();
        }
        return instance;
    }

    private MediaManager() {

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
                    Type type = new TypeToken<ArrayList<MediaBrowserCompat.MediaItem>>() {
                    }.getType();
                    ArrayList<MediaBrowserCompat.MediaItem> restoreData = new Gson().fromJson(mSharedPrefsUtils.getString(Constants.PREFERENCES.KEY, null), type);
                    replaceQueue(restoreData);
                    Log.d(TAG, "Retrieved queue from storage in MediaManager. " + restoreData.size() + " mSongsMain!");
                } catch (Exception e) {
                    Log.d(TAG, "Unable to retrieve data while queue is empty.");
                    Log.d(TAG, e.getMessage());
                }
            }
        }else if (MusicLibrary.model != null && MusicLibrary.model.size() > 0){
            if (mTotalSong == MusicLibrary.model.size()){
                return;
            }else {
                crawlData();
            }
        }
    }
    public MediaBrowserConnection getMediaBrowserConnection() {
        if (mMediaBrowserConnection == null && mContext != null) {
            mMediaBrowserConnection = new MediaBrowserConnection(mContext);
        }

        return mMediaBrowserConnection;
    }

    public void setType(String type){
        mSharedPrefsUtils.setString(Constants.PREFERENCES.TYPE, type);
    }

    public String getType(){
        return mSharedPrefsUtils.getString(Constants.PREFERENCES.TYPE, "");
    }

    public String getCurrentMusic(){
        String nameSong = mSharedPrefsUtils.getString(Constants.PREFERENCES.CURRENT_MUSIC,"");
        Log.d("CCC","getCurrentMusic: " + nameSong);
        return nameSong;
    }

    public void setCurrentMusic(String path){
        Log.d("CCC","setCurrentSong: " + path);
        // convert path --> music name
        String musicName = Utils.getKeyByValue(MusicLibrary.fileName, path);
        mSharedPrefsUtils.setString(Constants.PREFERENCES.CURRENT_MUSIC, musicName);
    }

    public List<MediaBrowserCompat.MediaItem> queue() {
        if (queue.isEmpty()){
            List<MediaBrowserCompat.MediaItem> list =
                    new ArrayList<>(MusicLibrary.getMediaItems());
            Collections.reverse(list);
            replaceQueue(list);
        }
        return queue;
    }

    public ArrayList<MediaBrowserCompat.MediaItem> allSortSongs() {
        grabIfEmpty(); // If no song in list
        // Sorted list of 0-9 A-Z
        ArrayList<MediaBrowserCompat.MediaItem> songs = new ArrayList<>(newSongs());
        Collections.sort(songs, new Comparator<MediaBrowserCompat.MediaItem>() {
            @Override
            public int compare(MediaBrowserCompat.MediaItem mediaItem1,
                               MediaBrowserCompat.MediaItem mediaItem2) {
                return mediaItem1.getMediaId().compareTo(mediaItem2.getMediaId());
            }
        });
        return songs;
    }

    public ArrayList<MediaBrowserCompat.MediaItem> newSongs() {
        grabIfEmpty(); // If no song in list (new songs)

        ArrayList<MediaBrowserCompat.MediaItem> newSongs = new ArrayList<>(MusicLibrary.getMediaItems());
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
        ArrayList<SongModel> list = new ArrayList<>(MusicLibrary.info);
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
        ArrayList<SongModel> list = new ArrayList<>(MusicLibrary.info);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getArtist().contains(artist)) {
                songs.add(list.get(i));
            }
        }
        return songs;
    }
    /**
    * Database
    * */
    public void addDatabaseMusic(String path){
        setCurrentMusic(path);
        getStatistic().increase(
                Constants.VALUE.MOST_SONG, Utils.getKeyByValue(MusicLibrary.fileName,
                        path));
    }

    public void addDatabasePlayList(String namePlayList, String songName){
        // add play list
        getAllPlaylistDB().addPlayList(namePlayList);
        if (songName != null) {
            getRelationSongs().addRow(namePlayList, songName);
        }
    }




    /*
     * Actions
     */

    public SongModel getSong(String title){
        for (SongModel model: MusicLibrary.info) {
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
            MusicLibrary.clear();
            queue.clear();
        }
        grabIfEmpty(); // isSync
    }


    public  boolean replaceQueue(final List<MediaBrowserCompat.MediaItem> list) {
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
        if (!mStatistic.getMusicMost(Constants.VALUE.MOST_PLAY_LIST).equals("")){

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

                Log.d(TAG, "MediaManager --- getAllSongToPlayList: error getAllSongToPlayList");
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
            Log.d(TAG, "MediaManager --- addPlayListFirst size > 0");
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
        mStatistic.increase(Constants.VALUE.MOST_SONG,songModel.getSongName());

    }
    public void increase(String title){
        mStatistic.increase(Constants.VALUE.MOST_PLAY_LIST,title);

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
                        "Xóa bài hát trong NAME: "+namePlayList+" thành công: "+song.getSongName());
                return true;
            }else {
                Utils.ToastShort(mContext,
                        "Xóa bài hát trong NAME: "+namePlayList+" không thành công: "+song.getSongName());
                return false;
            }
        }
    }

    public String renamePlayList(String main, String change){
        String temp = "";
        if (main.equals("")){
            Utils.ToastShort(mContext, "NAME không thể tìm trống");
        }else if (change.equals("")){
            Utils.ToastShort(mContext, "Vui lòng nhập tên cần thay đổi");
        }else if (main.equals(change)){
            Utils.ToastShort(mContext, "Vui lòng nhập tên không được trùng với NAME " +
                    "ban đầu");
        }else if (!main.equals("") && !change.equals("")) {
            if (mAllPlaylist.searchPlayList(main)) {
                try {
                    temp = new RenamePlayListTask(mContext, main, change).execute().get();
                    Utils.ToastShort(mContext, "Đã đổi tên NAME thành công ");
                    return temp;
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }else {
                Utils.ToastShort(mContext, "Không tìm thấy NAME để đổi");
            }
        }
        return temp;
    }

    public void changeMusic(String namePlayList, SongModel song){
        if (song == null){

        }else {
            if (mSongOfPlayList.searchSong(song)){

            }else {
                Utils.ToastShort(mContext,"Không tìm thấy NAME: "+namePlayList);
            }
        }
    }

    private void grabIfEmpty() {
        if (MusicLibrary.info.isEmpty()) {
            crawlData();
            Log.d(TAG, "Grabbing data for player...");
        } else {
            Log.d(TAG, "Data is present. Just setting context.");
        }
    }

    private void crawlData() {
        try {
            String[] mediaProjection = {"*"};
            String[] genresProjection = {
                    MediaStore.Audio.Genres.NAME,
                    MediaStore.Audio.Genres._ID
            };
            boolean excludeShortSounds = mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.EXCLUDE_SHORT_SOUNDS, false);
            boolean excludeWhatsApp = mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.EXCLUDE_WHATS_APP_SOUNDS, false);

            Uri uriMedia = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            Cursor cursor =
                    mContext.getContentResolver()
                    .query(uriMedia, mediaProjection, selection, null, null);
            Log.d(TAG, "Uri: " + uriMedia.getPath() + " ==== Selection: " + selection + " ====== Cursor: " + cursor.getCount());


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

                            MusicLibrary.createMediaMetadataCompat(builder.generate());
                        }
                    }

                }
                while (cursor.moveToNext());
                mSharedPrefsUtils.setInteger(Constants.PREFERENCES.TOTAL_SONGS, MusicLibrary.music.size());
                cursor.close();
            }

            filterData(MusicLibrary.info);
            Log.d(TAG, "CrawlData() performed");
        }catch (SQLiteException e){
        }
    }

    /*
     * Albums Data && Artist Data && folder Data
     */
    private void filterData(Set<SongModel> mains){

        ArrayList<SongModel> allSongList = new ArrayList<>(MusicLibrary.info);

        for (int song = 0; song < allSongList.size(); song++) {
            String artist = allSongList.get(song).getArtist();
            String album = allSongList.get(song).getAlbum();
            String folder = allSongList.get(song).getPath();

            while (true) {
                if (MusicLibrary.artist.get(artist) != null) {
                    MusicLibrary.artist.get(artist).add(allSongList.get(song));
                    break;
                } else {
                    MusicLibrary.artist.put(artist, new ArrayList<SongModel>());
                }
            }

            while (true) {
                if (MusicLibrary.album.get(album) != null) {
                    MusicLibrary.album.get(album).add(allSongList.get(song));
                    break;
                } else {
                    MusicLibrary.album.put(album, new ArrayList<SongModel>());
                }
            }

            while (true) {
                if (MusicLibrary.folder.get(folder) != null) {
                    MusicLibrary.folder.get(folder).add(allSongList.get(song));
                    break;
                } else {
                    MusicLibrary.folder.put(folder, new ArrayList<SongModel>());
                }
            }
        }
    }

    public Map<String, ArrayList<SongModel>> getAlbum() {
        grabIfEmpty();
        return MusicLibrary.album;
    }

    public Map<String, ArrayList<SongModel>> getArtist() {
        grabIfEmpty();
        return MusicLibrary.artist;
    }

    public Map<String, ArrayList<SongModel>> getFolder() {
        grabIfEmpty();
        return MusicLibrary.folder;
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
