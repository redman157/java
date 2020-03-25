package com.droidheat.musicplayer;

import java.util.Timer;

public class Constants {
    public @interface MANIFESTS {
        String READ_EXTERNAL_STORAGE  = "android.permission.READ_EXTERNAL_STORAGE";
    }

    public @interface NOTIFICATION {
        String IS_PLAYING = "is_playing";
        String PREVIOUS = "previous";
        String NEXT = "next";
        String PAUSE = "pause";
        String PLAY = "play";
        String SEEK_POS = "seekpos";
        String IS_PLAYING_STATUS = "is_playing_status";
    }
    public @interface VALUE {
        String SYNC = "sync";
        String TASK = "task";
        String ALL_NEW_SONGS = "all_new_songs";
        String NEW_SONGS = "new_songs";
        String ALL_SONGS = "all_songs";
        String PLAYLIST_DB = "playlist.db";
        String CATEGORIES_DB = "categories.db";
        String FAVS_DB = "Favs.db";
        String PLAYLISTSONGS_DB = "playlistsongs.db";
        String INPUT_METHOD = "input_method";
        String TYPE = "type";
        String POSITION = "position";

    }

    public @interface TAG{
        String SplashActivityLog = "SplashActivityLog";
        String SplashActivityAsyncTaskLog = "SplashActivityAsyncTaskLog";
        String SongsManagerConsole = "SongsManagerConsole";
    }

    public @interface REQUEST_CODE{
        int READ_EXTERNAL_STORAGE = 1;
        int PREVIOUS = 103;
        int NEXT = 102;
        int PAUSE = 101;
        int PLAY = 100;
        int REPEAT = 104;

    }


    public @interface PREFERENCES{
        String persistentNotificationPref = "persistentNotificationPref";
        String audio_session_id = "audio_session_id";
        String CURRENT_MEDIA = "CurrentMedia";
        String TYPE = "type";
        String POSITION = "position";
        String state = "state";
        String HOME_ARTIST = "HOME_ARTIST";
        String accentColor = "accentColor";
        String excludeShortSounds = "excludeShortSounds";
        String excludeWhatsAppSounds = "excludeWhatsAppSounds";
        String song_position = "song_position";
        String repeat = "repeat";
        String turnEqualizer = "turnEqualizer";
        String currentEqProfile = "currentEqProfile";
        String bassLevel = "bassLevel";
        String vzLevel = "vzLevel";

        String MUSIC_ID = "musicID";
        String TITLE = "title";
        String PATH = "path";
        String ARTIST = "artist";
        String ALBUM = "album";
        String NAME = "name";
        String DURATION = "duration";
        String ALBUMID = "albumid";
        String RAW_PATH = "raw_path";
        String DURATION_IN_MS = " durationInMS";
    }

    public @interface ACTION{
        String STOP = "com.droidheat.musicplayer.action.CLOSE";
        String ACTION_PERSISTENT_NOTIFICATION = "com.droidheat.musicplayer.action.PERSISTENT_NOTIFICATION";
        String PLAY = "com.droidheat.musicplayer.action.PLAY";
        String PAUSE = "com.droidheat.musicplayer.action.PLAY_PAUSE";
        String REPEAT = "com.droidheat.musicplayer.action.REPEAT";
        String NEXT = "com.droidheat.musicplayer.action.TRACK_NEXT";
        String PREVIOUS = "com.droidheat.musicplayer.action.TRACK_PREV";

        String BROADCAST_STOP_AUDIO = "com.droidheat.musicplayer.action.StopAudio";
        String BROADCAST_PLAY_NEW_AUDIO = "com.droidheat.musicplayer.action.PlayNewAudio";
        String BROADCAST_SEEK_BAR = "com.droidheat.musicplayer.action.seekprogress";
        String BROADCAST_PLAY_PAUSE = "com.droidheat.musicplayer.action.broadcastbutton";
        String BROADCAST_RESET_AUDIO = "com.droidheat.musicplayer.action.ResetAudio";
    }

    public @interface COLOR{
        String orange = "orange";
        String red = "red";
        String cyan = "cyan";
        String green = "green";
        String yellow = "yellow";
        String pink = "pink";
        String purple = "purple";
        String grey = "grey";
    }

    public @interface MENU{
        String Sync_Music = "Sync Music";
        String Set_Sleep_Timer = "Set Sleep Timer";
        String Change_Theme = "Change Theme";
        String Equalizer = "Equalizer";
        String Settings = "Settings";
    }
}
