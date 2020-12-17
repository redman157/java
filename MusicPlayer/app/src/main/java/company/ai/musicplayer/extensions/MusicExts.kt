package company.ai.musicplayer.extensions

import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import company.ai.musicplayer.R
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.models.SavedMusic
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.toContentUri(): Uri = ContentUris.withAppendedId(
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    this
)

fun String.toMusic(allMusic: MutableList<Music>?): Music? {
    for (index in allMusic!!){
        if (this == index.displayName){
            return index
        }
    }
    return null
}

fun Long.toFormattedDuration(isAlbum: Boolean, isSeekBar: Boolean) = try {
    val defaultFormat = if (isAlbum) {
        "%02dm:%02ds"
    } else {
        "%02d:%02d"
    }
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this)

    if (minutes < 60) {
        String.format(
            Locale.getDefault(), defaultFormat,
            minutes,
            seconds - TimeUnit.MINUTES.toSeconds(minutes)
        )
    }else{
        // https://stackoverflow.com/a/9027379
        when {
            isSeekBar -> String.format(
                "%02d:%02d:%02d",
                hours,
                minutes - TimeUnit.HOURS.toMinutes(hours),
                seconds - TimeUnit.MINUTES.toSeconds(minutes)
            )
            else -> String.format(
                "%02dh:%02dm",
                hours,
                minutes - TimeUnit.HOURS.toMinutes(hours)
            )
        }
    }

}catch (e: Exception) {
    e.printStackTrace()
    ""
}

fun Int.toFormattedYear(resources: Resources) =
    if (this != 0) {
        toString()
    } else {
        resources.getString(R.string.unknown_year)
    }

fun Music.getCover(context: Context): Bitmap? {
    val contentUri = id?.toContentUri()
    val retriever = MediaMetadataRetriever()
    return try {
        retriever.setDataSource(context, contentUri)

        val picture = retriever.embeddedPicture

        if (picture != null) {
            BitmapFactory
                .decodeByteArray(picture, 0, picture.size)
        } else {
            null
        }
    } finally {
        retriever.release()
    }
}

fun Music.getAlbumArt(context: Context): Bitmap {
    var bitmap: Bitmap? = null
    try {
        var uri: Uri
        if (this.albumID != null) {
            uri = ContentUris.withAppendedId(
                Uri
                    .parse("content://media/external/audio/albumart"), this.albumID
            )
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            return bitmap
        }else {
            bitmap = getLargeIcon(context)
        }

    } catch (e: IOException) {
        //handle exception
        if (e is FileNotFoundException) {
            bitmap = getLargeIcon(context)

        }
    }
    return bitmap!!
}

//https://gist.github.com/Gnzlt/6ddc846ef68c587d559f1e1fcd0900d3
private fun getLargeIcon(context: Context): Bitmap {
    val vectorDrawable = context.getDrawable(R.drawable.app_icon_music) as VectorDrawable?
    val largeIconSize = context.resources.getDimensionPixelSize(R.dimen._256sdp)
    val bitmap = Bitmap.createBitmap(largeIconSize, largeIconSize, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    if (vectorDrawable != null) {
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.setTint(ContextCompat.getColor(context,R.color.black))
        vectorDrawable.alpha = 100
        vectorDrawable.draw(canvas)
    }
    return bitmap
}

fun Music.toSavedMusic(playerPosition: Int, launchedBy: String) =
    SavedMusic(
        artist,
        title,
        displayName,
        year,
        playerPosition,
        duration,
        album,
        launchedBy
    )
