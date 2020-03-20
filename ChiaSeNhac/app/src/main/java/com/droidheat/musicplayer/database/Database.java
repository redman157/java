package com.droidheat.musicplayer.database;

public class Database {
    public @interface CATEGORY{
        String TEXT_TYPE = " TEXT";
        String COMMA_SEP = ",";
        String TABLE_NAME = "category";
        String COLUMN_NAME_ID = "id";
        String CATEGORY = "category";
        String VOTES = "rank";
        String TITLE = "title";
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
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                        CATEGORY + TEXT_TYPE + COMMA_SEP +
                        VOTES + " INTEGER " + COMMA_SEP +
                        TITLE + TEXT_TYPE + COMMA_SEP +
                        PATH + TEXT_TYPE + COMMA_SEP +
                        ARTIST + TEXT_TYPE + COMMA_SEP +
                        ALBUM + TEXT_TYPE + COMMA_SEP +
                        NAME + TEXT_TYPE + COMMA_SEP +
                        DURATION + TEXT_TYPE + COMMA_SEP +
                        ALBUM_ID + TEXT_TYPE + COMMA_SEP +
                        FAKEPATH + TEXT_TYPE +
                        ");";
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;
        String SQL_CONTROL_ROW_LIST = PLAYLIST.COLUMN_NAME_ID+"=";
        String SQL_SEARCH_LIST = PLAYLIST.TITLE+"==";
        String SQL_CONTROL_ROW_SONG = PLAYSONGS.COLUMN_NAME_ID+"=";
    }

    public @interface FAVOURITE{
        String TEXT_TYPE = " TEXT";
        String COMMA_SEP = ",";
        String TABLE_NAME = "fav";
        String COLUMN_NAME_ID = "id";
        String TITLE = "title";
        String PATH = "PATH";
        String ARTIST = "ARTIST";
        String ALBUM = "ALBUM";
        String NAME = "NAME";
        String ALBUM_ID = "ALBUMID";
        String DURATION = "DURATION";
        String[] ALL_KEYS = new String[]
            {COLUMN_NAME_ID, TITLE, PATH, ARTIST, ALBUM, NAME, DURATION, ALBUM_ID};
        String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE + COMMA_SEP +
                    ARTIST + TEXT_TYPE + COMMA_SEP +
                    ALBUM + TEXT_TYPE + COMMA_SEP +
                    NAME + TEXT_TYPE + COMMA_SEP +
                    DURATION + TEXT_TYPE + COMMA_SEP +
                    ALBUM_ID + TEXT_TYPE +
                    ");";
        String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;
        String SQL_CONTROL_ROW_LIST = PLAYLIST.COLUMN_NAME_ID+"=";
        String SQL_SEARCH_LIST = PLAYLIST.TITLE+"==";
        String SQL_CONTROL_ROW_SONG = PLAYSONGS.COLUMN_NAME_ID+"=";
    }
    public @interface PLAYLIST {
        String TABLE_NAME = "queue";
        String TEXT_TYPE = " TEXT";
        String TITLE = "title";
        String COLUMN_NAME_ID = "id";
        String COMMA_SEP = ",";

        String[] ALL_KEYS = {PLAYLIST.COLUMN_NAME_ID, PLAYLIST.TITLE};
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                        TITLE + TEXT_TYPE +
                        ");";
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;
        String SQL_CONTROL_ROW_LIST = PLAYLIST.COLUMN_NAME_ID+"=";
        String SQL_SEARCH_LIST = PLAYLIST.TITLE+"==";
        String SQL_CONTROL_ROW_SONG = PLAYSONGS.COLUMN_NAME_ID+"=";
    }
    public  @interface PLAYSONGS {
        String TEXT_TYPE = " TEXT";
        String COMMA_SEP = ",";
        String TABLE_NAME = "playlistsongs";
        String COLUMN_NAME_ID = "id";
        String PLAYLIST_ID = "playlist";
        String TITLE = "title";
        String PATH = "PATH";
        String ARTIST = "ARTIST";
        String ALBUM = "ALBUM";
        String NAME = "NAME";
        String ALBUM_ID = "ALBUMID";
        String DURATION = "DURATION";
        String[] ALL_KEYS = new String[]
                {COLUMN_NAME_ID, PLAYLIST_ID, TITLE, PATH, ARTIST, ALBUM, NAME, DURATION, ALBUM_ID};
        String SQL_QUERY_LIST = "SELECT * FROM " + TABLE_NAME;
        String SQL_CONTROL_ROW_LIST = PLAYLIST.COLUMN_NAME_ID+"=";
        String SQL_SEARCH_LIST = PLAYLIST.TITLE+"==";
        String SQL_CONTROL_ROW_SONG = PLAYSONGS.COLUMN_NAME_ID+"=";
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY Database AUTOINCREMENT" + COMMA_SEP +
                        PLAYLIST_ID + TEXT_TYPE + COMMA_SEP +
                        TITLE + TEXT_TYPE + COMMA_SEP +
                        PATH + TEXT_TYPE + COMMA_SEP +
                        ARTIST + TEXT_TYPE + COMMA_SEP +
                        ALBUM + TEXT_TYPE + COMMA_SEP +
                        NAME + TEXT_TYPE + COMMA_SEP +
                        DURATION + TEXT_TYPE + COMMA_SEP +
                        ALBUM_ID + TEXT_TYPE +
                        ");";
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;


    }
}
