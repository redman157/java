package com.android.music_player.managers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MediaManager {
    private BrowserConnectionListener mBrowserConnectionListener;
    private String TAG = "MediaManagerLog";
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
    private static MediaManager instance;
    private static final int TITLE = 0;
    private static final int DISPLAY_NAME = 1;
    private static final int DATA = 2;
    private static final int ARTIST = 3;
    private static final int ALBUM = 4;
    private static final int ALBUM_ID = 5;
    private static final int DURATION = 6;
    private static final int ARTIST_ID = 7;

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


    public void setContext(Context context) {
        this.mContext = context;

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
        if (mSharedPrefsUtils.getInteger(Constants.PREFERENCES.TOTAL_SONGS, -1) == -1) {
            grabIfEmpty();
        }else if (MusicLibrary.model != null && MusicLibrary.model.size() > 0){
            if (mSharedPrefsUtils.getInteger(Constants.PREFERENCES.TOTAL_SONGS, -1) == MusicLibrary.model.size()){
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
        String nameSong = mSharedPrefsUtils.getString(Constants.PREFERENCES.CURRENT_MUSIC, "");
        if (MusicLibrary.music.size() > 0) {
            if (nameSong.equals("")) {
                ArrayList<String> keys = new ArrayList<>(MusicLibrary.music.keySet());
                Log.d("PPP", "getCurrentMusic size: " + keys.size());
                mQueueManager.setCurrentMediaMetadata(getMetadata(mContext, keys.get(0)));
                nameSong = keys.get(0);

            } else {
                mQueueManager.setCurrentMediaMetadata(getMetadata(mContext,
                        nameSong));
            }
        }
        Log.d("PPP", "getCurrentMusic: "+nameSong);
        return nameSong;
    }

    public void setCurrentMusic(String mediaId){
        mSharedPrefsUtils.setString(Constants.PREFERENCES.CURRENT_MUSIC, mediaId);
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
        }
        grabIfEmpty(); // isSync
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

    public void setFavorite(String titleName, int fav){
        mCategoryMusic.favorite(titleName, fav);
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
        try {
            if (mAllPlayList.getSize() == 0) {
                mAllPlayList.addRow("Play List 1");
                mAllPlayList.addRow("Play List 2");
            } else {
                Log.d(TAG, "MediaManager --- buildDataTheFirst mAllPlayList size > 0");
            }

            if (mStatistic.getSize() == 0) {
                Log.d(TAG, "MediaManager --- buildDataTheFirst mStatistic size = 0");
                mStatistic.addRow(Constants.VALUE.MOST_PLAY_LIST, "Play List 1");
                mStatistic.addRow(Constants.VALUE.MOST_PLAY_LIST, "Play List 2");
            } else {
                Log.d(TAG, "MediaManager --- buildDataTheFirst mStatistic size > 0");
            }

            if (!mAllMusic.search(songName)) {
                mAllMusic.addRow(songName);
                mCategoryMusic.addCategory(Constants.VALUE.MUSIC, songName);
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
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
                mCategoryMusic.deleteCategory(mediaID);
                Utils.ToastShort(mContext, "Đã xóa bài hát thành công: "+mediaID);
                return true;
            }else {
                Utils.ToastShort(mContext, "Xóa bài hát không thành công: "+mediaID);
                return false;
            }
        }else {
            if (mAllPlayList.search(namePlayList) && mAllMusic.search(mediaID)){
                mAllMusic.delete(mediaID);
                mCategoryMusic.deleteCategory(mediaID);
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

    private static final String[] BASE_PROJECTION = new String[]{
            MediaStore.Audio.AudioColumns.TITLE,// 0
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,// 1
            MediaStore.Audio.AudioColumns.DATA,// 2
            MediaStore.Audio.AudioColumns.ARTIST,// 3
            MediaStore.Audio.AudioColumns.ALBUM,// 4
            MediaStore.Audio.AudioColumns.ALBUM_ID,// 5
            MediaStore.Audio.AudioColumns.DURATION,// 6
            MediaStore.Audio.AudioColumns.ARTIST_ID,// 6
    };

    private Cursor makeSongCursor(@NonNull final Context context) {
        try {
            Uri uriMedia = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            return context.getContentResolver().query(uriMedia, BASE_PROJECTION, selection, null,
                    getSongLoaderSortOrder());
        } catch (SecurityException e) {
            return null;
        }
    }
    private static String getSongLoaderSortOrder() {
        return MediaStore.Audio.Artists.DEFAULT_SORT_ORDER + ", " +
                MediaStore.Audio.Albums.DEFAULT_SORT_ORDER + ", " +
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
    }
    @NonNull
    private static MusicModel getSongFromCursorImpl(@NonNull Cursor cursor) {
        final String title = cursor.getString(TITLE);
        final String displayName = cursor.getString(DISPLAY_NAME);
        final String path = cursor.getString(DATA);
        final String artistName = cursor.getString(ARTIST);
        final String albumName = cursor.getString(ALBUM);
        final String album_Id = cursor.getString(ALBUM_ID);
        final int duration = cursor.getInt(DURATION);
        final String artist_Id = cursor.getString(ARTIST_ID);
        Log.d("SSS", "album_Id: "+album_Id + " --- artist_Id: "+artist_Id);
        return new MusicModel(title, path,artistName,artist_Id,albumName, album_Id, displayName ,duration);
    }

    private void crawlData() {
        boolean excludeShortSounds = mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.EXCLUDE_SHORT_SOUNDS, false);
        boolean excludeWhatsApp = mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.EXCLUDE_WHATS_APP_SOUNDS, false);

        Cursor cursor = makeSongCursor(mContext);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                Log.d("SSEE", "crawl: " + cursor.getCount());
                do {
                    MusicModel.Builder builder = null;
                    String duration = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DURATION));

                    String apps =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                    int currentDuration = Math.round(Integer.parseInt(duration));
                    if ( getSongFromCursorImpl(cursor).getTime() >= 5000) {
                        Log.d("SSS", "crawlData: " + getSongFromCursorImpl(cursor).getSongName());
                        MusicLibrary.createMediaMetadataCompat(getSongFromCursorImpl(cursor));
                        buildDataTheFirst(getSongFromCursorImpl(cursor).getSongName());

                    }
                }
                while (cursor.moveToNext());
                mSharedPrefsUtils.setInteger(Constants.PREFERENCES.TOTAL_SONGS, MusicLibrary.music.size());
                Log.d(TAG, "CrawlData() performed");
                Log.d(TAG, "info: "+MusicLibrary.info.size());
                filterData();
            }
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
    }

    /*
     * Albums Data && Artist Data && folder Data
     */
    public void filterData(){
        Log.d(TAG, "allKeyInfoMusic: "+MusicLibrary.info.size());
        ArrayList<MusicModel> allKeyInfoMusic = new ArrayList<>(MusicLibrary.info);

        ArrayList<String> keys = new ArrayList<>(MusicLibrary.music.keySet());
        ArrayList<MediaMetadataCompat> values = new ArrayList<>();
        for (int i = 0; i < keys.size(); i++){
            values.add(MusicLibrary.music.get(keys.get(i)));
        }

        for (int index = 0; index < allKeyInfoMusic.size(); index++) {
        /*    String artist =
                    MusicLibrary.music.get(allKeyInfoMusic.get(index)).getString(Constants.METADATA.Artist);
            String album = MusicLibrary.music.get(allKeyInfoMusic.get(index)).getString(Constants.METADATA.Album);
            String folder = MusicLibrary.fileName.get(allKeyInfoMusic.get(index));*/
            String artist = values.get(index).getString(Constants.METADATA.Artist);
            String album = values.get(index).getString(Constants.METADATA.Album);
            String folder = MusicLibrary.fileName.get(keys.get(index));
            String splitFolder = folder.split("/")[folder.split("/").length - 4]+"/"+folder.split("/")[folder.split("/").length - 3]+"/"+folder.split("/")[folder.split("/").length - 2];
            while (true) {
                if (MusicLibrary.artist.get(artist) != null) {
                    MusicLibrary.artist.get(artist).add(keys.get(index));
                    break;
                } else {
                    MusicLibrary.artist.put(artist, new ArrayList<>());
                }
            }

            while (true) {
                if (MusicLibrary.album.get(album) != null) {
                    MusicLibrary.album.get(album).add(keys.get(index));
                    break;
                } else {
                    MusicLibrary.album.put(album, new ArrayList<String>());
                }
            }

            while (true) {
                if (MusicLibrary.folder.get(splitFolder) != null) {
                    MusicLibrary.folder.get(splitFolder).add(keys.get(index));
                    break;
                } else {
                    MusicLibrary.folder.put(splitFolder, new ArrayList<String>());
                }
            }
        }
    }

    public MediaMetadataCompat getMetadata(Context context, String songName) {
        if (!MusicLibrary.music.containsKey(songName)){
            return null;
        }else {
            MediaMetadataCompat metadataWithoutBitmap = MusicLibrary.music.get(songName);

            // Since MediaMetadataCompat is immutable, we need to create a copy to assignData the album art.
            // We don't assignData it initially on all items so that they don't take unnecessary memory.
            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
            for (String key : new String[]{
                    MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST,
                    MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                    MediaMetadataCompat.METADATA_KEY_ALBUM,
                    MediaMetadataCompat.METADATA_KEY_ARTIST,
                    MediaMetadataCompat.METADATA_KEY_GENRE,
                    MediaMetadataCompat.METADATA_KEY_TITLE}) {
                builder.putString(key, metadataWithoutBitmap.getString(key));
            }
            builder.putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION,
                    metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));

            if (MusicLibrary.albumID.get(songName) != null) {
                Bitmap albumArt = ImageHelper.getAlbumArt(context,
                        Long.valueOf(MusicLibrary.albumID.get(songName)));
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
            }
            return builder.build();
        }
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

    public Map<String, ArrayList<String>> getAlbum() {
        grabIfEmpty();
        return MusicLibrary.album;
    }

    public Map<String, ArrayList<String>> getArtist() {
        grabIfEmpty();
        return MusicLibrary.artist;
    }

    public Map<String, ArrayList<String>> getFolder() {
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
