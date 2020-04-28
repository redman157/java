package com.android.music_player.utils;

public class Constants {
    public @interface NOTIFICATION {
        String PREVIOUS = "previous";
        String NEXT = "next";
        String PAUSE = "pause";
        String PLAY = "play";
        String REPEAT = "repeat";
    }

    public @interface INTENT{
        String SONG_CONTINUE = "SongContinue";
        String IS_PLAY_ACTIVITY = "isPlayActivity";
        String POSITION = "position";
        String POSITION_SONG = "position_song";
        String TYPE = "type";
        String IS_PLAY_MEDIA_SERVICE = "isPlayMedia";
        String IS_PLAY_MEDIA_NOTIFICATION = "isPlayingNotification";
        String NOTI_SERVICE_TO_ACTIVITY = "ServiceToActivity";
        String NEXT_TO_SERVICE = "nextToService";
        String PREVIOUS_TO_SERVICE = "previousToService";
        String IS_SHUFFLE = "isShuffle";
        String IS_REPEAT = "isRepeat";
        String IS_PLAY = "isPlay";
        String CHANGE_MUSIC = "ChangeMusic";
        String TYPE_MUSIC = "TypeMusic";
    }
    public @interface VALUE {
        int MAX_SLIDERS = 5;
        String SONG = "Song";
        String PLAY_LIST = "PlayList";
        String ID = "ID";
        String NAME_PLAYLIST = "NAME_PLAYLIST";
        String SYNC = "isSync";
        String TASK = "task";
        String ALL_NEW_SONGS = "all_new_songs";
        String NEW_SONGS = "new_songs";
        String ALL_SONGS = "all_songs";
        String PLAYLIST_DB = "playlist.db";
        String CATEGORIES_DB = "categories.db";
        String FAVS_DB = "Favs.db";
        String PLAYLISTSONGS_DB = "playlistsongs.db";
        String INPUT_METHOD = "input_method";
    }

    public @interface PREFERENCES{
        String POSITION_SONG = "position_song";
        String POSITION_MAIN = "position_main";
        String SaveAlbumID = "SaveAlbumID";
        String KEY = "key";
        String TOTAL_SONGS = "total_song";
        String AUDIO_SESSION_ID = "AudioSessionId";
        String TYPE = "type";
        String POSITION = "position";
        String STATE = "State";

        String ACCENT_COLOR = "AccentColor";
        String EXCLUDE_SHORT_SOUNDS = "ExcludeShortSounds";
        String EXCLUDE_WHATS_APP_SOUNDS = "ExcludeWhatsAppSounds";
        String TURN_EQUALIZER = "TurnEqualizer";
        String CURRENT_EQUALIZER_PROFILE = "CurrentEqualizerProfile";
        String BASS_LEVEL = "BassLevel";
        String VIRTUAL_LEVEL = "VirtualLevel";
    }

    public @interface ACTION{
        String CHANGE_SONG = "ChangeSong";
        String STOP = "com.android.music_player.action.STOP";
        String PLAY = "com.android.music_player.action.PLAY";
        String PAUSE = "com.android.music_player.action.PAUSE";
        String REPEAT = "com.android.music_player.action.REPEAT";
        String NEXT = "com.android.music_player.action.TRACK_NEXT";
        String PREVIOUS = "com.android.music_player.action.TRACK_PREV";
        String CLOSE_NOTIFICATION = "com.android.music_player.action.CLOSE_NOTIFICATION";
        String SEEK = "com.android.music_player.action.SEEK";
        String IS_PLAY = "com.android.music_player.action.IS_PLAY";
        String SHUFFLE = "com.android.music_player.action.SHUFFLE";
        String BROADCAST_STOP_AUDIO = "com.android.music_player.action.StopAudio";
        String BROADCAST_PLAY_NEW_AUDIO = "com.android.music_player.action.PlayNewAudio";
        String BROADCAST_SEEK_BAR = "com.android.music_player.action.seekprogress";
        String BROADCAST_PLAY_PAUSE = "com.android.music_player.action.broadcastbutton";
        String BROADCAST_RESET_AUDIO = "com.android.music_player.action.ResetAudio";
    }

    public @interface COLOR{
        String ORANGE = "Orange";
        String RED = "Red";
        String CYAN = "Cyan";
        String GREEN = "Green";
        String YELLOW = "Yellow";
        String PINK = "Pink";
        String PURPLE = "Purple";
        String GREY = "Grey";
    }

    public @interface MENU{
        String Sync_Music = "Sync Music";
        String Set_Sleep_Timer = "Set Sleep Timer";
        String Change_Theme = "Change Theme";
        String Equalizer = "Equalizer";
        String Settings = "Settings";
    }
}
