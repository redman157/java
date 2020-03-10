package com.droidheat.musicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.droidheat.musicplayer.Constants;

public class ReaderDB extends SQLiteOpenHelper {
    private static String databaseName;
    private Context context;

    private String TAG = "READERDB_LOG";
    public static final int DATABASE_VERSION = 1;

    private static ReaderDB instance;

    public static ReaderDB getInstance() {
        return instance;
    }

    static ReaderDB newInstance(Context context, String DATABASE_NAME){
        instance = new ReaderDB(context, DATABASE_NAME);
        databaseName = DATABASE_NAME;
        return instance;
    }

    public String getDATABASE_NAME() {
        return databaseName;
    }

    public void setDATABASE_NAME(String DATABASE_NAME) {
        this.databaseName = DATABASE_NAME;
    }

    private ReaderDB(Context context, String DATABASE_NAME) {

        super(context, DATABASE_NAME, null, 1);

    }


    public void onCreate( SQLiteDatabase database) {
        switch (databaseName){
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

    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        switch (databaseName){
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
        switch (databaseName){
            case Constants.VALUE.PLAYLIST_DB:
            case Constants.VALUE.PLAYLISTSONGS_DB:
                onUpgrade(sQLiteDatabase, i, i2);
                break;
        }

    }
}