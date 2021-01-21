package company.ai.musicplayer.extensions

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color.red
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import coil.transform.BlurTransformation
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.squareup.picasso.Picasso.LoadedFrom
import company.ai.musicplayer.R
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.utils.ThemeHelper
import java.lang.Exception
import kotlin.math.max


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
fun View.createCircularReveal(isErrorFragment: Boolean, show: Boolean): Animator {

    val revealDuration: Long = if (isErrorFragment) {
        1500
    } else {
        500
    }
    val radius = max(width, height).toFloat()

    val startRadius = if (show) {
        0f
    } else {
        radius
    }
    val finalRadius = if (show) {
        radius
    } else {
        0f
    }

    val cx = if (isErrorFragment) {
        width / 2
    } else {
        0
    }
    val cy = if (isErrorFragment) {
        height / 2
    } else {
        0
    }
    val animator =
        ViewAnimationUtils.createCircularReveal(
            this,
            cx,
            cy,
            startRadius,
            finalRadius
        ).apply {
            interpolator = FastOutSlowInInterpolator()
            duration = revealDuration
            doOnEnd {
                if (!show) {
                    handleViewVisibility(false)
                }
            }
            start()
        }

    val windowBackground = R.color.windowBackground.decodeColor(context)
    val closeColor = ThemeHelper.resolveColorAttr(context, R.attr.colorControlHighlight)
    val accent = if (!show) {
        windowBackground
    } else {
        ThemeHelper.resolveThemeAccent(context)
    }

    val startColor = if (isErrorFragment) {
        (ContextCompat.getColor(context, R.color.red_alpha_100)).decodeColor(context)
    } else {
        accent
    }
    val endColor = if (show) {
        windowBackground
    } else {
        closeColor
    }

    ValueAnimator().apply {
        setIntValues(startColor, endColor)
        setEvaluator(ArgbEvaluatorCompat())
        addUpdateListener { valueAnimator -> setBackgroundColor((valueAnimator.animatedValue as Int)) }
        duration = revealDuration
        if (isErrorFragment) {
            doOnEnd {
               /* background =
                    ThemeHelper.createColouredRipple(
                        context,
                        (ContextCompat.getColor(context, R.color.red_alpha_100)).decodeColor(context),
                        R.drawable.ripple
                    )*/
            }
        }
        start()
    }
    return animator
}

fun View.handleViewVisibility(show: Boolean) {
    visibility = if (show) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun FragmentManager.isFragment(fragmentTag: String): Boolean {
    val df = findFragmentByTag(fragmentTag)
    return df != null && df.isVisible && df.isAdded
}

fun FragmentManager.addFragment(fragment: Fragment, tag: String?, isReplace: Boolean) {
    beginTransaction().apply {
        addToBackStack(null)
        if (isReplace) replace(R.id.container, fragment, tag)
        else add(R.id.container, fragment, tag)
        commit()
    }
}

fun String.toToast(context: Context) {
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