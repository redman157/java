package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationSongs {
    private Context mContext;
    private String TAG = "RelationMusicLog";
    /* renamed from: db */
    private SongOfPlayList mSongOfPlayList;
    private ReaderSQL mDatabase;

    public RelationSongs(Context context){
        mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.RELATION_SONGS.DATABASE_NAME, null, 1);
        mSongOfPlayList = new SongOfPlayList(context);
        mDatabase.queryData(Database.RELATION_SONGS.CREATE_TABLE);
        mDatabase.close();
    }

    public RelationSongs closeDatabase(){
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

    public ArrayList<String> getMost(){
        Cursor cursor = mDatabase.getData(Database.RELATION_SONGS.QUERY);
        String first = "";
        String second = "";

        Map<Integer, List<String>> playListMost = new HashMap<>();
        ArrayList<String> most = new ArrayList<>();
        try {
            if (isSelect(cursor)){

                List<Integer> sorted;
                Log.d(TAG, cursor.getCount()+" kích thước");

                do {
                    Log.d(TAG, "Enter");
                    int counter = cursor.getInt(1);
                    String playlist = cursor.getString(2);
                    Log.d(TAG, playlist);
                    if (playListMost.get(counter) == null) {
                        playListMost.put(counter, new ArrayList<String>());
                    }

                    playListMost.get(counter).add(playlist);
                } while (cursor.moveToNext());
                sorted = new ArrayList<>(playListMost.keySet());

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
        } finally {
            closeDatabase();
        }
        return null;
    }


    public void addRow(String namePlayList, int id_song){
        String SQL_ADD = "INSERT INTO "+
                Database.RELATION_SONGS.TABLE_NAME+
                " VALUES(" +
                " null, " +
                "" + 0 +" "            + ","+
                "'" + namePlayList+ "'"  + ","+
                "" + id_song + ""      + ")";

        mDatabase.queryData(SQL_ADD);
        closeDatabase();
    }

    public boolean compareIdSong(SongModel songModel){
        Cursor cursor = mDatabase.getData(Database.RELATION_SONGS.QUERY);
        if (isSelect(cursor)){
            do {
                if (cursor.getInt(2) == mSongOfPlayList.getId(songModel)){
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
                        +Database.RELATION_SONGS.ID_SONGS + " = "+ "'"+ id+"'"+
                        " WHERE "+Database.RELATION_SONGS.NAME_PLAY_LIST+ "="+ namePlayList;
        mDatabase.queryData(SQL_UPDATE);
        closeDatabase();
    }

    public void deleteSongs(String namePlayList){
        String SQL_DELETE =  "UPDATE "+Database.RELATION_SONGS.TABLE_NAME +" SET "
                +Database.RELATION_SONGS.ID_SONGS + " = "+ "'"+ -1 +"'"+
                " WHERE "+Database.RELATION_SONGS.NAME_PLAY_LIST+ "="+ namePlayList;
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

    public ArrayList<SongModel> getAllSong(String namePlayList){
        String SQL_ALL_SONG = Database.RELATION_SONGS.QUERY;
        Cursor cursor = mDatabase.getData(SQL_ALL_SONG);
        try {
            if (isSelect(cursor) && getSize() > 0){
                do {
                    if (cursor.getString(2).equals(namePlayList)){
                        return mSongOfPlayList.getAllSong(cursor.getInt(2));
                    }
                }
                while (cursor.moveToNext());
            }
        } catch (SQLiteException e){
            Log.d(TAG, e.getMessage());
        }finally {
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
