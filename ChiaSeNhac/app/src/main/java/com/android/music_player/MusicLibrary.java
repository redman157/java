package com.android.music_player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.android.music_player.models.SongModel;
import com.android.music_player.utils.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MusicLibrary {
    private static final TreeMap<String, MediaMetadataCompat> music = new TreeMap<>();
    private static final HashMap<String, Integer> albumRes = new HashMap<>();
    private static final HashMap<String, String> musicFileName = new HashMap<>();

   /* static {
        createMediaMetadataCompat(
                "Jazz_In_Paris",
                "Jazz in Paris",
                "Media Right Productions",
                "Jazz & Blues",
                "Jazz",
                103,
                TimeUnit.SECONDS,
                "jazz_in_paris.mp3",
                R.drawable.album_jazz_blues,
                "album_jazz_blues");
        createMediaMetadataCompat(
                "The_Coldest_Shoulder",
                "The Coldest Shoulder",
                "The 126ers",
                "Youtube Audio Library Rock 2",
                "Rock",
                160,
                TimeUnit.SECONDS,
                "the_coldest_shoulder.mp3",
                R.drawable.album_youtube_audio_library_rock_2,
                "album_youtube_audio_library_rock_2");
    }*/

    public static String getRoot() {
        return "root";
    }

    public static String getAlbumArtUri(String albumArtResName) {
        return ImageUtils.getSongUri(Long.valueOf(albumArtResName)).getPath();
    }

    public static String getMusicFilename(String mediaId) {
        return musicFileName.containsKey(mediaId) ? musicFileName.get(mediaId) : null;
    }

    public static int getAlbumRes(String mediaId) {
        return albumRes.containsKey(mediaId) ? albumRes.get(mediaId) : 0;
    }

    public static Bitmap getAlbumBitmap(Context context, String mediaId) {
        return BitmapFactory.decodeResource(context.getResources(),
                MusicLibrary.getAlbumRes(mediaId));
    }

    public static List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();
        for (MediaMetadataCompat metadata : music.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public static MediaMetadataCompat getMetadata(Context context, String songName) {
        MediaMetadataCompat metadataWithoutBitmap = music.get(songName);
        Bitmap albumArt = ImageUtils.getAlbumArt(context, Long.valueOf(albumRes.get(songName)));

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
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
            albumRes.put(song.getSongName(), Integer.valueOf(song.getAlbumID()));
            musicFileName.put(song.getSongName(), song.getPath());
    }

}
