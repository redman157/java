package com.droidheat.musicplayer.database;

public class Database {
    public @interface CATEGORY{
        String DATABASE_NAME = "categories.db";
        String TABLE_NAME = "category";
        String COLUMN_NAME_ID = "id";
        String CATEGORY = "category";
        String VOTES = "rank";
        String TITLE = "NAME_PLAYLIST";
        String PATH = "PATH";
        String ARTIST = "ARTIST";
        String ALBUM = "ALBUM";
        String NAME = "NAME";
        String ALBUM_ID = "ALBUMID";
        String FAKEPATH = "fakepath";
        String DURATION = "DURATION";
        String[] ALL_KEYS = new String[]
                {COLUMN_NAME_ID, CATEGORY, VOTES, TITLE, PATH, ARTIST, ALBUM, NAME, DURATION, ALBUM_ID, FAKEPATH};
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                        CATEGORY + " TEXT "    + "," +
                        VOTES    + " INTEGER " + "," +
                        TITLE    + " TEXT "    + "," +
                        PATH     + " TEXT"     + "," +
                        ARTIST   + " TEXT "    + "," +
                        ALBUM    + " TEXT "    + "," +
                        NAME     + " TEXT "    + "," +
                        DURATION + " TEXT "    + "," +
                        ALBUM_ID + " TEXT "    + "," +
                        FAKEPATH + " TEXT "    + ");";
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;
        String SQL_CONTROL_ROW_LIST = PLAYLIST.ID +"=";
        String SQL_SEARCH_LIST = PLAYLIST.TITLE+"==";
        String SQL_CONTROL_ROW_SONG = PLAYLISTSONGS.COLUMN_NAME_ID+"=";
    }

    public @interface FAVOURITE{
        String DATABASE_NAME = "Favs.db";
        String TABLE_NAME = "fav";
        String COLUMN_NAME_ID = "id";
        String TITLE = "NAME_PLAYLIST";
        String PATH = "PATH";
        String ARTIST = "ARTIST";
        String ALBUM = "ALBUM";
        String NAME = "NAME";
        String ALBUM_ID = "ALBUMID";
        String DURATION = "DURATION";
        String[] ALL_KEYS = new String[]
            {COLUMN_NAME_ID, TITLE, PATH, ARTIST, ALBUM, NAME, DURATION, ALBUM_ID};
        String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                    TITLE     + " TEXT " + "," +
                    PATH      + " TEXT " + "," +
                    ARTIST    + " TEXT " + "," +
                    ALBUM     + " TEXT " + "," +
                    NAME      + " TEXT " + "," +
                    DURATION  + " TEXT " + "," +
                    ALBUM_ID  + " TEXT " + ");";
        String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;
        String SQL_CONTROL_ROW_LIST = PLAYLIST.ID +"=";
        String SQL_SEARCH_LIST = PLAYLIST.TITLE+"==";
        String SQL_CONTROL_ROW_SONG = PLAYLISTSONGS.COLUMN_NAME_ID+"=";
    }

    public @interface PLAYLIST {
        String DATABASE_NAME = "playlist.db";
        String TABLE_NAME = "queue";
        String TITLE = "title";
        String ID = "id";

        String[] ALL_KEYS = {PLAYLIST.ID, PLAYLIST.TITLE};

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                        TITLE + " TEXT " + ");";
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        String QUERY = "SELECT * FORM "+ Database.PLAYLIST.TABLE_NAME;

        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;

    }

    public  @interface PLAYLISTSONGS {
        String DATABASE_NAME = "playlistsongs.db";
        String TABLE_NAME = "playlistsongs";
        String COLUMN_NAME_ID = "id";
        String PLAYLIST_ID = "playlist";
        String TITLE = "NAME_PLAYLIST";
        String PATH = "PATH";
        String ARTIST = "ARTIST";
        String ALBUM = "ALBUM";
        String NAME = "NAME";
        String ALBUM_ID = "ALBUMID";
        String DURATION = "DURATION";
        String[] ALL_KEYS = new String[]
                {COLUMN_NAME_ID, PLAYLIST_ID, TITLE, PATH, ARTIST, ALBUM, NAME, DURATION, ALBUM_ID};
        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;
        String SQL_CONTROL_ROW_LIST = PLAYLIST.ID +"=";
        String SQL_SEARCH_LIST = PLAYLIST.TITLE+"==";
        String SQL_CONTROL_ROW_SONG = PLAYLISTSONGS.COLUMN_NAME_ID+"=";
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                        PLAYLIST_ID + " TEXT " + "," +
                        TITLE       + " TEXT " + "," +
                        PATH        + " TEXT " + "," +
                        ARTIST      + " TEXT " + "," +
                        ALBUM       + " TEXT " + "," +
                        NAME        + " TEXT " + "," +
                        DURATION    + " TEXT " + "," +
                        ALBUM_ID    + " TEXT " + ");";
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;


    }
}
