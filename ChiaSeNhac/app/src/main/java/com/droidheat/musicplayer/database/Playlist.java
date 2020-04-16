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
public class Playlist {

    private Context context;
    private String TAG = "PLAYLISTLOG";
    /* renamed from: db */

    private ReaderSQL myDBHelper;

    private static Playlist instance;
    public static Playlist getInstance(){
        if (instance == null){
            instance = new Playlist();
        }
        return instance;
    }

    private Playlist() {

    }

    public void newRenderDB(Context context){
        this.context = context;
        this.myDBHelper = new ReaderSQL(context, Database.PLAYLIST.DATABASE_NAME, null, 1);
        myDBHelper.queryData(Database.PLAYLIST.SQL_CREATE_ENTRIES);
    }

    public Playlist close() {

        myDBHelper.close();

        return this;
    }

    public boolean isSelect(Cursor cursor){
        cursor = myDBHelper.getData(Database.PLAYLIST.QUERY);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                return true;
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }
        return false;
    }
    public void addPlayList( String title) {
        String SQL_INSERT =
                "INSERT INTO "+ Database.PLAYLIST.TABLE_NAME+" VALUES(null, '"+ title +"')";

        myDBHelper.queryData(SQL_INSERT);
        Toast.makeText(context, "Đã Add PlayList: "+title, Toast.LENGTH_SHORT).show();
    }

    public void deleteRow(int id) {
        String SQL_DELETE =
                "DROP TABLE IF EXISTS "+Database.PLAYLIST.TABLE_NAME+" WHERE id= '"+id+ "' ";
        myDBHelper.queryData(SQL_DELETE);
        Toast.makeText(context, "Đã Xóa Row PlayList: "+id, Toast.LENGTH_SHORT).show();
    }

    public void updatePlayList(int id, String title){
        String SQL_UPDATE =
                "UPDATE "+
                        Database.PLAYLIST.TABLE_NAME+ " SET " +
                        Database.PLAYLIST.TITLE+ "= '"+ title +"'" +
                        " WHERE "+ Database.PLAYLIST.ID+ "= '"+ id+ "' ";
        myDBHelper.queryData(SQL_UPDATE);
        Toast.makeText(context, "Đã Update PlayList: "+title, Toast.LENGTH_SHORT).show();
    }

    public boolean searchPlayList(String title){
        Cursor cursor = myDBHelper.getData(Database.PLAYLIST.QUERY);
        try {
            if (isSelect(cursor)) {
                while (cursor.moveToNext()) {
                    if (cursor.getString(1).equals(title)) {
                        return true;
                    }
                }
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }

        return false;
    }
    public ArrayList<HashMap<String, String>> getAllPlayList() {
        Cursor query = myDBHelper.getData(Database.PLAYLIST.QUERY);

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
        }
        return null;
    }

    public int getCount() {
        Cursor cursor = myDBHelper.getData(Database.PLAYLIST.SQL_QUERY_LIST);
        int count = 0;
        try {
            if (isSelect(cursor)) {
                count = cursor.getCount();
                cursor.close();
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }

        return count;
    }

    public HashMap<String, String> getPlayList(int id) {
        Cursor query = myDBHelper.getData(Database.PLAYLIST.QUERY);
        String name = "";
        try {
            if (isSelect(query)) {
                if (query.moveToFirst()) {
                    while (!query.isAfterLast()) {
                        if (query.getInt(0) == (id)) {
                            name = query.getString(1);
                            break;
                        }
                    }
                }
            }
            query.close();
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(Constants.VALUE.NAME_PLAYLIST, name);
            return hashMap;
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }

        return null;
    }

    public boolean deletePlayList(String title) {
        String SQL_DELETE =
                "DELETE FORM "+Database.PLAYLIST.TABLE_NAME+" WHERE title= '"+title+ "' ";

        if (searchPlayList(title)){
            myDBHelper.queryData(SQL_DELETE);
            Toast.makeText(context, "Xóa thành công PlayList: "+title, Toast.LENGTH_SHORT).show();
            return true;
        }else {
            Toast.makeText(context, "Xóa không thành công PlayList: "+title,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public boolean deleteAll() {
        try {
            Cursor cursor = myDBHelper.getData(Database.PLAYLIST.QUERY);
            if (cursor.moveToFirst()) {
                do {
                    deleteRow(cursor.getInt(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return true;
        } catch (SQLiteException e) {
           Log.d(TAG, e.getMessage());
        }
        return false;
    }

}
