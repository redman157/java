package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

public class AllMusic {

    private Context mContext;
    /* renamed from: db */
    private String TAG = "SongOfPlayListLog";
    private ReaderSQL mDatabase;


    public AllMusic(Context mContext){
        this.mContext = mContext;
        mDatabase = new ReaderSQL(mContext, Database.ALL_MUSIC.DATABASE_NAME, null, 1);
        mDatabase.queryData(Database.ALL_MUSIC.CREATE_TABLE);
        mDatabase.close();
    }

    public AllMusic closeDatabase() {
        this.mDatabase.close();
        return this;
    }

    public boolean isExistData(Cursor cursor){
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

    public void addRow(String mediaID) {
        String SQL_INSERT = "INSERT INTO " + Database.ALL_MUSIC.TABLE_NAME +
                "VALUES" +
                "(" + "null"         + "," +
                "'" + mediaID+ "'"   + ")";
        mDatabase.queryData(SQL_INSERT);
        closeDatabase();
    }

    public void delete(int id){
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_MUSIC.TABLE_NAME+" WHERE id= '"+id+ "' ";
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }

    public void deletePlayList(String namePlayList){
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_MUSIC.TABLE_NAME+" WHERE " +
                        "name_play_list= '"+namePlayList+ "' ";
    }

    public boolean delete(String mediaID){
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_MUSIC.TABLE_NAME+" WHERE " +
                        Database.ALL_MUSIC.NAME +"= '"+mediaID+ "' ";

        if (search(mediaID) && getSize() > 0){
            mDatabase.queryData(SQL_DELETE);
            Toast.makeText(mContext, "Xóa Thành Công Bài Hát: "+mediaID, Toast.LENGTH_SHORT).show();
            closeDatabase();
            return true;
        }else {
            Toast.makeText(mContext, "Xóa Không thành công Bài Hát: "+mediaID,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean search(String mediaID){
        Cursor data = mDatabase.getData(Database.ALL_MUSIC.QUERY);
        try {
            if (isExistData(data)){
                do {
                    if (data.getString(1).equals(mediaID)){
                        return true;
                    }
                }
                while (data.moveToNext());
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }catch (CursorIndexOutOfBoundsException e) {
            return false;
        }finally {
            closeDatabase();
        }
        return false;
    }

    public void update(String name, String mediaID){
        String SQL_UPDATE = "UPDATE "+ Database.ALL_MUSIC.TABLE_NAME+ " SET "+
                Database.ALL_MUSIC.NAME + " = " + "'"+ mediaID+"'" + "," +
                " WHERE " + "name_song = '"+ name +"'";
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public String getMusicId(String mediaId){
        Cursor cursor = mDatabase.getData(Database.ALL_MUSIC.QUERY);
        try {
            if (isExistData(cursor)) {
                do {
                    if (cursor.getString(1).equals(mediaId)) {
                        return cursor.getString(1);
                    }
                }
                while (cursor.moveToNext());
            }
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return "";
    }

    public int getSize() {
        Cursor songData = mDatabase.getData(Database.ALL_MUSIC.QUERY);
        int count = 0;
        try {
            if (isExistData(songData)) {
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
            mDatabase.queryData(Database.ALL_MUSIC.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }
}
