package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Database;

import java.util.ArrayList;

public class SongOfPlayList {
    private AllPlaylist mAllPlayList;
    private Context mContext;
    /* renamed from: db */
    private String TAG = "SongOfPlayListLog";
    private ReaderSQL mDatabase;


    public SongOfPlayList(Context mContext){
        this.mContext = mContext;
        mAllPlayList= new AllPlaylist(mContext);
        mDatabase = new ReaderSQL(mContext, Database.SONGS_OF_PLAY_LIST.DATABASE_NAME, null, 1);
        mDatabase.queryData(Database.SONGS_OF_PLAY_LIST.CREATE_ENTRIES);
    }

    public SongOfPlayList closeDatabase() {
        this.mDatabase.close();
        return this;
    }

    public boolean isSelect(Cursor cursor){
        cursor = mDatabase.getData(Database.SONGS_OF_PLAY_LIST.QUERY);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                return true;
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
        return false;
    }

    public void addSong(SongModel song) {
        String SQL_INSERT = "INSERT INTO " + Database.SONGS_OF_PLAY_LIST.TABLE_NAME +
                " Value" +
                "(" + "null"                   + "," +
                "'" + song.getSongName() + "'" + "," +
                "'" + song.getPath() + "'"     + "," +
                "'" + song.getArtist() + "'"   + "," +
                "'" + song.getAlbum() + "'"    + "," +
                "'" + song.getAlbumID() + "'"  + "," +
                "'" + song.getFileName() + "'" + "," +
                "'" + song.get_ID() + "'"      + "," +
                "'" + 0 + "'"                  + "," +
                "'" + song.getTime() + "'"     + ")";
        mDatabase.queryData(SQL_INSERT);
        closeDatabase();
    }

    public void deleteSong(int id){
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.SONGS_OF_PLAY_LIST.TABLE_NAME+" WHERE id= '"+id+ "' ";
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }

    public boolean deleteSong(SongModel song){
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.SONGS_OF_PLAY_LIST.TABLE_NAME+" WHERE " +
                        "name_song= '"+song.getSongName()+ "' ";

        if (searchSong(song)){
            mDatabase.queryData(SQL_DELETE);
            Toast.makeText(mContext, "Xóa Thành Công Bài Hát: "+song.getSongName(), Toast.LENGTH_SHORT).show();
            closeDatabase();
            return true;
        }else {
            Toast.makeText(mContext, "Xóa Không thành công Bài Hát: "+song.getSongName(),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean isSongInPlayList(String namePlayList, String name_song){
        Cursor dataSongOfPlayList = mDatabase.getData(Database.SONGS_OF_PLAY_LIST.QUERY);

        try {
            if (mAllPlayList.searchPlayList(namePlayList)) {
                if (isSelect(dataSongOfPlayList)) {
                    while (dataSongOfPlayList.moveToNext()) {
                        if (dataSongOfPlayList.getString(1).equals(name_song)) {
                            return true;
                        }
                    }
                } else {
                    return false;
                }
            }
            return false;
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
        return false;
    }

    public boolean searchSong(SongModel song){
        Cursor songsData = mDatabase.getData(Database.SONGS_OF_PLAY_LIST.QUERY);
        try {
            if (isSelect(songsData) && getSize() != 0){
                while (songsData.moveToNext()){
                    if (songsData.getString(1).equals(song.getSongName())){
                        return true;
                    }
                }
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
        return false;
    }

    public void updateSong(String name,SongModel song){
        String SQL_UPDATE = "UPDATE "+ Database.SONGS_OF_PLAY_LIST.TABLE_NAME+ " SET "+
                Database.SONGS_OF_PLAY_LIST.NAME_SONG + "='"+ song.getSongName()+"'" + "," +
                Database.SONGS_OF_PLAY_LIST.PATH      + "='"+ song.getPath()+"'"     + "," +
                Database.SONGS_OF_PLAY_LIST.ARTIST    + "='"+ song.getArtist()+"'"   + "," +
                Database.SONGS_OF_PLAY_LIST.ALBUM     + "='"+ song.getAlbum()+"'"    + "," +
                Database.SONGS_OF_PLAY_LIST.ALBUM_ID  + "='"+ song.getAlbumID()+"'"  + "," +
                Database.SONGS_OF_PLAY_LIST.FILE_NAME + "='"+ song.getFileName()+"'" + "," +
                Database.SONGS_OF_PLAY_LIST.ID_SONG   + "='"+ song.get_ID()+"'"      + "," +
                Database.SONGS_OF_PLAY_LIST.TIME      + "='"+ song.getTime()+"'" +
                " WHERE " + "name_song= '"+ name +"'";
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public void favoriteSong(String name, int fav){
        String SQL_UPDATE = "UPDATE "+ Database.SONGS_OF_PLAY_LIST.TABLE_NAME+ " SET "+
                Database.SONGS_OF_PLAY_LIST.FAVORITE + " = " + "'"+ fav +"'"
                + " WHERE "+Database.SONGS_OF_PLAY_LIST.NAME_SONG+ " = "+name;
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }
    public ArrayList<SongModel> getAllSong() {
        Cursor data = mDatabase.getData(Database.SONGS_OF_PLAY_LIST.QUERY);

        ArrayList<SongModel> mSongs = new ArrayList<>();
        if (isSelect(data)){
            while (!data.isAfterLast()){

                SongModel.Builder builder = new SongModel.Builder();
                builder.setSongName(data.getString(1));
                builder.setPath(data.getString(2));
                builder.setArtist(data.getString(3));
                builder.setAlbum(data.getString(4));
                builder.setAlbumID(data.getString(5));
                builder.setFileName(data.getString(6));
                builder.setID(data.getString(7));
                builder.setTime(data.getInt(8));

                SongModel songModel = builder.generate();
                mSongs.add(songModel);
            }
        }
        closeDatabase();
        return mSongs;
    }

    public SongModel getSong(String nameSong) {
        Cursor data = mDatabase.getData(Database.SONGS_OF_PLAY_LIST.QUERY);
        SongModel songModel = null;
        try {
            if (isSelect(data)){
                while (data.moveToNext()){
                    if (data.getString(1).equals(nameSong)){
                        SongModel.Builder builder = new SongModel.Builder();
                        builder.setSongName(data.getString(1));
                        builder.setPath(data.getString(2));
                        builder.setArtist(data.getString(3));
                        builder.setAlbum(data.getString(4));
                        builder.setAlbumID(data.getString(5));
                        builder.setFileName(data.getString(6));
                        builder.setTime(data.getInt(7));

                        songModel = builder.generate();
                        return songModel;
                    }
                }
            }
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return null;
    }


    public int getSize() {
        Cursor songData = mDatabase.getData(Database.SONGS_OF_PLAY_LIST.QUERY);
        int count = 0;
        try {
            if (isSelect(songData)) {
                count = songData.getCount();
            }
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return count;
    }

    public void deleteAllSong() {
        try {
            mDatabase.queryData(Database.SONGS_OF_PLAY_LIST.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }
}
