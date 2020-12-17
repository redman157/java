package company.ai.musicplayer.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.res.Resources
import android.provider.MediaStore
import company.ai.musicplayer.extensions.toFormattedYear
import company.ai.musicplayer.extensions.toSavedMusic
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.models.Album
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.models.SavedMusic
import company.ai.musicplayer.player.MediaPlayerHolder

object MusicOrg {
    @JvmStatic
    @Suppress("DEPRECATION")
    fun getPathColumn() =
        if (VersioningHelper.isQ()) {
            MediaStore.Audio.AudioColumns.BUCKET_DISPLAY_NAME
        } else {
            MediaStore.Audio.AudioColumns.DATA
        }

    @JvmStatic
    @SuppressLint("InlinedApi")
    fun getMusicCursor(contentResolver: ContentResolver) = contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        arrayOf(
            MediaStore.Audio.AudioColumns.ARTIST, // 0
            MediaStore.Audio.AudioColumns.YEAR, // 1
            MediaStore.Audio.AudioColumns.TRACK, // 2
            MediaStore.Audio.AudioColumns.TITLE, // 3
            MediaStore.Audio.AudioColumns.DISPLAY_NAME, // 4,
            MediaStore.Audio.AudioColumns.DURATION, //5,
            MediaStore.Audio.AudioColumns.ALBUM, // 6
            MediaStore.Audio.AudioColumns.ALBUM_ID, // 7
            MediaStore.Audio.AudioColumns.ALBUM_ARTIST, //8
            getPathColumn(), // 8
            MediaStore.Audio.AudioColumns._ID //9
        ),
        MediaStore.Audio.AudioColumns.IS_MUSIC + " = 1",
        null,
        MediaStore.Audio.Media.DEFAULT_SORT_ORDER
    )

    @JvmStatic
    fun getSongForRestore(savedMusic: SavedMusic?, deviceSongs: MutableList<Music>): Music {
        return deviceSongs.firstOrNull { s ->
            s.artist == savedMusic?.artist && s.title == savedMusic?.title && s.displayName == savedMusic?.displayName
                    && s.year == savedMusic?.year && s.duration == savedMusic.duration && s.album == savedMusic.album
        } ?: deviceSongs.random()
    }

    @JvmStatic
    fun buildSortedArtistAlbums(resources: Resources, artistSongs: List<Music>?): List<Album>{
        val sortedAlbums = mutableListOf<Album>()
        artistSongs?.let {
            try {
                val groupedSongs = it.groupBy { song -> song.album }
                groupedSongs.keys.iterator().forEach { album ->

                    val albumSongs = groupedSongs.getValue(album).toMutableList()
                    albumSongs.sortBy { song -> song.track }

                    sortedAlbums.add(
                        Album(
                            album,
                            albumSongs[0].year.toFormattedYear(resources),
                            albumSongs,
                            albumSongs.map { song -> song.duration }.sum()
                        )
                    )
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            sortedAlbums.sortBy { album -> album.year }
        }
        return sortedAlbums
    }

    fun saveLatestSong(
        latestSong: Music?,
        mediaPlayerHolder: MediaPlayerHolder,
        launchedBy: String){
        val playerPosition = mediaPlayerHolder.playerPosition
        latestSong?.let {music ->
            val toSave = music.toSavedMusic(playerPosition, launchedBy)
            if (mPreferences.latestPlayedSong != toSave) {
                mPreferences.latestPlayedSong = toSave
            }
        }
    }
}