package com.android.music_player.utils;

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

        String CREATE_TABLE =
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

    public @interface STATISTIC {
        String DATABASE_NAME = "statistic.db";
        String TABLE_NAME = "statistic";
        String ID = "id";
        String FILE_NAME = "file_name";
        String MOST_SONG = "most_song";

        String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                    FILE_NAME + " TEXT "    + "," +
                    MOST_SONG + " INTEGER " + ");";

        String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        String QUERY = "SELECT * FROM " + TABLE_NAME;

    }

    public @interface ALL_PLAY_LISTS {
        String DATABASE_NAME = "play_list.db";
        String TABLE_NAME = "play_list";
        String NAME_PLAY_LIST = "name_play_list";
        String ID = "id";
        String MOST_PLAYLIST = "most_playlist";
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        MOST_PLAYLIST + " INTEGER " + "," +
                        NAME_PLAY_LIST + " TEXT "    + ");";
        String QUERY = "SELECT * FROM "+ ALL_PLAY_LISTS.TABLE_NAME;
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
        String ID_SONG = "id_song";
        String TIME = "time";

        String QUERY = "SELECT * FROM " + TABLE_NAME;
        String DELETE = "DELETE FROM " + TABLE_NAME;
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        COL_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        NAME_SONG     + " TEXT "    + "," +
                        PATH          + " TEXT "    + "," +
                        ARTIST        + " TEXT "    + "," +
                        ALBUM         + " TEXT "    + "," +
                        ALBUM_ID      + " TEXT "    + "," +
                        FILE_NAME     + " TEXT "    + "," +
                        ID_SONG       + " TEXT "    + "," +
                        FAVORITE      + " INTEGER " + "," +
                        TIME          + " INTEGER " + ");";
    }

    public @interface RELATION_SONGS{
        String NAME_PLAY_LIST = "name_play_list";
        String ID_SONGS = "id_songs";
        String ID = "id";
        String MOST = "most_play_list";
        String DATABASE_NAME = "relation_songs.db";
        String TABLE_NAME = "relation_songs";
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        ID             + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        MOST           + " INTEGER "  + "," +
                        NAME_PLAY_LIST + " STRING "  + "," +
                        ID_SONGS       + " INTEGER " + ");";
        String QUERY = "SELECT * FROM " + TABLE_NAME;
        String DELETE = "DELETE FROM " + TABLE_NAME;
    }
}
