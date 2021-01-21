package company.ai.musicplayer.database
import androidx.lifecycle.LiveData
import androidx.room.*
import company.ai.musicplayer.models.Music
import kotlinx.coroutines.flow.Flow

/*
@Dao
interface MusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(music: Music)

    @Query("SELECT * FROM music_tb ORDER BY name_music ASC")
    fun getAlphabetizedMusic(): LiveData<List<Music>>

    @Delete
    suspend fun delete(nameMusic: String)

    @Query("DELETE FROM music_tb")
    fun deleteTable()

    @Query("SELECT * FROM music_tb WHERE name_music = :nameMusic")
    fun getMusic(nameMusic: String): Flow<Music>

//    @Update
    suspend fun update(music: Music)

//    @Query("SELECT most FROM common WHERE most = (SELECT MAX(most) FROM common)")
    fun getMusicMost(): Flow<Music>
}
*/
