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

public class RelationMusic {
    private Context mContext;
    private String TAG = "RelationMusicLog";
    /* renamed from: db */
    private MusicOfPlayList mMusicOfPlayList;
    private ReaderSQL mDatabase;

    public RelationMusic(Context context){
        mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.RELATION_SONGS.DATABASE_NAME, null, 1);
        mMusicOfPlayList = new MusicOfPlayList(context);
        mDatabase.queryData(Database.RELATION_SONGS.CREATE_TABLE);
        mDatabase.close();
    }

    public RelationMusic closeDatabase(){
        mDatabase.close();
        return this;
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

    public ArrayList<String> getPlayListMost(){
        Cursor cursor = mDatabase.getData(Database.RELATION_SONGS.QUERY);
        String first = "";
        String second = "";

        Map<Integer, List<String>> playListMost = new HashMap<>();
        ArrayList<String> most = new ArrayList<>();
        try {
            if (isSelect(cursor)){
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

    public void addRow(String namePlayList, String nameSong){
        String SQL_ADD = "INSERT INTO "+
                Database.RELATION_SONGS.TABLE_NAME+
                " VALUES(" +
                " null, " +
                "'" + namePlayList+ "'"  + ","+
                "'" + nameSong + "'"     + ")";

        mDatabase.queryData(SQL_ADD);
        closeDatabase();
    }

    public boolean compareMediaID(String mediaID){
        Cursor cursor = mDatabase.getData(Database.RELATION_SONGS.QUERY);
        if (isSelect(cursor)){
            do {
                if (cursor.getString(2).equals(mMusicOfPlayList.getSongName(mediaID))){
                    return true;
                }
            }
            while (cursor.moveToNext());
        }
        return false;
    }

    public void updateSongs(String namePlayList , int id){
        String SQL_UPDATE =
                "UPDATE "+Database.RELATION_SONGS.TABLE_NAME +" SET "
                        +Database.RELATION_SONGS.NAME_SONGS + " = "+ "'"+ id+"'"+
                        " WHERE "+Database.RELATION_SONGS.NAME_PLAY_LISTS + "="+ namePlayList;
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public void deletePlayList(String namePlayList){
        String SQL_DELETE = "DELETE FROM "+Database.RELATION_SONGS.TABLE_NAME +
                " WHERE "+Database.RELATION_SONGS.NAME_PLAY_LISTS + " = "+"'"+namePlayList+"'";
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }

    public void deleteSongs(String namePlayList){
        String SQL_DELETE =  "UPDATE "+Database.RELATION_SONGS.TABLE_NAME +" SET "
                +Database.RELATION_SONGS.NAME_SONGS + " = "+ "'"+ -1 +"'"+
                " WHERE "+Database.RELATION_SONGS.NAME_PLAY_LISTS + "="+ namePlayList;
        mDatabase.queryData(SQL_DELETE);
        closeDatabase();
    }

    public void deleteAllSongs(){
        try {
            mDatabase.queryData(Database.RELATION_SONGS.DELETE);
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        } finally {
            closeDatabase();
        }
    }

    public ArrayList<String> getAllSongName(String namePlayList){
        String SQL_ALL_SONG = Database.RELATION_SONGS.QUERY;
        Cursor cursor = mDatabase.getData(SQL_ALL_SONG);
        ArrayList<String> nameSongs = new ArrayList<>();

        try {
            if (isSelect(cursor)){
                do {
                    if (cursor.getString(1).equals(namePlayList)){
                        nameSongs.add(cursor.getString(2));
                    }
                }
                while (cursor.moveToNext());
                return nameSongs;
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
}
