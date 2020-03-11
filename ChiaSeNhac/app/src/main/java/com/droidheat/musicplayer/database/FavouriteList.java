package com.droidheat.musicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.droidheat.musicplayer.models.SongModel;
import java.util.ArrayList;
import java.util.HashMap;

public class FavouriteList {
    private Context context;

    /* renamed from: db */
    private SQLiteDatabase database;
    private ReaderDB myDBHelper;

    private static FavouriteList instance;

    public static FavouriteList getInstance() {
        if (instance == null){
            instance = new FavouriteList();
        }
        return instance;
    }

    private FavouriteList() {
    }

    public void newRenderDB(Context context, String database){
        this.context = context;
        this.myDBHelper = ReaderDB.newInstance(context, database);
//        this.myDBHelper = new ReaderDB(context, database);
    }

    public FavouriteList open() {
        this.database = myDBHelper.getWritableDatabase();
        return this;
    }

    public FavouriteList close() {
        this.myDBHelper.close();
        return this;
    }


    public long addRow(SongModel songModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.FAVOURITE.TITLE, songModel.getTitle());
        contentValues.put(Database.FAVOURITE.PATH, songModel.getPath());
        contentValues.put(Database.FAVOURITE.ARTIST, songModel.getArtist());
        contentValues.put(Database.FAVOURITE.ALBUM, songModel.getAlbum());
        contentValues.put(Database.FAVOURITE.NAME, songModel.getFileName());
        contentValues.put(Database.FAVOURITE.DURATION, songModel.getDuration());
        contentValues.put(Database.FAVOURITE.ALBUM_ID, songModel.getAlbumID());
        return this.database.insert(Database.FAVOURITE.TABLE_NAME, "NULL", contentValues);
    }


    /*
     * Returns ArrayList Hashmap of all Rows
     */
    public ArrayList<SongModel> getAllRows() {


        String db_Favourite = Database.FAVOURITE.COLUMN_NAME_ID+" DESC";

        Cursor cursor = database.query(Database.FAVOURITE.TABLE_NAME,
                Database.FAVOURITE.ALL_KEYS, null, null, null, null, db_Favourite);
        ArrayList<SongModel> profilesArray = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SongModel song = new SongModel();
                song.setTitle(cursor.getString(1));
                song.setPath(cursor.getString(2));
                song.setArtist(cursor.getString(3));
                song.setAlbum(cursor.getString(4));
                song.setFileName(cursor.getString(5));
                song.setDuration(cursor.getString(6));
                song.setAlbumID(cursor.getString(7));
                profilesArray.add(song);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return profilesArray;
    }

    /*
     * Get Row
     */
    public SongModel getRow(long rowId) {
        String title = null, path = null, artist = null, album = null, name = null, duration = null, albumid = null;
        String where = Database.FAVOURITE.COLUMN_NAME_ID +"="+rowId;
        Cursor cursor = database.query(
                true, Database.FAVOURITE.TABLE_NAME, Database.FAVOURITE.ALL_KEYS,
                where, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            title = cursor.getString(1);
            path = cursor.getString(2);
            artist = cursor.getString(3);
            album = cursor.getString(4);
            name = cursor.getString(5);
            duration = cursor.getString(6);
            albumid = cursor.getString(7);
        }
        cursor.close();

        SongModel songModel = new SongModel();
        songModel.setTitle(title);
        songModel.setPath(path);
        songModel.setArtist(artist);
        songModel.setAlbum(album);
        songModel.setFileName(name);
        songModel.setDuration(duration);
        songModel.setAlbumID(albumid);
        return songModel;
    }

    /*
     * Delete a Row
     */
    private boolean deleteRow(long rowId) {
        String delete = Database.FAVOURITE.COLUMN_NAME_ID +"="+rowId;
        return this.database.delete(Database.FAVOURITE.TABLE_NAME, delete, null) != 0;
    }

    public boolean deleteRowByPath(String path) {
        String delete = Database.FAVOURITE.PATH +"="+path;
        try {
            if (this.database.delete(Database.FAVOURITE.TABLE_NAME, delete, null) != 0) {
                return true;
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public boolean deleteAll() {
        try {
            Cursor allRowsCursor = getAllRowsCursor();
            long columnIndexOrThrow = allRowsCursor.getColumnIndexOrThrow(Database.FAVOURITE.COLUMN_NAME_ID);
            if (allRowsCursor.moveToFirst()) {
                do {
                    deleteRow(allRowsCursor.getLong((int) columnIndexOrThrow));
                } while (allRowsCursor.moveToNext());
            }
            allRowsCursor.close();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }
    /*
     * Get All Rows Cursor
     */
    // Return all data in the database.
    private Cursor getAllRowsCursor() {
        Cursor cursor = this.database.query(true,
                Database.FAVOURITE.TABLE_NAME, Database.FAVOURITE.ALL_KEYS,
                null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean updateRow(HashMap<String, String> list, int id) {
        String where = Database.FAVOURITE.COLUMN_NAME_ID + "="+id;

        /*
         * CHANGE 4:
         */
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:

        ContentValues contentValues = new ContentValues();
        String title = Database.FAVOURITE.TITLE;
        contentValues.put(title,  list.get(title));

        String path = Database.FAVOURITE.PATH;
        contentValues.put(path,  list.get(path));

        String artist = Database.FAVOURITE.ARTIST;
        contentValues.put(artist,  list.get(artist));

        String album = Database.FAVOURITE.ALBUM;
        contentValues.put(album,  list.get(album));

        String name = Database.FAVOURITE.NAME;
        contentValues.put(name,  list.get(name));

        String duration = Database.FAVOURITE.DURATION;
        contentValues.put(duration,  list.get(duration));

        String albumId = Database.FAVOURITE.ALBUM_ID;
        contentValues.put(albumId, list.get(albumId));

        return this.database.update(Database.FAVOURITE.TABLE_NAME, contentValues, where, null) != 0;
    }
}
