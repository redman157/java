package company.ai.musicplayer.database

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.util.DBUtil
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import company.ai.musicplayer.MusicApplication
import company.ai.musicplayer.MusicDatabaseWorker
import company.ai.musicplayer.extensions.rePopulateDb
import company.ai.musicplayer.models.Common
import company.ai.musicplayer.models.Director
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants.DATABASE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(entities = [Music::class, Director::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun directorDao(): DirectorDao
    companion object{
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null
        fun getInstance(application: Application): AppDatabase {
          /*  return instance ?: synchronized(this) {
                instance ?: buildDatabase(application).also { instance = it }
            }*/
            if (instance == null) {
                synchronized(AppDatabase::class.java) {
                    if (instance == null) {
                        instance = buildDatabase(application)
                    }
                }
            }
            return instance!!
        }
        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(application: Application): AppDatabase {
            Log.d("WWW", "${DATABASE_NAME}")
            return Room.databaseBuilder(application.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(
                object : RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.d("WWW", "onCreate ${DATABASE_NAME}")
                       /* GlobalScope.launch(Dispatchers.IO) {
                            Log.d("WWW", "launch ${DATABASE_NAME}")
                            rePopulateDb(database = instance,application = application)
                        }*/
                        val request = OneTimeWorkRequestBuilder<MusicDatabaseWorker>().build()
                        WorkManager.getInstance(application.applicationContext).enqueue(request)
                    }
                }
            ).build()
        }
    }

}
