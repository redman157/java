package com.android.music_player.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.android.music_player.models.SongModel;
import com.android.music_player.utils.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MusicLibrary {
    public static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    public static final HashMap<String, Integer> albumID = new HashMap<>();
    public static final HashMap<String, String> fileName = new HashMap<>();
    public static final Set<SongModel> info = new HashSet<>();
    public static final Map<String, ArrayList<SongModel>> album = new HashMap<>();
    public static final Map<String, ArrayList<SongModel>> artist = new HashMap<>();
    public static final Map<String, ArrayList<SongModel>> folder = new HashMap<>();
    public static final Map<String , SongModel> model = new HashMap<>();
    public static String getRoot() {
        return "root";
    }

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
        return ImageUtils.getSongUri(Long.valueOf(albumArtResName)).getPath();
    }

    public static String getMusicFilename(String mediaId) {
        return fileName.containsKey(mediaId) ? fileName.get(mediaId) : null;
    }

    public static int getAlbumRes(String mediaId) {
        return albumID.containsKey(mediaId) ? albumID.get(mediaId) : 0;
    }

    public static MediaMetadataCompat getCurrentMusic(String mediaId){
        return music.containsKey(mediaId) ? music.get(mediaId):  null;
    }

    public static int getPosition(String songName){
        if (music.containsKey(songName)){
            for (int i = 0; i < music.size(); i++){
                if (songName.equals(music.keySet().toArray()[i])){
                    return i;
                }
            }
        }
        return -1;
    }

    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
        return BitmapFactory.decodeResource(context.getResources(),
                MusicLibrary.getAlbumRes(mediaId));
    }

    // khi change album sẽ có 1 list mới thì mình sẽ edit ở đây
    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(new MediaBrowserCompat.MediaItem(
                metadata.getDescription(),
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static void setMediaShuffle(Context context, List<MediaBrowserCompat.MediaItem> mediaItems){
        MusicManager.getInstance().setContext(context);
        Collections.shuffle(mediaItems);
        for (int i = 0; i < mediaItems.size(); i++){
            if (mediaItems.get(i).getDescription().getMediaId().equals(MusicManager.getInstance().getCurrentMusic())){
                mediaItems.remove(i);
                mediaItems.add(0, mediaItems.get(i));
            }
        }
    }

    public static void setMediaDefault(List<MediaBrowserCompat.MediaItem> mediaItems){
        mediaItems.clear();

    }

    public static ArrayList<PlaybackStateCompat> getRepeatList(){
        ArrayList<PlaybackStateCompat> list = new ArrayList<>();
        list.add(PlaybackStateCompat.fromPlaybackState(PlaybackStateCompat.REPEAT_MODE_NONE));
        list.add(PlaybackStateCompat.fromPlaybackState(PlaybackStateCompat.REPEAT_MODE_ALL));
        list.add(PlaybackStateCompat.fromPlaybackState(PlaybackStateCompat.REPEAT_MODE_ONE));
        return list;
    }

    public static MediaMetadataCompat getMetadata(Context context, String songName) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(songName);
        Bitmap albumArt = ImageUtils.getAlbumArt(context, Long.valueOf(albumID.get(songName)));

        // Since MediaMetadataCompat is immutable, we need to create a copy to assignData the album art.
        // We don't assignData it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    public static void createMediaMetadataCompat(SongModel song) {
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
