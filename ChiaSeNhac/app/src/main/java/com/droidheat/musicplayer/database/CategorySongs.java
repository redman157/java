package com.droidheat.musicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class CategorySongs {

    private Context context;

    /* renamed from: db */
    private SQLiteDatabase database;
    private ReaderDB myDBHelper;


    private static CategorySongs instance;
    public static CategorySongs getInstance() {
        if ( instance == null){
            instance = new CategorySongs();
        }
        return instance;
    }

    private CategorySongs() {
    }

    public void newRenderDB(Context context, String database){
        this.context = context;
        this.myDBHelper = ReaderDB.newInstance(context, database);
//        this.myDBHelper = new ReaderDB(context, database);
    }

    public CategorySongs open() {
        this.database = this.myDBHelper.getWritableDatabase();
        return this;
    }

    public CategorySongs close() {
        this.myDBHelper.close();
        return this;
    }

    private String dropInvalidString(String str) {
        return str.replaceAll("[^A-Za-z0-9()\\[\\]]", "");
    }

    public long addRow(long playListId, SongModel songModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.CATEGORY.TITLE, songModel.getTitle());
        contentValues.put(Database.CATEGORY.CATEGORY, Long.toString(playListId));
        contentValues.put(Database.CATEGORY.VOTES, "0");
        contentValues.put(Database.CATEGORY.PATH, songModel.getPath());
        contentValues.put(Database.CATEGORY.ARTIST, songModel.getArtist());
        contentValues.put(Database.CATEGORY.ALBUM, songModel.getAlbum());
        contentValues.put(Database.CATEGORY.NAME, songModel.getFileName());
        contentValues.put(Database.CATEGORY.FAKE_PATH, dropInvalidString(songModel.getPath()));
        contentValues.put(Database.CATEGORY.DURATION, songModel.getDuration());
        contentValues.put(Database.CATEGORY.ALBUM_ID, songModel.getAlbumID());
        return database.insert(Database.CATEGORY.TABLE_NAME, "NULL", contentValues);
    }

    public ArrayList<SongModel> getAllRows(int id) {
        String row = Database.CATEGORY.CATEGORY +"="+id;

        SQLiteDatabase sQLiteDatabase = database;
        String tableName = Database.CATEGORY.TABLE_NAME;
        String[] allKeys = Database.CATEGORY.ALL_KEYS;
        String votes = Database.CATEGORY.VOTES+ " DESC";

        Cursor cursor = sQLiteDatabase.query(tableName, allKeys, row,
                null, null, null, votes);
        ArrayList<SongModel> models = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SongModel model = new SongModel();
                model.setTitle(cursor.getString(3));
                model.setPath(cursor.getString(4));
                model.setArtist(cursor.getString(5));
                model.setAlbum(cursor.getString(6));
                model.setFileName(cursor.getString(7));
                model.setDuration(cursor.getString(8));
                model.setAlbumID(cursor.getString(9));
                models.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return models;
    }

    public boolean deleteRow(long rowId, long category) {
        String where = Database.CATEGORY.COLUMN_NAME_ID+"="+rowId+" AND "+ Database.CATEGORY.CATEGORY+"="+category;

        return this.database.delete(Database.CATEGORY.TABLE_NAME, where, null) != 0;
    }

    public boolean deleteRowByPath(String path) {
        String where = Database.CATEGORY.PATH +"=" +path;

        try {
            if (this.database.delete(Database.CATEGORY.TABLE_NAME, where, null) != 0) {
                return true;
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public int getCount(long id) {
        String countId = "SELECT * FROM "+ Database.CATEGORY.TABLE_NAME+" WHERE "+ Database.CATEGORY.CATEGORY+"="+id;

        Cursor rawQuery = this.database.rawQuery(countId, null);
        int count = rawQuery.getCount();
        rawQuery.close();
        return count;
    }

    public boolean deleteAll(int id) {
        try {
            Cursor allRowsCursor = getAllRowsCursor();
            long columnIndexOrThrow = (long) allRowsCursor.getColumnIndexOrThrow(Database.CATEGORY.COLUMN_NAME_ID);
            if (allRowsCursor.moveToFirst()) {
                do {
                    deleteRow(allRowsCursor.getLong((int) columnIndexOrThrow), (long) id);
                } while (allRowsCursor.moveToNext());
            }
            allRowsCursor.close();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public Cursor getAllRowsCursor() {
        Cursor query = this.database.query(true, Database.CATEGORY.TABLE_NAME, Database.CATEGORY.ALL_KEYS, null, null, null, null, null, null);
        if (query != null) {
            query.moveToFirst();
        }
        return query;
    }

    public boolean checkRow(String row) {
        String where = Database.CATEGORY.FAKE_PATH+" = '"+dropInvalidString(row)+"';";

        Cursor query =
                this.database.query(Database.CATEGORY.TABLE_NAME, Database.CATEGORY.ALL_KEYS, where, null, null, null, null);
        if (query.getCount() > 0) {
            query.close();
            return true;
        }
        query.close();
        return false;
    }

    public boolean updateRow(String pathRow) {
        String albumId;
        String duration;
        String name;
        String album;
        String artist;
        String path;
        String title;
        int id;
        String category;
        String update = Database.CATEGORY.FAKE_PATH+" = '"+dropInvalidString(pathRow)+"';";

        Cursor query = this.database.query(Database.CATEGORY.TABLE_NAME, Database.CATEGORY.ALL_KEYS, update, null, null, null, null);
        if (query != null) {
            query.moveToFirst();
            category = query.getString(1);
            id = query.getInt(2) + 1;
            title = query.getString(3);
            path = query.getString(4);
            artist = query.getString(5);
            album = query.getString(6);
            name = query.getString(7);
            duration = query.getString(8);
            albumId = query.getString(9);
        } else {
            category = null;
            title = null;
            path = null;
            artist = null;
            album = null;
            name = null;
            duration = null;
            albumId = null;
            id = 0;
        }
        if (query != null) {
            query.close();
        }

        ContentValues values = new ContentValues();
        values.put(Database.CATEGORY.CATEGORY, category);
        values.put(Database.CATEGORY.VOTES, Integer.valueOf(id));
        values.put(Constants.PREFERENCES.TITLE, title);
        values.put(Constants.PREFERENCES.PATH, path);
        values.put(Constants.PREFERENCES.ARTIST, artist);
        values.put(Constants.PREFERENCES.ALBUM, album);
        values.put(Constants.PREFERENCES.NAME, name);
        values.put(Constants.PREFERENCES.DURATION, duration);
        values.put(Constants.PREFERENCES.ALBUMID, albumId);
        values.put(Database.CATEGORY.FAKE_PATH, dropInvalidString(path));
        if (this.database.update(Database.CATEGORY.TABLE_NAME, values, update, null) != 0) {
            return true;
        }
        return false;
    }
}
