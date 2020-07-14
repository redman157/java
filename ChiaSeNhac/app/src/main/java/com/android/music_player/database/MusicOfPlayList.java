package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicOfPlayList {
    private Context mContext;
    private String TAG = "RelationMusicLog";
    /* renamed from: db */
    private AllMusic mAllMusic;
    private ReaderSQL mDatabase;

    public MusicOfPlayList(Context context){
        mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.MUSIC_OF_PLAY_LIST.DATABASE_NAME, null, 1);
        mAllMusic = new AllMusic(context);
        mDatabase.queryData(Database.MUSIC_OF_PLAY_LIST.CREATE_TABLE);
        mDatabase.close();
    }

    public MusicOfPlayList closeDatabase(){
        mDatabase.close();
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

    public ArrayList<String> getPlayListMost(){
        Cursor cursor = mDatabase.getData(Database.MUSIC_OF_PLAY_LIST.QUERY);
        String first = "";
        String second = "";

        Map<Integer, List<String>> playListMost = new HashMap<>();
        ArrayList<String> most = new ArrayList<>();
        try {
            if (isExistData(cursor)){
                List<Integer> sorted;

                do {
                    int counter = cursor.getInt(1);
                    String playlist = cursor.getString(2);
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
                    Log.d(TAG, "first: "+first);
                    most.add(first);
                    if (playListMost.get(sorted.get(0)).size() > 1) {
                        second = playListMost.get(sorted.get(0)).get(1);
                        Log.d(TAG, "second: "+second);
                        most.add(second);
                    } else if (playListMost.size() > 1) {
                        second = playListMost.get(sorted.get(1)).get(0);
                        Log.d(TAG, "second: "+second);
                        most.add(second);
                    }
                    return most;

                } else {
                    return null;
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

    public void addRow(String titlePlayList, String titleMusic){
        String SQL_ADD = "INSERT INTO "+
                Database.MUSIC_OF_PLAY_LIST.TABLE_NAME+
                " VALUES(" +
                " null, " +
                "'" + titlePlayList+ "'"  + ","+
                "'" + titleMusic   + "'"  + ")";

        mDatabase.queryData(SQL_ADD);
        closeDatabase();
    }

    public boolean compareMediaID(String titleMusic){
        Cursor cursor = mDatabase.getData(Database.MUSIC_OF_PLAY_LIST.QUERY);
        if (isExistData(cursor)){
            do {
                if (cursor.getString(2).equals(mAllMusic.getMusicId(titleMusic))){
                    return true;
                }
            }
            while (cursor.moveToNext());
        }
        return false;
    }

    public boolean searchMusicInPlayList(String titlePlayList, String titleMusic){
        Cursor cursor = mDatabase.getData(Database.MUSIC_OF_PLAY_LIST.QUERY);

        try {
            if (isExistData(cursor)) {

                do {
                    if (cursor.getString(1).equals(titlePlayList)) {
                        if (cursor.getString(2).equals(titleMusic)) {

                            return true;
                        }
                    }
                } while (cursor.moveToNext());
            }
        }catch (CursorIndexOutOfBoundsException e){

            return false;
        }
        finally {
            closeDatabase();
        }
        return false;
    }

    public boolean searchMusicInPlayList(String titlePlayList){
        Cursor cursor = mDatabase.getData(Database.MUSIC_OF_PLAY_LIST.QUERY);
        try {
            if (isExistData(cursor)) {

                do {
                    if (cursor.getString(1).equals(titlePlayList)) {
                        return true;
                    }
                } while (cursor.moveToNext());
            }
        }catch (CursorIndexOutOfBoundsException e){

            return false;
        }
        finally {
            closeDatabase();
        }
        return false;
    }

    public void updateSongs(String titlePlayList , String titleMusicUpdate){
        String SQL_UPDATE =
                "UPDATE "+ Database.MUSIC_OF_PLAY_LIST.TABLE_NAME +" SET "
                        + Database.MUSIC_OF_PLAY_LIST.NAME_SONGS + " = "+ "'"+ titleMusicUpdate+"'"+
                        " WHERE "+ Database.MUSIC_OF_PLAY_LIST.NAME_PLAY_LISTS + "="+ titlePlayList;
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public void deletePlayList(String titlePlayList){
        String SQL_DELETE = "DELETE FROM "+ Database.MUSIC_OF_PLAY_LIST.TABLE_NAME +
                " WHERE "+ Database.MUSIC_OF_PLAY_LIST.NAME_PLAY_LISTS + " = "+"'"+titlePlayList+"'";
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }
    
    public void deleteAllSongs(){
        try {
            mDatabase.queryData(Database.MUSIC_OF_PLAY_LIST.DELETE);
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public ArrayList<String> getAllMusicInPlayList(String titlePlayList){
        String SQL_ALL_SONG = Database.MUSIC_OF_PLAY_LIST.QUERY;
        Cursor cursor = mDatabase.getData(SQL_ALL_SONG);
        ArrayList<String> mAllMusic = new ArrayList<>();

        try {
            if (isExistData(cursor)){
                do {
                    if (cursor.getString(1).equals(titlePlayList)){
                        mAllMusic.add(cursor.getString(2));
                    }
                }
                while (cursor.moveToNext());
                return mAllMusic;
            }
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
        return null;
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
}
