package com.droidheat.musicplayer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.droidheat.musicplayer.Constants;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class AllPlaylist {

    private Context mContext;
    private String TAG = "AllPlaylistLog";
    /* renamed from: db */

    private ReaderSQL mAllPlayList;

    public AllPlaylist(Context context){
        this.mContext = context;
        this.mAllPlayList = new ReaderSQL(context, Database.ALL_PLAY_LISTS.DATABASE_NAME, null, 1);
        mAllPlayList.queryData(Database.ALL_PLAY_LISTS.SQL_CREATE_ENTRIES);
    }

    public AllPlaylist closeDatabase() {

        mAllPlayList.close();

        return this;
    }

    public boolean isSelect(Cursor cursor){
        cursor = mAllPlayList.getData(Database.ALL_PLAY_LISTS.QUERY);
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
                "INSERT INTO "+ Database.ALL_PLAY_LISTS.TABLE_NAME+" VALUES(null, '"+ title +"')";

        mAllPlayList.queryData(SQL_INSERT);
        closeDatabase();

        Toast.makeText(mContext, "Đã Add PlayList: "+title, Toast.LENGTH_SHORT).show();
    }

    public void deletePlayList(int id) {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_PLAY_LISTS.TABLE_NAME+" WHERE id= '"+id+ "' ";
        mAllPlayList.queryData(SQL_DELETE);
        closeDatabase();
    }

    public boolean deletePlayList(String name_play_list) {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+ Database.ALL_PLAY_LISTS.TABLE_NAME+" WHERE name_play_list= '"+name_play_list+ "' ";

        if (searchPlayList(name_play_list) && getSize()!=0){
            mAllPlayList.queryData(SQL_DELETE);
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
            mAllPlayList.queryData(Database.SONGS_OF_PLAY_LIST.DELETE);

        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            closeDatabase();
        }
    }

    public void updatePlayList(int id, String title){
        String SQL_UPDATE =
                "UPDATE "+
                        Database.ALL_PLAY_LISTS.TABLE_NAME+ " SET " +
                        Database.ALL_PLAY_LISTS.COL_NAME_PLAY_LIST + "= '"+ title +"'" +
                        " WHERE "+ Database.ALL_PLAY_LISTS.COL_ID + "= '"+ id+ "' ";
        mAllPlayList.queryData(SQL_UPDATE);
        Toast.makeText(mContext, "Đã Update PlayList: "+title, Toast.LENGTH_SHORT).show();
    }

    public boolean searchPlayList(String namePlayList){
        Cursor playListData = mAllPlayList.getData(Database.ALL_PLAY_LISTS.QUERY);
        try {
            if (isSelect(playListData) && getSize() != 0) {
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
    public ArrayList<HashMap<String, String>> getAllPlayList() {
        Cursor query = mAllPlayList.getData(Database.ALL_PLAY_LISTS.QUERY);

        try {
            ArrayList<HashMap<String, String>> allPlayList = new ArrayList<>();
            if (isSelect(query)) {
                do {
                    HashMap playlist = new HashMap();
                    playlist.put(Constants.VALUE.ID, query.getInt(0));
                    playlist.put(Constants.VALUE.NAME_PLAYLIST, query.getString(1));
                    allPlayList.add(playlist);
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
        Cursor cursor = mAllPlayList.getData(Database.ALL_PLAY_LISTS.QUERY);
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
        Cursor playListData = mAllPlayList.getData(Database.ALL_PLAY_LISTS.QUERY);
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
