package company.ai.musicplayer.database

import androidx.annotation.WorkerThread
import company.ai.musicplayer.models.Music
import kotlinx.coroutines.flow.Flow

/*
class MusicRepository(private val musicDao: MusicDao) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allWords: Flow<List<Music>> = musicDao.getAlphabetizedMusic()
    @WorkerThread
    suspend fun insert(music: Music){
        musicDao.insert(music)
    }
}*/
