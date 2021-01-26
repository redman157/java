package company.ai.musicplayer.database

import androidx.room.*
import company.ai.musicplayer.models.Director
import company.ai.musicplayer.models.Music

@Dao
abstract class DirectorDao: BaseDao<Director>() {

    @Query("SELECT * FROM director ORDER BY name_music")
    abstract suspend fun getListMusic() : MutableList<Director>

    @Query("SELECT count FROM director WHERE name_music = :nameMusic AND type = :type")
    abstract suspend fun getCountMusic(nameMusic: String, type: String): Int

    @Query("SELECT count FROM director WHERE name_music = :nameMusic AND type = :type ")
    abstract suspend fun getCountPlayList(nameMusic: String, type: String): Int

}