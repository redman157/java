package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.android.music_player.utils.Constants;

import java.util.ArrayList;

public class CategoryMusic {

    private Context context;
    private String TAG = "CategorySongsLog";
    /* renamed from: db */
    private ReaderSQL mDatabase;


    public CategoryMusic(Context context){
        this.context = context;
        mDatabase = new ReaderSQL(context, Database.CATEGORY.DATABASE_NAME, null, 1 );
        mDatabase.queryData(Database.CATEGORY.CREATE_TABLE);
        mDatabase.close();
    }


    public CategoryMusic closeDatabase() {
        this.mDatabase.close();
        return this;
    }

    private String dropInvalidString(String str) {
        return str.replaceAll("[^A-Za-z0-9()\\[\\]]", "");
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

    // add Data
    public void addCategory(String type, String mediaId) {
        String SQL_INSERT = "INSERT INTO "+ Database.CATEGORY.TABLE_NAME+
                " VALUES(" +
                " null," +
                "'"+ mediaId +"'"  + "," +
                "'"+ type    +"'"  + "," +
                "'"+ 0       +"'"  + ")";

        mDatabase.queryData(SQL_INSERT);
        closeDatabase();
    }

    public void favorite(String name, int fav) {
        String SQL_UPDATE = "UPDATE " + Database.CATEGORY.TABLE_NAME + " SET " +
                Database.CATEGORY.VOTES + " = " + "'" + fav + "'"
                + " WHERE " + Database.CATEGORY.NAME + " = " + name;
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }


    public int isFavorite(String name){
        Cursor cursor = mDatabase.getData(Database.CATEGORY.QUERY);
        try {
            if (isExistData(cursor)){
                do  {
                    if (cursor.getString(3).equals(name)) {
                        Log.d("ZZZ", name);
                        return cursor.getInt(5);
                    }
                }while (cursor.moveToNext());
            }

        }catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        } catch (CursorIndexOutOfBoundsException e) {
            return -1;
        } finally {
            closeDatabase();
        }
        return -1;
    }

    // lấy hết toàn bộ data
    public ArrayList<String> getAllCategory() {
        Cursor data = mDatabase.getData(Database.CATEGORY.QUERY);

        ArrayList<String> mSongs = new ArrayList<>();
        try{
            if (isExistData(data)){
                do{
                    if(data.getString(3).equals(Constants.VALUE.MUSIC)){
                        if(data.getInt(4) == 1) {
                            mSongs.add(data.getString(2));
                        }
                    }
                }while(data.moveToNext());
            }
        }catch (CursorIndexOutOfBoundsException e){
            return null;
        }finally{
            closeDatabase();
        }
        return mSongs;
    }

    // xóa category theo id
    public void deleteCategory(String mediaId){
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.CATEGORY.TABLE_NAME+" WHERE name= '"+mediaId+ " ";
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }


    // xóa tất cả
    public void deleteAll(int id) {
        try {
            mDatabase.queryData(Database.CATEGORY.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }


    // kích thước data
    public int getSize() {
        Cursor songData = mDatabase.getData(Database.CATEGORY.QUERY);
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
}
