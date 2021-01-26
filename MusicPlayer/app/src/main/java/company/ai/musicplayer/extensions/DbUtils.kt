package company.ai.musicplayer.extensions

import android.app.Application
import android.util.Log
import company.ai.musicplayer.database.AppDatabase
import company.ai.musicplayer.utils.MusicOrg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun rePopulateDb(database: AppDatabase?, application: Application) {
    database?.let { db ->
        withContext(Dispatchers.IO) {
            val music = db.musicDao()
            val director = db.directorDao()
            music.deleteTable()
            val allMusic = MusicOrg.queryForMusic(application)
            Log.d("WWW","size: ${allMusic!!.size}")
            allMusic.let { music.insertAll(it) }
        }
    }
}