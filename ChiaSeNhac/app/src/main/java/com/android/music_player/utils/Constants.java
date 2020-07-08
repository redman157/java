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
        String TYPE = "type";
        String TYPE_DIALOG = "TypeDialog";
        String OPEN_SLIDING_PANEL = "OpenSlidingPanel";
    }
    public @interface VALUE {
        int MAX_SLIDERS = 5;
        String MOST_MUSIC = "Music";
        String MOST_PLAY_LIST = "PlayList";
        String NAME_PLAYLIST = "NAME_PLAYLIST";
        String SYNC = "isSync";
    }

    public @interface PREFERENCES{
        String CURRENT_MUSIC = "current_music";
        String SAVE_ALBUM_ID = "SaveAlbumId";
        String IS_ROOT = "IsRoot";
        String KEY = "key";
        String TOTAL_SONGS = "TotalSongs";
        String AUDIO_SESSION_ID = "AudioSessionId";
        String TYPE = "type";
        String STATE = "State";
        String ACCENT_COLOR = "AccentColor";
        String EXCLUDE_SHORT_SOUNDS = "ExcludeShortSounds";
        String EXCLUDE_WHATS_APP_SOUNDS = "ExcludeWhatsAppSounds";
        String TURN_EQUALIZER = "TurnEqualizer";
        String CURRENT_EQUALIZER_PROFILE = "CurrentEqualizerProfile";
        String BASS_LEVEL = "BassLevel";
        String VIRTUAL_LEVEL = "VirtualLevel";
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
        String WHITE = "White";
        String BLACK = "Black";
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
