package com.droidheat.musicplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.models.SongModel;

import java.util.ArrayList;

public class PlaylistSongs {
    private Playlist allPlaylistDB;
    private Context context;
    /* renamed from: db */
    private SQLiteDatabase database;
    private ReaderDB myDBHelper;

    private static PlaylistSongs instance;

    public static PlaylistSongs getInstance() {
        if (instance == null){
            instance = new PlaylistSongs();
        }
        return instance;
    }

    private PlaylistSongs() {
    }

    public void newRenderDB(Context context, String database){
        this.allPlaylistDB = Playlist.getInstance();
        this.context = context;
        this.allPlaylistDB.newRenderDB(context,Constants.VALUE.PLAYLISTSONGS_DB);
        myDBHelper = ReaderDB.newInstance(context, database);

    }

    public PlaylistSongs open() {
        this.database = myDBHelper.getWritableDatabase();
        return this;
    }

    public PlaylistSongs close() {
        this.myDBHelper.close();
        return this;
    }

    public long addRow(long j, SongModel songModel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.PLAYSONGS.TITLE, songModel.getTitle());
        contentValues.put(Database.PLAYSONGS.PLAYLIST_ID, Long.toString(j));
        contentValues.put(Database.PLAYSONGS.PATH, songModel.getPath());
        contentValues.put(Database.PLAYSONGS.ARTIST, songModel.getArtist());
        contentValues.put(Database.PLAYSONGS.ALBUM, songModel.getAlbum());
        contentValues.put(Database.PLAYSONGS.NAME, songModel.getFileName());
        contentValues.put(Database.PLAYSONGS.DURATION, songModel.getDuration());
        contentValues.put(Database.PLAYSONGS.ALBUM_ID, songModel.getAlbumID());
        return this.database.insert(Database.PLAYSONGS.TABLE_NAME, "NULL", contentValues);
    }

    public ArrayList<SongModel> getAllRows(int id) {
        String db_PlayList = Database.PLAYSONGS.PLAYLIST_ID+"="+id;
        Cursor query = database.query(Database.PLAYSONGS.TABLE_NAME, Database.PLAYSONGS.ALL_KEYS,
                db_PlayList, null, null, null, null);
        ArrayList<SongModel> arrayList = new ArrayList<>();
        if (query.moveToFirst()) {
            do {
                SongModel songModel = new SongModel();
                songModel.setFileName(query.getString(6));
                songModel.setTitle(query.getString(2));
                songModel.setArtist(query.getString(4));
                songModel.setAlbum(query.getString(5));
                songModel.setAlbumID(query.getString(8));
                songModel.setPath(query.getString(3));
                songModel.setDuration(query.getString(7));
                arrayList.add(songModel);
            } while (query.moveToNext());
        }
        query.close();
        return arrayList;
    }

    public SongModel getRow(long id) {
        String albumId = null;
        String duration = null;
        String fileName ="";
        String album= "";
        String artist="";
        String path="";
        Cursor query = this.database.query(true, Database.PLAYSONGS.TABLE_NAME, Database.PLAYSONGS.ALL_KEYS, Database.PLAYSONGS.SQL_CONTROL_ROW_SONG+id, null, null, null, null, null);
        String title = null;
        if (query != null) {
            query.moveToFirst();
            title = query.getString(2);
            path = query.getString(3);
            artist = query.getString(4);
            album = query.getString(5);
            fileName = query.getString(6);
            duration = query.getString(7);
            albumId = query.getString(8);
        }
        query.close();
        SongModel songModel = new SongModel();
        songModel.setTitle(title);
        songModel.setPath(path);
        songModel.setArtist(artist);
        songModel.setAlbum(album);
        songModel.setFileName(fileName);
        songModel.setDuration(duration);
        songModel.setAlbumID(albumId);
        return songModel;
    }

    public boolean deleteRow(long columnId, long playListId) {
        String where = Database.PLAYSONGS.COLUMN_NAME_ID+"="+columnId+" AND "+ Database.PLAYSONGS.PLAYLIST_ID+"="+playListId;

        return this.database.delete(Database.PLAYSONGS.TABLE_NAME, where, null) != 0;
    }

    public boolean deleteRowByPath(String row) {
        String rowPath = Database.PLAYSONGS.PATH+"="+row;

        try {
            if (this.database.delete(Database.PLAYSONGS.TABLE_NAME, rowPath.toString(), null) != 0) {
                return true;
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public int getCount(long id) {
        String getCount = "SELECT * FROM "+ Database.PLAYSONGS.TABLE_NAME+ " WHERE "+ Database.PLAYSONGS.PLAYLIST_ID+ "="+id;
        Cursor rawQuery = this.database.rawQuery(getCount, null);
        int count = rawQuery.getCount();
        rawQuery.close();
        return count;
    }

    public boolean deleteAll(int i) {
        try {
            Cursor allRowsCursor = getAllRowsCursor();
            long columnIndexOrThrow = (long) allRowsCursor.getColumnIndexOrThrow(Database.PLAYSONGS.COLUMN_NAME_ID);
            if (allRowsCursor.moveToFirst()) {
                do {
                    deleteRow(allRowsCursor.getLong((int) columnIndexOrThrow), (long) i);
                } while (allRowsCursor.moveToNext());
            }
            allRowsCursor.close();
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public Cursor getAllRowsCursor() {
        Cursor query = this.database.query(true, Database.PLAYSONGS.TABLE_NAME, Database.PLAYSONGS.ALL_KEYS, null, null, null, null, null, null);
        if (query != null) {
            query.moveToFirst();
        }
        return query;
    }
}