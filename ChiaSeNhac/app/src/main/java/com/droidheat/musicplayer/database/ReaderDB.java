package com.droidheat.musicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.droidheat.musicplayer.Constants;

public class ReaderDB extends SQLiteOpenHelper {

    private Context context;

    private static String TAG = "READERDB_LOG";
    public static final int DATABASE_VERSION = 1;

    private static ReaderDB instance;



    private String mDatabaseName;
    public static ReaderDB getInstance() {

        return instance ;


    }

    public static ReaderDB newInstance(Context context, String databaseName){

        instance = new ReaderDB(context, databaseName);

        return instance;
    }


    private ReaderDB(Context context, String DATABASE_NAME) {

        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {

        switch (mDatabaseName){

            case Constants.VALUE.PLAYLISTSONGS_DB:
                database.execSQL(Database.PLAYSONGS.SQL_CREATE_ENTRIES);
                break;
            case Constants.VALUE.PLAYLIST_DB:
                database.execSQL(Database.PLAYLIST.SQL_CREATE_ENTRIES);
                break;

            case Constants.VALUE.FAVS_DB:
                database.execSQL(Database.FAVOURITE.SQL_DELETE_ENTRIES);

                break;
            case Constants.VALUE.CATEGORIES_DB:
                database.execSQL(Database.CATEGORY.SQL_DELETE_ENTRIES);
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        switch (mDatabaseName){
            case Constants.VALUE.FAVS_DB:
                database.execSQL(Database.FAVOURITE.SQL_DELETE_ENTRIES);
                onCreate(database);
                break;
            case Constants.VALUE.CATEGORIES_DB:
                database.execSQL(Database.CATEGORY.SQL_DELETE_ENTRIES);
                onCreate(database);
                break;
            case Constants.VALUE.PLAYLIST_DB:
                database.execSQL(Database.PLAYLIST.SQL_DELETE_ENTRIES);
                onCreate(database);
                break;
            case Constants.VALUE.PLAYLISTSONGS_DB:
                database.execSQL(Database.PLAYSONGS.SQL_DELETE_ENTRIES);
                onCreate(database);
                break;
        }
    }

    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        switch (mDatabaseName){
            case Constants.VALUE.PLAYLIST_DB:
            case Constants.VALUE.PLAYLISTSONGS_DB:
                onUpgrade(sQLiteDatabase, i, i2);
                break;
        }

    }
}