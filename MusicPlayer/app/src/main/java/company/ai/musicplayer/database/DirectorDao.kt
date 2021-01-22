package company.ai.musicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import company.ai.musicplayer.models.Director
import company.ai.musicplayer.models.Music

@Dao
abstract class DirectorDao: BaseDao<Director>() {
    
    @Query("SELECT * FROM director ORDER BY name_music")
    abstract fun getListMusic() : MutableList<Director>
}