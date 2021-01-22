package company.ai.musicplayer.database
import androidx.lifecycle.LiveData
import androidx.room.*
import company.ai.musicplayer.models.Music
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: Music)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(musics: MutableList<Music>)

    @Query("SELECT * FROM music_tb ORDER BY name_music ASC")
    suspend fun getAlphabetizedMusic(): MutableList<Music>

    @Delete
    suspend fun delete(music: Music)

    @Query("DELETE FROM music_tb")
    suspend fun deleteTable()

    @Query("SELECT * FROM music_tb WHERE name_music = :nameMusic")
    suspend fun getMusic(nameMusic: String): Music
}
