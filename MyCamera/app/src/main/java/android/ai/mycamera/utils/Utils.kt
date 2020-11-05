package android.ai.mycamera.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore

object Utils {
    private const val MAX_ALLOWED_RESOLUTION = 1080
    fun getVideoFilePath(context: Context): String {
        val dir = context.getExternalFilesDir(null)
        return ((if (dir == null) "" else dir.absolutePath + "/")
                + System.currentTimeMillis() + ".mp4")
    }

    fun getPictureFilePath(context: Context): String {
        val dir = context.getExternalFilesDir(null)
        return ((if (dir == null) "" else dir.absolutePath + "/")
                + System.currentTimeMillis() + ".jpg")
    }
    // TODO get path from uri
    fun getRealPathFromURI(uri: Uri, context: Context): String {
        var path = ""
        context.contentResolver?.let { it ->
            val cursor = it.query(uri, null, null, null, null)
            cursor?.let {
                it.moveToFirst()
                val idx = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                path = it.getString(idx)
                cursor.close()
            }
        }
        return path
    }

    // TODO resize bitmap
    fun downscaleToMaxAllowedDimension(bitmap: Bitmap): Bitmap {
        val outWidth: Int
        val outHeight: Int
        val inWidth = bitmap.width
        val inHeight = bitmap.height
        if (inHeight <= MAX_ALLOWED_RESOLUTION || inWidth <= MAX_ALLOWED_RESOLUTION) {
            return bitmap
        }
        if (inWidth > inHeight) {
            outWidth = MAX_ALLOWED_RESOLUTION
            outHeight = inHeight * MAX_ALLOWED_RESOLUTION / inWidth
        } else {
            outHeight = MAX_ALLOWED_RESOLUTION
            outWidth = inWidth * MAX_ALLOWED_RESOLUTION / inHeight
        }
        return Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false)
    }
}