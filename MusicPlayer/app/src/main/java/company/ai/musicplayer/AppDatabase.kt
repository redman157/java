package company.ai.musicplayer

import androidx.room.Database
import androidx.room.RoomDatabase
import company.ai.musicplayer.models.Common
import company.ai.musicplayer.models.Music

@Database(entities = [Music::class, Common::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun musicController(): MusicController
}