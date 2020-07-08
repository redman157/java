package com.android.music_player.managers;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.ArrayMap;

import com.android.music_player.models.MusicModel;
import com.android.music_player.utils.ImageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MusicLibrary {
    public static MusicLibrary instance;

    public static MusicLibrary getInstance() {
        if (instance == null){
            instance = new MusicLibrary();
        }
        return instance;
    }
    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private  MusicLibrary() {
    }

    public static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    public static final HashMap<String, Integer> albumID = new HashMap<>();
    public static final HashMap<String, String> fileName = new HashMap<>();
    public static final Set<MediaMetadataCompat> metadata = new HashSet<>();
    public static final Set<MusicModel> info = new HashSet<>();

    public ArrayList<MediaMetadataCompat> queue = new ArrayList<>();
    public static final Map<String, ArrayList<MusicModel>> album = new HashMap<>();
    public static final Map<String, ArrayList<MusicModel>> artist = new HashMap<>();
    public static final Map<String, ArrayList<MusicModel>> folder = new HashMap<>();
    public static final Map<String , MusicModel> model = new HashMap<>();
    public static final Map<String, List<MediaSessionCompat.QueueItem>> mPlayingQueue =
            new ArrayMap<>();
    public static String MEDIA_ID_ROOT = "__Root__";
    public static String MEDIA_ID_EMPTY_ROOT = "__Empty_Root__";

    public static void clear(){
        music.clear();
        albumID.clear();
        fileName.clear();
        info.clear();
        model.clear();
    }

    public static int getSize(){
        return music.size();
    }

    public static String getAlbumArtUri(String albumArtResName) {
        return ImageHelper.getSongUri(Long.valueOf(albumArtResName)).getPath();
    }

    public static String getMusicFilename(String mediaId) {
        return fileName.containsKey(mediaId) ? fileName.get(mediaId) : null;
    }

    public static int getAlbumRes(String mediaId) {
        return albumID.containsKey(mediaId) ? albumID.get(mediaId) : 0;
    }

    public static int getPosition(List<MediaSessionCompat.QueueItem> music, String songName){
        for (int i = 0; i < music.size(); i++){
            if (songName.equals(music.get(i).getDescription().getMediaId())){
                return i;
            }
        }
        return  -1;
    }

    // khi change album sẽ có 1 list mới thì mình sẽ edit ở đây
    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(
                    metadata.getDescription(),
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
            result.add(mediaItem);
        }
        return result;
    }

    public static List<MediaBrowserCompat.MediaItem> getAlbumItems(ArrayList<String> albums) {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (int i = 0 ; i < albums.size();i ++){
            MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(
                    music.get(albums.get(i)).getDescription(),
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
            result.add(mediaItem);
        }
        return result;
    }

    public static void createMediaMetadataCompat(MusicModel song ) {
        music.put(
                song.getSongName(),
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.getSongName())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.getAlbum())
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtist())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getTime())
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, song.getGenres())
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                getAlbumArtUri(song.getAlbumID()))
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                getAlbumArtUri(song.getAlbumID()))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getSongName())
                        .build());
        albumID.put(song.getSongName(), Integer.valueOf(song.getAlbumID()));
        fileName.put(song.getSongName(), song.getPath());
        info.add(song);
        model.put(song.getSongName(), song);
    }
}
