package com.android.music_player.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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
        this.mDatabase = new ReaderSQL(context, Database.STATISTIC.DATABASE_NAME, null, 1);
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
                "" + 1 + ""      + ")";
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

    public int getNumber(String fileName){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);

        try {
            if (isSelect(cursor)){
                do {
                    if (cursor.getString(1).equals(fileName)){
                        return cursor.getInt(2);
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



    public String getMost(){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        int max = 0;
        String name = "";
        try {
            if (isSelect(cursor)){
                do {
                    if (cursor.getInt(2) >= max){
                        max = cursor.getInt(2);
                        name = cursor.getString(1);
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
        return null;
       /* String name = null;
        int max = 0;
        if (isSelect(cursor)) {
            do {
                if (cursor.getInt(1) >= max){
                    max = cursor.getInt(1);
                    name = cursor.getString(2);

                }
            } while (cursor.moveToNext());
            return name;
        }
        return null;*/
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

        if (!searchFileName(fileName) ){
            Log.d(TAG, fileName +" --- searchFileName: true ");
            addFileName(fileName);
            return false;
        }else {
            int s = (getNumber(fileName)) + 1;
            Log.d(TAG, fileName +" --- searchFileName: false ");
            Log.d(TAG, fileName+ ": "+s);

            String SQL_UPDATE =
                    "UPDATE "+Database.STATISTIC.TABLE_NAME +" SET "+Database.STATISTIC.MOST_SONG
                    +" = "  +""+ s +""
                            +" WHERE "+Database.STATISTIC.FILE_NAME+ " = "+"'"+fileName+"'";
            Log.d(TAG, SQL_UPDATE);
            mDatabase.queryData(SQL_UPDATE);
            closeDatabase();
            return true;
        }
    }

    public boolean searchFileName(String filePath){
        Cursor cursor = mDatabase.getData(Database.STATISTIC.QUERY);
        try {
            if (isSelect(cursor)){
                do  {

                    if (cursor.getString(1).equals(filePath)){
                        return true;
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
