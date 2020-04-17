package com.droidheat.musicplayer.database;

public class Database {
    public @interface CATEGORY{
        String DATABASE_NAME = "categories.db";
        String TABLE_NAME = "categories";
        String COLUMN_NAME_ID = "id";
        String CATEGORY = "category";
        String VOTES = "votes";
        String NAME_CATEGORY = "name_category";
        String PATH = "path";
        String ARTIST = "artist";
        String ALBUM = "album";
        String FILE_NAME = "FILE_NAME";
        String ALBUM_ID = "album_id";
        String FAKE_PATH = "fake_path";

        String TIME = "time";

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                        CATEGORY       + " TEXT "    + "," +
                        VOTES          + " INTEGER " + "," +
                        NAME_CATEGORY  + " TEXT "    + "," +
                        PATH           + " TEXT "    + "," +
                        ARTIST         + " TEXT "    + "," +
                        ALBUM          + " TEXT "    + "," +
                        ALBUM_ID       + " TEXT "    + "," +
                        FILE_NAME      + " TEXT "    + "," +
                        TIME           + " INTEGER " + "," +
                        FAKE_PATH      + " TEXT "    + ");";
        String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        String QUERY = "SELECT * FROM " + TABLE_NAME;
    }

    public @interface FAVOURITE{
        String DATABASE_NAME = "Favs.db";
        String TABLE_NAME = "fav";
        String COLUMN_NAME_ID = "id";
        String TITLE = "NAME_PLAYLIST";
        String PATH = "PATH";
        String ARTIST = "ARTIST";
        String ALBUM = "ALBUM";
        String NAME = "FILE_NAME";
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
        String SQL_CONTROL_ROW_LIST = ALL_PLAY_LISTS.COL_ID +"=";
        String SQL_SEARCH_LIST = ALL_PLAY_LISTS.COL_NAME_PLAY_LIST +"==";
        String SQL_CONTROL_ROW_SONG = SONGS_OF_PLAY_LIST.COL_NAME_ID +"=";
    }

    public @interface ALL_PLAY_LISTS {
        String DATABASE_NAME = "play_list.db";
        String TABLE_NAME = "play_list";
        String COL_NAME_PLAY_LIST = "name_play_list";
        String COL_ID = "id";
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        COL_NAME_PLAY_LIST + " TEXT " + ");";
        String QUERY = "SELECT * FORM "+ ALL_PLAY_LISTS.TABLE_NAME;
        String DELETE = "DELETE FROM " + TABLE_NAME;
    }

    public @interface SONGS_OF_PLAY_LIST {
        String DATABASE_NAME = "song_of_play_list.db";
        String TABLE_NAME = "song_of_play_list ";
        String COL_NAME_ID = "id ";

        String NAME_SONG = "name_song";
        String PATH = "path";
        String ARTIST = "artist";
        String ALBUM = "album";
        String FILE_NAME = "file_name";
        String ALBUM_ID = "album_id";
        String FAVORITE = "favorite";
        String ID_SONG = "id";
        String TIME = "time";


        String QUERY = "SELECT * FROM " + TABLE_NAME;
        String DELETE = "DELETE FROM " + TABLE_NAME;
        String SQL_CREATE_ENTRIES =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        COL_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        NAME_SONG   + " TEXT "    + "," +
                        PATH        + " TEXT "    + "," +
                        ARTIST      + " TEXT "    + "," +
                        ALBUM       + " TEXT "    + "," +
                        ALBUM_ID    + " TEXT "    + "," +
                        FILE_NAME   + " TEXT "    + "," +
                        ID_SONG     + " TEXT "    + "," +
                        FAVORITE    + " INTEGER " + "," +
                        TIME        + " INTEGER " + ");";
    }
}
