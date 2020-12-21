package company.ai.musicplayer.extensions

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Picasso.LoadedFrom
import company.ai.musicplayer.R
import company.ai.musicplayer.models.Music
import java.lang.Exception


// viewTreeObserver extension to measure layout params
// https://antonioleiva.com/kotlin-ongloballayoutlistener/
inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                f()
            }
        }
    })
}

fun FragmentManager.isFragment(fragmentTag: String): Boolean {
    val df = findFragmentByTag(fragmentTag)
    return df != null && df.isVisible && df.isAdded
}

fun View.handleViewVisibility(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun FragmentManager.addFragment(fragment: Fragment, tag: String?, isReplace: Boolean) {
    beginTransaction().apply {
        addToBackStack(null)
        if (isReplace) {
            replace(R.id.container, fragment, tag)
        } else {
            add(
                R.id.container,
                fragment,
                tag
            )
        }
        commit()
    }
}

fun String.toToast(
    context: Context
) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}

fun ImageView.imageByPicasso(albumID: Long?){
    Picasso.get()
        .load(getSongUri(albumID = albumID))
        .placeholder(R.drawable.ic_music_note)
        .resize(400, 400)
        .onlyScaleDown()
        .into(this)
}


private fun getSongUri(albumID: Long?): Uri? {
    return if (albumID == null){
        null
    }else{
        ContentUris.withAppendedId(
            Uri
                .parse("content://media/external/audio/albumart"), albumID
        )
    }
}

@ColorInt
fun Int.decodeColor(context: Context) = ContextCompat.getColor(context, this)