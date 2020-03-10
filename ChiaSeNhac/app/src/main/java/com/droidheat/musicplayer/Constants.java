package com.droidheat.musicplayer;

public class Constants {
    public @interface MANIFESTS {
        String READ_EXTERNAL_STORAGE  = "android.permission.READ_EXTERNAL_STORAGE";
    }

    public @interface VALUE {
        String SYNC = "sync";
        String TASK = "task";

        String PLAYLIST_DB = "playlist.db";
        String CATEGORIES_DB = "categories.db";
        String FAVS_DB = "Favs.db";
        String PLAYLISTSONGS_DB = "playlistsongs.db";
        String INPUT_METHOD = "input_method";


    }

    public @interface TAG{
        String SplashActivityLog = "SplashActivityLog";
        String SplashActivityAsyncTaskLog = "SplashActivityAsyncTaskLog";
        String SongsManagerConsole = "SongsManagerConsole";
    }

    public @interface REQUEST_CODE{
        int READ_EXTERNAL_STORAGE = 1;
    }

    public @interface PREFERENCES{
        String HOME_ARTIST = "HOME_ARTIST";

        String excludeShortSounds = "excludeShortSounds";
        String excludeWhatsAppSounds = "excludeWhatsAppSounds";
        String song_position = "song_position";
        String repeat = "repeat";
        String turnEqualizer = "turnEqualizer";
        String currentEqProfile = "currentEqProfile";
        String bassLevel = "bassLevel";
        String vzLevel = "vzLevel";
        String persistentNotificationPref = "persistentNotificationPref";
        String AUDIO_SESSION_ID =  "AUDIO_SESSION_ID";
        String MUSIC_ID = "musicID";
        String TITLE = "title";
        String PATH = "PATH";
        String ARTIST = "ARTIST";
        String ALBUM = "ALBUM";
        String NAME = "NAME";
        String DURATION = "DURATION";
        String ALBUMID = "ALBUMID";
        String RAW_PATH = "RAW_PATH";
        String DURATION_IN_MS = " DURATION_IN_MS";
    }

    public @interface ACTION{
        String ACTION_CLOSE = "com.droidheat.musicplayer.action.CLOSE";
        String ACTION_PERSISTENT_NOTIFICATION = "com.droidheat.musicplayer.action.PERSISTENT_NOTIFICATION";
        String ACTION_PLAY = "com.droidheat.musicplayer.action.PLAY";
        String ACTION_PLAY_PAUSE = "com.droidheat.musicplayer.action.PLAY_PAUSE";
        String ACTION_REPEAT = "com.droidheat.musicplayer.action.REPEAT";
        String ACTION_TRACK_NEXT = "com.droidheat.musicplayer.action.TRACK_NEXT";
        String ACTION_TRACK_PREV = "com.droidheat.musicplayer.action.TRACK_PREV";
    }
}
