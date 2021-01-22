package company.ai.musicplayer

import android.app.Activity
import android.content.Context
import android.util.JsonReader
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import company.ai.musicplayer.database.AppDatabase
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants.MUSIC_DATA_FILENAME
import company.ai.musicplayer.utils.MusicOrg
import kotlinx.coroutines.coroutineScope

class MusicDatabaseWorker(
    var context: Context,
    workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try{
            AppDatabase.getInstance(context = context).musicController().insertAll(MusicOrg.queryForMusic((context as Activity).application)!!)
            Result.success()
        }catch (e: Exception){
            e.printStackTrace()
            Result.failure()
        }
    }
}