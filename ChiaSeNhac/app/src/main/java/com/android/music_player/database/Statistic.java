package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.android.music_player.utils.Constants;
import com.android.music_player.utils.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addRow(String type,String row){
        String SQL_ADD = "INSERT INTO "+
                Database.STATISTIC.TABLE_NAME+
                " VALUES(" +
                " null, " +
                "'" + type+ "'"+ ","+
                "'" + row + "'"+ ","+
                ""  + 1  + ""  + ")";
        mDatabase.queryData(SQL_ADD);
        closeDatabase();
    }


    public boolean isSelect(Cursor cursor){
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
            if (isSelect(cursor)){
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
            if (isSelect(cursor)){
                if (cursor.getString(1).equals(Constants.VALUE.PLAY_LIST)) {
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
            return null;
        }
        finally {
            closeDatabase();
        }
        return null;
    }
    public String getMost(String type){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        int max = 0;
        String name = "";
        try {
            if (isSelect(cursor)){
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
            if (isSelect(data)) {
                count = data.getCount();
            }
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return count;
    }

    public boolean increase(String type, String name){
        if (!search(type ,name) ){
            Log.d(TAG, name +" --- search: true ");
            addRow(type ,name);
            return false;
        }else {
            int most = (getNumber(type, name)) + 1;
            Log.d(TAG, most +" --- search: false ");
            String SQL_UPDATE =
                    "UPDATE "+Database.STATISTIC.TABLE_NAME +" SET "+Database.STATISTIC.MOST
                    +" = "  +""+ most +""
                            +" WHERE "+Database.STATISTIC.TITLE + " = "+"'"+name+"'";
            Log.d(TAG, SQL_UPDATE);
            mDatabase.queryData(SQL_UPDATE);
            closeDatabase();
            return true;
        }
    }

    public boolean search(String type, String namePlayList){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        try {
            if (isSelect(cursor)){
                do  {
                    if (cursor.getString(1).equals(type)) {
                        if (cursor.getString(2).equals(namePlayList)) {
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
                        " WHERE "+Database.STATISTIC.TITLE + " = "+fileName;
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