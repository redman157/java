package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.android.music_player.utils.Constants;
import com.android.music_player.utils.Database;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class AllPlaylist {

    private Context mContext;
    private String TAG = "AllPlaylistLog";
    /* renamed from: db */

    private ReaderSQL mDatabase;

    public AllPlaylist(Context context){
        this.mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.ALL_PLAY_LISTS.DATABASE_NAME, null, 1);
        mDatabase.queryData(Database.ALL_PLAY_LISTS.CREATE_ENTRIES);
    }

    public AllPlaylist closeDatabase() {

        mDatabase.close();

        return this;
    }

    public boolean isSelect(Cursor cursor){
        cursor = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        try {
            if (cursor != null) {
                cursor.moveToFirst();

                return true;
            }

        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            cursor.close();
        }

        return false;
    }

    public void addPlayList(String title) {

        String SQL_INSERT =
                "INSERT INTO "+ Database.ALL_PLAY_LISTS.TABLE_NAME
                        +" VALUES(null, '"+ title +"')";

        mDatabase.queryData(SQL_INSERT);
        closeDatabase();
        Toast.makeText(mContext, "Đã Add PlayList: "+title, Toast.LENGTH_SHORT).show();
    }

    public void deletePlayList(int id) {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_PLAY_LISTS.TABLE_NAME+" WHERE id= '"+id+ "' ";
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }

    public boolean deletePlayList(String name_play_list) {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_PLAY_LISTS.TABLE_NAME+" WHERE name_play_list= '"+name_play_list+ "' ";

        if (searchPlayList(name_play_list) && getSize() > 0){
            mDatabase.queryData(SQL_DELETE);
            Toast.makeText(mContext, "Xóa thành công PlayList: "+name_play_list, Toast.LENGTH_SHORT).show();
            closeDatabase();
            return true;
        }else {
            Toast.makeText(mContext, "Xóa không thành công PlayList: "+name_play_list,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void deleteAllPlayList() {
        try {
            mDatabase.queryData(Database.SONGS_OF_PLAY_LIST.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }

    public void updatePlayList(String main,String change){
        String SQL_UPDATE =
                "UPDATE "+
                        Database.ALL_PLAY_LISTS.TABLE_NAME+ " SET " +
                        Database.ALL_PLAY_LISTS.NAME_PLAY_LIST + "= '"+ change +"'" +
                        " WHERE "+ Database.ALL_PLAY_LISTS.NAME_PLAY_LIST + "= '"+ main+ "' ";
        mDatabase.queryData(SQL_UPDATE);

        Toast.makeText(mContext, "Đã Update PlayList: "+change, Toast.LENGTH_SHORT).show();
    }

    public boolean searchPlayList(String namePlayList){
        Cursor playListData = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        try {
            if (isSelect(playListData) && getSize() > 0) {
                while (playListData.moveToNext()) {
                    if (playListData.getString(1).equals(namePlayList)) {
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


    public ArrayList<String> getAllPlayList() {
        Cursor query = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);

        try {
            ArrayList<String> allPlayList = new ArrayList<>();
            if (isSelect(query)) {
                do {
                    allPlayList.add(query.getString(1));
                } while (query.moveToNext());
            }
            query.close();
            return allPlayList;
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
        return null;
    }

    public int getSize() {
        Cursor cursor = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        int count = 0;
        try {
            if (isSelect(cursor)) {
                count = cursor.getCount();
            }
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return count;
    }

    public HashMap<String, String> getPlayList(String title) {
        Cursor playListData = mDatabase.getData(Database.ALL_PLAY_LISTS.QUERY);
        String name = "";
        try {
            if (isSelect(playListData)) {
                if (playListData.moveToFirst()) {
                    while (!playListData.isAfterLast()) {
                        if (playListData.getString(1).equals (title)) {
                            name = playListData.getString(1);
                            break;
                        }
                    }
                }
            }
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.VALUE.NAME_PLAYLIST, name);
            return hashMap;
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }

        return null;
    }
}
