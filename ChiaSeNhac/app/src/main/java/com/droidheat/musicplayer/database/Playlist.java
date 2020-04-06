package com.droidheat.musicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.droidheat.musicplayer.Constants;

import java.util.ArrayList;
import java.util.HashMap;
@SuppressWarnings("unchecked")
public class Playlist {

    private Context context;
    private String TAG = "PLAYLISTLOG";
    /* renamed from: db */
    private SQLiteDatabase database;
    private ReaderDB myDBHelper;

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
        this.myDBHelper = new ReaderDB(context);

    }

    public Playlist open() {

        this.database = this.myDBHelper.getWritableDatabase();
        return this;
    }

    public Playlist close() {

        this.myDBHelper.close();

        return this;
    }

    public long addRow(String row) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.PLAYLIST.TITLE, row);
        return this.database.insert(Database.PLAYLIST.TABLE_NAME, "NULL", contentValues);
    }

    public ArrayList<HashMap<String, String>> getAllRows() {
        Cursor query = this.database.query(Database.PLAYLIST.TABLE_NAME,
                Database.PLAYLIST.ALL_KEYS,
                null, null, null, null, null);
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        if (query.moveToFirst()) {
            do {
                HashMap hashMap = new HashMap();
                hashMap.put("ID", query.getString(0));
                hashMap.put("title", query.getString(1));
                arrayList.add(hashMap);
            } while (query.moveToNext());
        }
        query.close();
        return arrayList;
    }

    public int getCount() {
        Cursor cursor = database.rawQuery(Database.PLAYLIST.SQL_QUERY_LIST, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public HashMap<String, String> getRow(long id) {
        String columnIndex;

        Cursor query = this.database.query(true,
                Database.PLAYLIST.TABLE_NAME, Database.PLAYLIST.ALL_KEYS, Database.PLAYLIST.SQL_CONTROL_ROW_LIST +id,
                null, null, null, null, null);
        if (query != null) {
            query.moveToFirst();
            columnIndex = query.getString(1);
        } else {
            columnIndex = null;
        }
        query.close();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Constants.PREFERENCES.TITLE, columnIndex);
        return hashMap;
    }

    public boolean deleteRow(long rowId) {

        return this.database.delete(Database.PLAYLIST.TABLE_NAME,
                Database.PLAYLIST.SQL_CONTROL_ROW_LIST +rowId, null) != 0;
    }

    public boolean deleteAll() {
        try {
            Cursor cursor = getAllRowsCursor();
            long rowId = (long) cursor.getColumnIndexOrThrow(Database.PLAYLIST.COLUMN_NAME_ID);
            if (cursor.moveToFirst()) {
                do {
                    deleteRow(cursor.getLong((int) rowId));
                } while (cursor.moveToNext());
            }
            cursor.close();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public Cursor getAllRowsCursor() {
        Cursor query = this.database.query(true,
                Database.PLAYLIST.TABLE_NAME,
                Database.PLAYLIST.ALL_KEYS,
                null, null, null, null, null, null);
        if (query != null) {
            query.moveToFirst();
        }
        return query;
    }

    public boolean updateRow(long id, String row) {
        String where = Database.PLAYLIST.COLUMN_NAME_ID+"="+id+";";
        Cursor query = this.database.query(true,
                Database.PLAYLIST.TABLE_NAME, Database.PLAYLIST.ALL_KEYS,
                where, null, null, null, null, null);
        if (query != null) {
            query.moveToFirst();
        }
        query.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.PLAYLIST.TITLE, id);
        return this.database.update(Database.PLAYLIST.TABLE_NAME, contentValues, Database.PLAYLIST.SQL_CONTROL_ROW_LIST +id+";", null) != 0;
    }

    public boolean searchPlaylist(String name) {
        SQLiteDatabase sQLiteDatabase = this.database;
        Cursor query = sQLiteDatabase.query(true,
                Database.PLAYLIST.TABLE_NAME,
                Database.PLAYLIST.ALL_KEYS,
                Database.PLAYLIST.SQL_SEARCH_LIST +name+";",
                null, null, null, null, null);
        if (query == null) {
            return false;
        }
        query.close();
        return true;
    }

    public class ReaderDB extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 3;
        public static final String DATABASE_NAME = "categories.db";

        public ReaderDB(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Database.PLAYLIST.SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(Database.PLAYLIST.SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

    }
}
