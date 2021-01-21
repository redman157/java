package company.ai.musicplayer

import android.content.Context
import android.util.JsonReader
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.reflect.TypeToken
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.Constants.MUSIC_DATA_FILENAME
import kotlinx.coroutines.coroutineScope

class MusicDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try{
            applicationContext.assets.open(MUSIC_DATA_FILENAME).use {inputStream ->
                JsonReader(inputStream.reader()).use {
                    val planType = object: TypeToken<MutableList<Music>>(){}.type

                    Result.success()
                }

            }
        }catch (e: Exception){
            e.printStackTrace()
            Result.failure()
        }
    }
}