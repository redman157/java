package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.android.music_player.utils.Database;

public class Statistic {

    private Context mContext;
    /* renamed from: db */
    private String TAG = "StatisticLog";
    private ReaderSQL mDatabase;

    public Statistic(Context context){
        this.mContext = context;
        this.mDatabase = new ReaderSQL(context, Database.ALL_PLAY_LISTS.DATABASE_NAME, null, 1);
        mDatabase.queryData(Database.STATISTIC.CREATE_TABLE);
        mDatabase.close();
    }

    public Statistic closeDatabase() {
        this.mDatabase.close();
        return this;
    }

    public void addFileName(String fileName){
        String SQL_ADD = "INSERT INTO "+
                Database.STATISTIC.TABLE_NAME+
                " VALUES(" +
                " null, " +
                "'" + fileName+ "'"+ ","+
                "'" + 0 + "'"      + ","+
                "')";
        mDatabase.queryData(SQL_ADD);
        closeDatabase();
    }


    public boolean isSelect(Cursor cursor){
        cursor = mDatabase.getData(Database.STATISTIC.QUERY);
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

    public int getNumber(String fileName){
        String QUERY_NUMBER = "SELECT most_song FROM "+Database.STATISTIC.TABLE_NAME+
                "WHERE "+Database.STATISTIC.FILE_NAME+" = "+fileName;
        Cursor cursor = mDatabase.getData(QUERY_NUMBER);
        if (cursor == null){
            addFileName(fileName);
            return 0;
        }else {
            try {
                if (isSelect(cursor)) {
                    return cursor.getInt(2);
                }
            } catch (SQLiteException e) {
                Log.d(TAG, e.getMessage());
            } finally {
                closeDatabase();
            }
            return -1;
        }
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

    public boolean increase(String fileName){
        String SQL_QUERY = "SELECT "+Database.STATISTIC.FILE_NAME +" From "+Database.STATISTIC.TABLE_NAME
                + " WHERE "+Database.STATISTIC.FILE_NAME+" = "+fileName;

        Cursor cursor = mDatabase.getData(SQL_QUERY);
        if (cursor == null){
            addFileName(fileName);
            return false;
        }else {
            String SQL_UPDATE =
                    "UPDATE "+Database.STATISTIC.TABLE_NAME +" SET "+Database.STATISTIC.MOST_SONG
                    +" = "  +"'"+ (getNumber(fileName) + 1)+"'"
                            +" WHERE "+Database.STATISTIC.FILE_NAME+ " = "+fileName;
            mDatabase.queryData(SQL_QUERY);
            closeDatabase();
            return true;
        }
    }

    public void resetSong(String fileName){
        String SQL_UPDATE =
                "UPDATE "+Database.STATISTIC.TABLE_NAME +" SET " +
                        Database.STATISTIC.MOST_SONG +" = " +"'" + 0 +"'"+
                        " WHERE "+Database.STATISTIC.FILE_NAME+ " = "+fileName;
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
