package company.ai.musicplayer.database

import android.annotation.SuppressLint
import android.app.Application
import android.content.res.Resources
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import company.ai.musicplayer.R
import company.ai.musicplayer.models.Album
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.ListsHelper
import company.ai.musicplayer.utils.MusicOrg
import company.ai.musicplayer.utils.VersioningHelper
import kotlinx.coroutines.*
import java.io.File

class MusicViewModel(application: Application): AndroidViewModel(application){

    /**
     * This is the job for all coroutines started by this ViewModel.
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val mViewModelJob = SupervisorJob()
    private val mHandler = CoroutineExceptionHandler{ _, exception ->
        exception.printStackTrace()
        mDeviceMusic.value = null
    }

    fun getSongFromIntent(queriedDisplayName: String) =
        mDeviceMusicList.firstOrNull { s -> s.displayName == queriedDisplayName }

    private val uiDispatcher = Dispatchers.Main
    private val ioDispatcher = Dispatchers.IO + mViewModelJob + mHandler
    private val uiScope = CoroutineScope(uiDispatcher)

    val mDeviceMusic = MutableLiveData<MutableList<Music>?>()

    var mDeviceMusicFiltered: MutableList<Music>? = null

    var mDeviceMusicList = mutableListOf<Music>()
    //keys: artist || value: its songs
    var mDeviceMusicByArtist: Map<String?, List<Music>>? = null

    //keys: album || value: its songs
    var mDeviceMusicByAlbum: Map<String?, List<Music>>? = null

    //keys: artist || value: albums
    var mDeviceAlbumsByArtist: MutableMap<String, List<Album>>? = mutableMapOf()

    //keys: artist || value: songs contained in the folder
    var mDeviceMusicByFolder: Map<String, List<Music>>? = null

    val mRandomMusic get() = mDeviceMusicList.random()

    val mDatabaseSize get() = mDeviceMusicFiltered?.size
    /**
     * Cancel all coroutines when the ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        mViewModelJob.cancel()
    }

    fun cancel() {
        onCleared()
    }

    fun getDeviceMusic() {
        uiScope.launch {
            withContext(ioDispatcher) {
                val music = getMusic(getApplication()) // get music from MediaStore on IO thread
                Log.d("XXX", "file âm thanh: ${music.size} ")
                withContext(uiDispatcher) {
                    Log.d("XXX", "file âm thanh: ${music.size} ")
                    mDeviceMusic.value = music // post values on Main thread
                }
            }
        }
    }

    @SuppressLint("InlinedApi")
    fun queryForMusic(application: Application) =
        try {
            val musicCursor =
                MusicOrg.getMusicCursor(
                    application.contentResolver
                )
            // Query the storage for music files
            musicCursor?.use { cursor ->

                val idIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                val artistIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                val yearIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
                val trackIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TRACK)
                val titleIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                val displayNameIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                val durationIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                val albumIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                val albumIdIndex =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                val relativePathIndex =
                    cursor.getColumnIndexOrThrow(MusicOrg.getPathColumn())

                while (cursor.moveToNext()) {
                    // Now loop through the music files
                    val audioId = cursor.getLong(idIndex)
                    val audioArtist = cursor.getString(artistIndex)
                    val audioYear = cursor.getInt(yearIndex)
                    val audioTrack = cursor.getInt(trackIndex)
                    val audioTitle = cursor.getString(titleIndex)
                    val audioDisplayName = cursor.getString(displayNameIndex)
                    val audioDuration = cursor.getLong(durationIndex)
                    val audioAlbum = cursor.getString(albumIndex)
                    val audioAlbumId = cursor.getString(albumIdIndex)
                    val audioRelativePath = cursor.getString(relativePathIndex)

                    val audioFolderName =
                        if (VersioningHelper.isQ()) {
                            audioRelativePath ?: application.getString(R.string.slash)
                        } else {
                            val returnedPath = File(audioRelativePath).parentFile?.name
                                ?: application.getString(R.string.slash)
                            if (returnedPath != "0") {
                                returnedPath
                            } else {
                                application.getString(
                                    R.string.slash
                                )
                            }
                        }

                    // Add the current music to the list
                    mDeviceMusicList.add(
                        Music(
//                            mId = 1,
                            displayName = audioDisplayName,
                            artist = audioArtist,
                            album = audioAlbum,
                            year = audioYear,
                            track = audioTrack,
                            title = audioTitle,
                            duration = audioDuration,
                            albumID = audioAlbumId.toLong(),
                            relativePath = audioFolderName,
                            id = audioId
                        )
                    )

                }
            }
            mDeviceMusicList

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    private fun buildLibrary(resources: Resources){
        // Removing duplicates by comparing everything except path which is different
        // if the same song is hold in different paths
        mDeviceMusicFiltered = mDeviceMusicList.distinctBy {
            it.artist to it.year to it.track to it.title to it.duration to it.album
        }.toMutableList()

        mDeviceMusicFiltered?.let { dsf ->
            // group music by artist
            mDeviceMusicByArtist = dsf.groupBy { it.artist!!.split(",",";")[0] }
            mDeviceMusicByAlbum = dsf.groupBy { it.album }
            mDeviceMusicByFolder = dsf.groupBy { it.relativePath!! }
        }
        // group artists songs by albums
        mDeviceMusicByArtist?.keys?.iterator()?.iterator()?.forEach {artist ->
            artist?.let{artistKey ->
                mDeviceAlbumsByArtist?.set(
                    artistKey,
                    MusicOrg.buildSortedArtistAlbums(resources, mDeviceMusicByArtist?.getValue(artist))
                )
            }
        }

    }

    private fun getMusic(application: Application): MutableList<Music> {
        queryForMusic(application)?.let { fm ->
            mDeviceMusicList = fm
        }
        buildLibrary(application.resources)
        return mDeviceMusicList
    }

    fun syncMusic(application: Application){
        mViewModelJob.cancel()
        mDeviceMusic.value = null
        mDeviceMusicList.clear()
        mDeviceMusicList = mutableListOf<Music>()
        mDeviceMusic.value = getMusic(application)
        ListsHelper.getRecentlyMusicAdd(mDeviceMusic.value!!)
    }
}