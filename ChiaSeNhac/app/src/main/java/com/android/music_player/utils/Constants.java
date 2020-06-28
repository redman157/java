package com.android.music_player.utils;

import android.support.v4.media.MediaMetadataCompat;

public class Constants {
    public @interface NOTIFICATION {
        String PREVIOUS = "previous";
        String NEXT = "next";
        String PAUSE = "pause";
        String PLAY = "play";
        String REPEAT = "repeat";
    }

    public @interface INTENT{
        String AUTO_PLAY = "AutoPlay";
        String SONG_CONTINUE = "SongContinue";
        String SONG_NAME = "SongName";
        String CURR_POS = "CurrentPosition";
        String CHOOSE_POS = "ChoosePosition";
        String POSITION_SONG = "position_song";
        String TYPE = "type";
        String IS_PLAY_MEDIA_SERVICE = "isPlayMedia";
        String IS_PLAY_MEDIA_NOTIFICATION = "isPlayingNotification";
        String NEXT_TO_SERVICE = "nextToService";
        String IS_SHUFFLE = "isShuffle";
        String IS_REPEAT = "isRepeat";
        String IS_PLAY = "isPlay";
        String CHANGE_MUSIC = "ChangeMusic";
        String TYPE_MUSIC = "TypeMusic";
    }
    public @interface VALUE {
        int MAX_SLIDERS = 5;
        String MOST_MUSIC = "Music";
        String MOST_PLAY_LIST = "PlayList";
        String ID = "ID";
        String SHUFFLE = "shuffle";
        String NAME_PLAYLIST = "NAME_PLAYLIST";
        String SYNC = "isSync";
        String TASK = "task";
        String ALL_NEW_SONGS = "all_new_songs";
        String NEW_SONGS = "new_songs";
        String ALL_SONGS = "all_songs";
    }

    public @interface PREFERENCES{
        String CURRENT_MUSIC = "current_music";
        String CURRENT_SONG = "current_song";
        String POSITION_SONG = "position_song";
        String POSITION_MAIN = "position_main";
        String SAVE_ALBUM_ID = "SaveAlbumId";
        String KEY = "key";
        String TOTAL_SONGS = "TotalSongs";
        String AUDIO_SESSION_ID = "AudioSessionId";
        String TYPE = "type";
        String POSITION = "position";
        String STATE = "State";
        String LAST_POS = "LastPosition";
        String ACCENT_COLOR = "AccentColor";
        String EXCLUDE_SHORT_SOUNDS = "ExcludeShortSounds";
        String EXCLUDE_WHATS_APP_SOUNDS = "ExcludeWhatsAppSounds";
        String TURN_EQUALIZER = "TurnEqualizer";
        String CURRENT_EQUALIZER_PROFILE = "CurrentEqualizerProfile";
        String BASS_LEVEL = "BassLevel";
        String VIRTUAL_LEVEL = "VirtualLevel";
    }

    public @interface ACTION{
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

    public @interface METADATA{
        String Title = MediaMetadataCompat.METADATA_KEY_TITLE;
        String Artist = MediaMetadataCompat.METADATA_KEY_ARTIST;
        String Album = MediaMetadataCompat.METADATA_KEY_ALBUM;
        String AlbumID = MediaMetadataCompat.METADATA_KEY_ALBUM_ART;
        String Genre = MediaMetadataCompat.METADATA_KEY_GENRE;
        String MediaID = MediaMetadataCompat.METADATA_KEY_MEDIA_ID;
        String Duration = MediaMetadataCompat.METADATA_KEY_DURATION;
    }
}
