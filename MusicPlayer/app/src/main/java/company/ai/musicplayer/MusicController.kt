package company.ai.musicplayer
import androidx.room.*
import company.ai.musicplayer.models.Music

@Dao
interface MusicController {
    @Insert
    fun insertValue(vararg music: Music)

    @Delete
    fun delete(music: Music)

    @Query("SELECT * FROM musics")
    fun getAll(): List<Music>

    @Update
    fun update(music: Music)

    @Query("SELECT most FROM common WHERE most = (SELECT MAX(most) FROM common)")
    fun getMusicMost(): Music


}