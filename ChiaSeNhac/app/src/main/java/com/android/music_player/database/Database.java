package com.android.music_player.database;

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
        String FILE_NAME = "NAME";
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
        String NAME = "name";
        String MOST = "most";
        String TYPE = "type_name";
        String FAV = "favorite";
        String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + "," +
                    TYPE            + " TEXT "    + "," +
                    NAME            + " TEXT "    + "," +
                    MOST            + " INTEGER " + "," +
                    FAV            + " INTEGER " + ");";
        String DELETE = "DROP TABLE IF EXISTS " +TABLE_NAME;
        String QUERY = "SELECT * FROM " + TABLE_NAME;
    }

    public @interface ALL_PLAY_LISTS {
        String DATABASE_NAME = "all_play_list.db";
        String TABLE_NAME = "play_list";
        String NAME_PLAY_LIST = "name_play_list";
        String ID = "id";
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        NAME_PLAY_LIST + " TEXT "    + ");";
        String QUERY = "SELECT * FROM "+ ALL_PLAY_LISTS.TABLE_NAME;
        String DELETE = "DROP TABLE IF EXISTS " +TABLE_NAME;
    }

    public @interface ALL_MUSIC {
        String DATABASE_NAME = "all_music.db";
        String TABLE_NAME = "song_of_play_list ";
        String COL_NAME_ID = "id ";
        String NAME = "media_id";
        String FAVORITE = "favorite";
        String ID_SONG = "id_song";

        String QUERY = "SELECT * FROM " + TABLE_NAME;
        String DELETE = "DROP TABLE IF EXISTS " +TABLE_NAME;
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        COL_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        NAME        + " TEXT " + ");";
    }

    public @interface MUSIC_OF_PLAY_LIST {
        String NAME_PLAY_LISTS = "name_play_list";
        String NAME_SONGS = "id_songs";
        String ID = "id";
        String DATABASE_NAME = "music_of_play_list.db";
        String TABLE_NAME = "music_of_play_list";
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                        ID             + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
                        NAME_PLAY_LISTS + " TEXT "  + "," +
                        NAME_SONGS      + " TEXT "  + ");";
        String QUERY = "SELECT * FROM " + TABLE_NAME;
        String DELETE = "DROP TABLE IF EXISTS " +TABLE_NAME;
    }
}
