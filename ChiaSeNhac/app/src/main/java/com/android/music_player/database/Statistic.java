package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.android.music_player.utils.Constants;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Statistic {

    private Context mContext;
    /* renamed from: db */
    private String TAG = "StatisticLog";
    private ReaderSQL mDatabase;

    public Statistic(Context context){
        this.mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.STATISTIC.DATABASE_NAME, null, 1);
        mDatabase.queryData(Database.STATISTIC.CREATE_TABLE);
        mDatabase.close();
    }

    public Statistic closeDatabase() {
        this.mDatabase.close();
        return this;
    }

    public void addRow(String type ,String name){
        /**
         * type : playlist or music
         * row : 1 row in database
         * update số lần nghe
         * */
        String SQL_ADD = "INSERT INTO "+
                Database.STATISTIC.TABLE_NAME+
                " VALUES(" +
                " null, " +
                "'" + type    + "'"+ ","+
                "'" + name + "'"+ ","+
                "'" + 1       + "'"+ ","+
                "'" + 0       + "'"+ ")";
        mDatabase.queryData(SQL_ADD);
        closeDatabase();
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

    public int getNumber(String type,String name){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        try {
            if (isExistData(cursor)){
                do {
                    if (cursor.getString(1).equals(type) && cursor.getString(2).equals(name) ){
                        return cursor.getInt(3);
                    }
                }
                while (cursor.moveToNext());
            }
        }catch (SQLiteException e){

        }finally {
            closeDatabase();
        }
        return -2;
    }

    public ArrayList<String> getPlayListMost(){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        String first = "";
        String second = "";

        Map<Integer, List<String>> playListMost = new HashMap<>();
        ArrayList<String> most = new ArrayList<>();
        try {
            if (isExistData(cursor)){
                if (cursor.getString(1).equals(Constants.VALUE.MOST_PLAY_LIST)) {
                    List<Integer> sorted;
                    Log.d(TAG, cursor.getCount() + " kích thước");

                    do {
                        Log.d(TAG, "Enter0");
                        int counter = cursor.getInt(1);
                        String playlist = cursor.getString(2);
                        Log.d(TAG, playlist);
                        if (playListMost.get(counter) == null) {
                            playListMost.put(counter, new ArrayList<String>());
                        }

                        playListMost.get(counter).add(playlist);
                    } while (cursor.moveToNext());

                    sorted = new ArrayList<>(playListMost.keySet());
                    Log.d(TAG, "Enter1");
                    if (sorted.size() > 0) {
                        Collections.sort(sorted);

                        first = playListMost.get(sorted.get(0)).get(0);
                        Log.d(TAG, "first: " + first);
                        most.add(first);
                        if (playListMost.get(sorted.get(0)).size() > 1) {
                            second = playListMost.get(sorted.get(0)).get(1);
                            Log.d(TAG, "second: " + second);
                            most.add(second);
                        } else if (playListMost.size() > 1) {
                            second = playListMost.get(sorted.get(1)).get(0);
                            Log.d(TAG, "second: " + second);
                            most.add(second);
                        }
                        return most;

                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } catch (SQLiteException exception) {
            Log.d(TAG, exception.getMessage());
        } catch (CursorIndexOutOfBoundsException e){
            return most;
        }
        finally {
            closeDatabase();
        }
        return most;
    }

    public ArrayList<String> getAllMusicMost(){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        Map<Integer, String> allMusicMap = new HashMap<>();
        ArrayList<String> allMusic = new ArrayList<>();
        try {
            if (isExistData(cursor)){
                do {
                    if (cursor.getString(1).equals(Constants.VALUE.MOST_MUSIC)){
                        allMusicMap.put(cursor.getInt(3), cursor.getString(2));
                    }
                }while (cursor.moveToNext());
                Map<Integer, String> reverseSortedMap =
                        new TreeMap<>(Collections.reverseOrder());
                reverseSortedMap.putAll(allMusicMap);


            }
        }finally {
            closeDatabase();
        }
        allMusic = (ArrayList<String>) allMusicMap.values();
        return allMusic;
    }

    public String getMusicMost(String type){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        int max = 0;
        String name = "";
        try {
            if (isExistData(cursor)){
                do {
                    if (cursor.getString(1).equals(type)) {
                        if (cursor.getInt(3) >= max) {
                            max = cursor.getInt(3);
                            name = cursor.getString(2);
                        }
                    }
                }while (cursor.moveToNext());
                return name;
            }
        }catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }catch (CursorIndexOutOfBoundsException e){
            return "";
        }
        finally {
            closeDatabase();
        }
        return "";
    }

    public int getSize(){
        Cursor data = mDatabase.getData(Database.STATISTIC.QUERY);
        int count = 0;
        try {
            if (isExistData(data)) {
                count = data.getCount();
            }
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return count;
    }

    public void increase(String type, String name){
        if (!search(type ,name) ){
            Log.d(TAG, name +" --- search: true ");
            addRow(type ,name);
        }else {
            int most = (getNumber(type, name)) + 1;
            Log.d(TAG, most +" --- search: false ");
            String SQL_UPDATE =
                    "UPDATE "+Database.STATISTIC.TABLE_NAME +" SET " +
                            Database.STATISTIC.MOST +"="+""+ most +"" +
                        " WHERE "+Database.STATISTIC.NAME + " = "+"'"+name+"'";
            Log.d(TAG, SQL_UPDATE);
            mDatabase.queryData(SQL_UPDATE);
            closeDatabase();
        }
    }
    public void favorite(String name, int fav){
        String SQL_UPDATE = "UPDATE "+ Database.STATISTIC.TABLE_NAME+ " SET "+
                Database.STATISTIC.FAV + " = " + "'"+ fav +"'"
                + " WHERE "+ Database.STATISTIC.NAME + " = "+name;
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public boolean search(String type, String title){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        try {
            if (isExistData(cursor)){
                do  {
                    if (cursor.getString(1).equals(type)) {
                        if (cursor.getString(2).equals(title)) {
                            return true;
                        }
                    }
                }while (cursor.moveToNext());
            }
        }catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        } catch (CursorIndexOutOfBoundsException e) {
            return false;
        } finally {
            closeDatabase();
        }

        return false;
    }
    public void resetSong(String fileName){
        String SQL_UPDATE =
                "UPDATE "+Database.STATISTIC.TABLE_NAME +" SET " +
                        Database.STATISTIC.MOST +" = " +"'" + 0 +"'"+
                        " WHERE "+Database.STATISTIC.NAME + " = "+fileName;
        mDatabase.queryData(SQL_UPDATE);
    }

    public void resetAllSong(){
        try {
            mDatabase.queryData(Database.STATISTIC.DELETE);
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
    }
}
