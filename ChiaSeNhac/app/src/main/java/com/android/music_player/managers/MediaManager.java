package com.android.music_player.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.android.music_player.database.AllMusic;
import com.android.music_player.database.AllPlayList;
import com.android.music_player.database.CategoryMusic;
import com.android.music_player.database.MusicOfPlayList;
import com.android.music_player.database.Statistic;
import com.android.music_player.media.BrowserConnectionListener;
import com.android.music_player.models.MusicModel;
import com.android.music_player.tasks.RenamePlayListTask;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MediaManager {
    private BrowserConnectionListener mBrowserConnectionListener;
    private ArrayList<MediaBrowserCompat.MediaItem> queue = new ArrayList<>();
    private ArrayList<MusicModel> shuffleSongs = new ArrayList<>();
    private String TAG = "SongsManagerConsole";
    /* access modifiers changed from: private */
    private Context mContext;
    /* access modifiers changed from: private */
    private SharedPrefsUtils mSharedPrefsUtils;
    private int mTotalSong;
    private AllPlayList mAllPlayList;
    private AllMusic mAllMusic;
    private CategoryMusic mCategoryMusic;
    private Statistic mStatistic;
    private MusicOfPlayList mMusicOfPlayList;
    private QueueManager mQueueManager;
    @SuppressLint("StaticFieldLeak")
    private String albumChoose;
    private static MediaManager instance;
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
        mAllPlayList = new AllPlayList(mContext);
        mAllMusic = new AllMusic(mContext);
        mCategoryMusic = new CategoryMusic(mContext);
        mStatistic = new Statistic(mContext);
        mMusicOfPlayList = new MusicOfPlayList(mContext);
        mSharedPrefsUtils = new SharedPrefsUtils(mContext);
//        mQueueManager = QueueManager.getInstance(mContext);
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
    public BrowserConnectionListener getMediaBrowserConnection() {
        if (mBrowserConnectionListener == null && mContext != null) {
            mBrowserConnectionListener = new BrowserConnectionListener(mContext);
        }
        return mBrowserConnectionListener;
    }

    public void setChooseAlbum(String nameAlbum){
        mSharedPrefsUtils.setString(Constants.PREFERENCES.IS_ROOT, nameAlbum);
    }

    public String getType(){
        return mSharedPrefsUtils.getString(Constants.PREFERENCES.IS_ROOT, "");
    }

    public String getCurrentMusic(){
        mQueueManager = QueueManager.getInstance(mContext);
        String nameSong =
                mQueueManager.getCurrentMediaMetadata() == null ?
                        MusicLibrary.music.get(MusicLibrary.music.keySet().toArray()[0]).getString(Constants.METADATA.Title):
                        mQueueManager.getCurrentMediaMetadata().getString(Constants.METADATA.Title);
        return nameSong;
    }

    public List<String> getAlbumIds(String rawAlbumIds) {
        String SPLIT_EXPRESSION = ";,,;,;;";
        List<String> list = new ArrayList<>();
        String[] albumIDs = rawAlbumIds.split(SPLIT_EXPRESSION);
        Collections.addAll(list, albumIDs);
        return list;
    }


    public ArrayList<MusicModel> artistSongs(String artist) {
        ArrayList<MusicModel> songs = new ArrayList<>();
        ArrayList<MusicModel> list = new ArrayList<>(MusicLibrary.info);
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
    public void addDatabaseMusic(String mediaID){
        if (!mStatistic.search(Constants.VALUE.MOST_MUSIC, mediaID)) {
            getStatistic().addRow(
                    Constants.VALUE.MOST_MUSIC, Utils.getKeyByValue(MusicLibrary.fileName,
                            mediaID));
        }
    }

    public void addDatabasePlayList(String namePlayList, String mediaID){
        // add play list
        getAllPlaylistDB().addRow(namePlayList);
        if (mediaID != null) {
            getRelationSongs().addRow(namePlayList, mediaID);
        }
    }

    /*
     * Actions
     */

    public MusicModel getSong(String title){
        for (MusicModel model: MusicLibrary.info) {
            if (model.getSongName().equals(title)){
                return model;
            }
        }
        return null;
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

    /*
     * Helper Functions. Keep them private or public as required
     */

    private void clearQueue() {
        queue.clear();
    }


    /**
     * Database MusicOfPlayList
     * */
    private boolean existData(String titlePlayList, String titleMusic){
        if (mMusicOfPlayList.searchMusicInPlayList(titlePlayList, titleMusic)){

            return true;
        }else {
//            mMusicOfPlayList.addRow(titlePlayList, mAllMusic.getMusicId(titleMusic));

            return false;
        }
    }

    private boolean existPlayList(String titlePlayList){
        if (mMusicOfPlayList.searchMusicInPlayList(titlePlayList) && mAllPlayList.search(titlePlayList)){
            return true;
        }else {
            return false;
        }
    }

    public boolean addMusicToPlayList(String titlePlayList, String titleMusic){
        if (existData(titlePlayList, titleMusic)){
            return true;
        }else{
            mMusicOfPlayList.addRow(titlePlayList, mAllMusic.getMusicId(titleMusic));
            return false;
        }
    }

    public boolean updateMusicOfPlayList(String titlePlayList, String titleMusic){
        if (existData(titlePlayList, titleMusic)){
            mMusicOfPlayList.updateSongs(titlePlayList, titleMusic);
            return true;
        }else {
            return false;
        }
    }

    public ArrayList<String> getAllMusicOfPlayList(String titlePlayList){
        if (existPlayList(titlePlayList)){
            ArrayList<String> allMusic = mMusicOfPlayList.getAllMusicInPlayList(titlePlayList);
            if (allMusic.size() > 0){
                return allMusic;
            }
        }
        return null;
    }

    /**
     * Database Statistic
     * */
    public void increase(String type,String title){
        mStatistic.increase(type, title);
    }

    public ArrayList<String> getListMost(String type){
        if (type.equals(Constants.VALUE.MOST_PLAY_LIST)) {
                return mStatistic.getPlayListMost();
        }else {
            return mStatistic.getAllMusicMost();
        }
    }

    /**
     * Database AllPlayList
     * */

  /*  public ArrayList<MusicModel> getPlayList(String playListName){
        ArrayList<MusicModel> songs = new ArrayList<>();
        if (mAllPlayList.search(playListName)){
            ArrayList<String> allSongName = mMusicOfPlayList.getAllMusicInPlayList(playListName);
            if (allSongName != null && allSongName.size() > 0 ){
                for (String songName : allSongName){
                    songs.add(getSong(songName));
                }
                return songs;
            }else {

                Log.d(TAG, "MediaManager --- getAllMusicInPlayList: error getAllMusicInPlayList");
                return null;

            }
        }else {
            return null;
        }
    }*/

    public void buildDataTheFirst(String songName){
        if (mAllPlayList.getSize() == 0) {
            mAllPlayList.addRow("Play List 1");
            mAllPlayList.addRow("Play List 2");

        }else {
            Log.d(TAG, "MediaManager --- buildDataTheFirst mAllPlayList size > 0");
        }

        if (mStatistic.getSize() == 0){
            mStatistic.addRow(Constants.VALUE.MOST_PLAY_LIST, "Play List 1");
            mStatistic.addRow(Constants.VALUE.MOST_PLAY_LIST, "Play List 2");
        }else {
            Log.d(TAG, "MediaManager --- buildDataTheFirst mStatistic size > 0");
        }

        if (!mAllMusic.search(songName)){
            mAllMusic.addRow(songName);
        }
    }

    public boolean addPlayList(String namePlayList){
        if (mAllPlayList.search(namePlayList)){
            return false;
        }else {
            mAllPlayList.addRow(namePlayList);
            return true;
        }
    }

    public ArrayList<String> getAllPlayList(){
        return mAllPlayList.getAllPlayList();
    }

    public boolean delete(String namePlayList, String mediaID){
        if (namePlayList.equals("")){
            if (mAllMusic.search(mediaID)){
                mAllMusic.delete(mediaID);
                Utils.ToastShort(mContext, "Đã xóa bài hát thành công: "+mediaID);
                return true;
            }else {
                Utils.ToastShort(mContext, "Xóa bài hát không thành công: "+mediaID);
                return false;
            }
        }else {
            if (mAllPlayList.search(namePlayList) && mAllMusic.search(mediaID)){
                mAllMusic.delete(mediaID);
                Utils.ToastShort(mContext,
                        "Xóa bài hát trong NAME: "+namePlayList+" thành công: "+mediaID);
                return true;
            }else {
                Utils.ToastShort(mContext,
                        "Xóa bài hát trong NAME: "+namePlayList+" không thành công: "+mediaID);
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
            if (mAllPlayList.search(main)) {
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

    public void changeMusic(String namePlayList, String mediaID){
        if (mediaID == null){
            return;
        }else {
            if (mAllMusic.search(mediaID)){

            }else {
                Utils.ToastShort(mContext,"Không tìm thấy NAME: "+namePlayList);
            }
        }
    }
    /**
     * AllMusic
     * */

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
                    MusicModel.Builder builder = null;
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


                            String fileName = cursor
                                    .getString(
                                            cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                                    .replace("_", " ").trim().replaceAll(" +", " ");
                            String path = cursor.getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DATA));
                            String songName = cursor.getString(cursor
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
                            builder = new MusicModel.Builder();
                            builder.setFileName(fileName);
                            builder.setSongName(songName);
                            builder.setArtist(artistName);
                            builder.setAlbum(albumName);
                            builder.setAlbumID(albumID);
                            builder.setGenres(genres);
                            builder.setPath(path);
                            builder.setTime(currentDuration);

                            MusicLibrary.createMediaMetadataCompat(builder.generate());

                            buildDataTheFirst(songName);

                        }
                    }

                }
                while (cursor.moveToNext());
                mSharedPrefsUtils.setInteger(Constants.PREFERENCES.TOTAL_SONGS, MusicLibrary.music.size());
                cursor.close();
            }
            filterData();
            Log.d(TAG, "CrawlData() performed");
        }catch (SQLiteException e){
        }
    }

    /*
     * Albums Data && Artist Data && folder Data
     */
    private void filterData(){

        ArrayList<MusicModel> allKeyInfoMusic = new ArrayList<>(MusicLibrary.info);

        for (int index = 0; index < allKeyInfoMusic.size(); index++) {
        /*    String artist =
                    MusicLibrary.music.get(allKeyInfoMusic.get(index)).getString(Constants.METADATA.Artist);
            String album = MusicLibrary.music.get(allKeyInfoMusic.get(index)).getString(Constants.METADATA.Album);
            String folder = MusicLibrary.fileName.get(allKeyInfoMusic.get(index));*/
            String artist = allKeyInfoMusic.get(index).getArtist();
            String album = allKeyInfoMusic.get(index).getAlbum();
            String folder = allKeyInfoMusic.get(index).getFileName();
            while (true) {
                if (MusicLibrary.artist.get(artist) != null) {
                    MusicLibrary.artist.get(artist).add(allKeyInfoMusic.get(index));
                    break;
                } else {
                    MusicLibrary.artist.put(artist, new ArrayList<MusicModel>());
                }
            }

            while (true) {
                if (MusicLibrary.album.get(album) != null) {
                    MusicLibrary.album.get(album).add(allKeyInfoMusic.get(index));
                    break;
                } else {
                    MusicLibrary.album.put(album, new ArrayList<MusicModel>());
                }
            }

            while (true) {
                if (MusicLibrary.folder.get(folder) != null) {
                    MusicLibrary.folder.get(folder).add(allKeyInfoMusic.get(index));
                    break;
                } else {
                    MusicLibrary.folder.put(folder, new ArrayList<MusicModel>());
                }
            }
        }
    }

    public MediaMetadataCompat getMetadata(Context context, String songName) {
        MediaMetadataCompat metadataWithoutBitmap = MusicLibrary.music.get(songName);
        Bitmap albumArt = ImageHelper.getAlbumArt(context,
                Long.valueOf(MusicLibrary.albumID.get(songName)));

        // Since MediaMetadataCompat is immutable, we need to create a copy to assignData the album art.
        // We don't assignData it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    public List<MediaSessionCompat.QueueItem> getShuffleQueue(MediaMetadataCompat currentMedia,
                                                                     List<MediaSessionCompat.QueueItem> playList) {
        List<MediaSessionCompat.QueueItem> result = new ArrayList<>();
        for (MediaSessionCompat.QueueItem tracks : playList){
            result.add(tracks);
        }
        Collections.shuffle(result);
        MediaSessionCompat.QueueItem mCurrentQueue =
                new MediaSessionCompat.QueueItem(currentMedia.getDescription(),
                        currentMedia.getDescription().hashCode());
        result.remove(currentMedia);
        result.add(0, mCurrentQueue);

        return result;
    }

    public Map<String, ArrayList<MusicModel>> getAlbum() {
        grabIfEmpty();
        return MusicLibrary.album;
    }

    public Map<String, ArrayList<MusicModel>> getArtist() {
        grabIfEmpty();
        return MusicLibrary.artist;
    }

    public Map<String, ArrayList<MusicModel>> getFolder() {
        grabIfEmpty();
        return MusicLibrary.folder;
    }


    public AllPlayList getAllPlaylistDB() {
        return mAllPlayList;
    }

    public void setAllPlaylistDB(AllPlayList allPlayListDB) {
        this.mAllPlayList = allPlayListDB;
    }

    public AllMusic getMusicOfPlayListDB() {
        return mAllMusic;
    }

    public void setSongOfPlayListDB(AllMusic allMusicDB) {
        this.mAllMusic = allMusicDB;
    }

    public CategoryMusic getCategorySongsDB() {
        return mCategoryMusic;
    }

    public void setCategorySongsDB(CategoryMusic categoryMusicDB) {
        this.mCategoryMusic = categoryMusicDB;
    }

    public Statistic getStatistic() {
        return mStatistic;
    }

    public void setStatistic(Statistic mStatistic) {
        this.mStatistic = mStatistic;
    }

    public MusicOfPlayList getRelationSongs() {
        return mMusicOfPlayList;
    }

    public void setRelationSongs(MusicOfPlayList mMusicOfPlayList) {
        this.mMusicOfPlayList = mMusicOfPlayList;
    }
}
