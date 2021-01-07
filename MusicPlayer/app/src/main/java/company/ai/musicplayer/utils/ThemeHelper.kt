package company.ai.musicplayer.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import android.widget.ImageButton
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.os.bundleOf
import androidx.core.widget.ImageViewCompat
import company.ai.musicplayer.R
import company.ai.musicplayer.ui.HomeActivity
import company.ai.musicplayer.extensions.decodeColor
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.player.MediaPlayerHolder

object ThemeHelper {
    @JvmStatic
    fun applyChanges(activity: Activity) {
        val intent = Intent(activity, HomeActivity::class.java)

        val bundle = bundleOf(Pair(Constants.RESTORE_SETTINGS_FRAGMENT, true))
        intent.putExtras(bundle)
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK
        )
        activity.apply {
            finishAfterTransition()
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    @JvmStatic
    fun getPreciseVolumeIcon(volume: Int) = when (volume) {
        in 1..33 -> R.drawable.ic_volume_mute
        in 34..67 -> R.drawable.ic_volume_down
        in 68..100 -> R.drawable.ic_volume_up
        else -> R.drawable.ic_volume_off
    }

    @JvmStatic
    private fun resolveThemeAttr(context: Context, @AttrRes attrRes: Int) =
        TypedValue().apply { context.theme.resolveAttribute(attrRes, this, true) }

    @ColorInt
    @JvmStatic
    fun resolveColorAttr(context: Context, @AttrRes colorAttr: Int): Int {
        val resolvedAttr: TypedValue =
            resolveThemeAttr(
                context,
                colorAttr
            )
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes =
            if (resolvedAttr.resourceId != 0) {
                resolvedAttr.resourceId
            } else {
                resolvedAttr.data
            }
        return colorRes.decodeColor(context)
    }

    @JvmStatic
    fun updateIconTint(imageButton: ImageButton, tint: Int) {
        ImageViewCompat.setImageTintList(
            imageButton, ColorStateList.valueOf(tint)
        )
    }

    @JvmStatic
    fun updateTint(circle: ImageButton, tint: Int) {
        circle.setColorFilter(tint, android.graphics.PorterDuff.Mode.SRC_IN)
    }

    // Fixed array of pairs (first: accent, second: theme, third: color primary dark)
    @JvmStatic
    val accents = arrayOf(
        Pair(R.string.red ,Pair(R.color.primary_red, R.style.BaseTheme_Red)),
        Pair(R.string.pink ,Pair(R.color.primary_pink, R.style.BaseTheme_Pink)),
        Pair(R.string.purple ,Pair(R.color.primary_purple, R.style.BaseTheme_Purple)),
        Pair(R.string.deep_purple ,Pair(R.color.primary_deep_purple, R.style.BaseTheme_DeepPurple)),
        Pair(R.string.indigo ,Pair(R.color.primary_indigo, R.style.BaseTheme_Indigo)),
        Pair(R.string.blue ,Pair(R.color.primary_blue, R.style.BaseTheme_Blue)),
        Pair(R.string.light_blue ,Pair(R.color.primary_light_blue, R.style.BaseTheme_LightBlue)),
        Pair(R.string.cyan ,Pair(R.color.primary_cyan, R.style.BaseTheme_Cyan)),
        Pair(R.string.teal ,Pair(R.color.primary_teal, R.style.BaseTheme_Teal)),
        Pair(R.string.green ,Pair(R.color.primary_green, R.style.BaseTheme_Green)),
        Pair(R.string.light_green ,Pair(R.color.primary_light_green, R.style.BaseTheme_LightGreen)),
        Pair(R.string.lime ,Pair(R.color.primary_lime, R.style.BaseTheme_Lime)),
        Pair(R.string.yellow ,Pair(R.color.primary_yellow, R.style.BaseTheme_Yellow)),
        Pair(R.string.amber ,Pair(R.color.primary_amber, R.style.BaseTheme_Amber)),
        Pair(R.string.orange ,Pair(R.color.primary_orange, R.style.BaseTheme_Orange)),
        Pair(R.string.deep_orange ,Pair(R.color.primary_deep_orange, R.style.BaseTheme_DeepOrange)),
        Pair(R.string.brown ,Pair(R.color.primary_brown, R.style.BaseTheme_Brown)),
        Pair(R.string.grey ,Pair(R.color.primary_grey, R.style.BaseTheme_Grey)),
        Pair(R.string.blue_grey ,Pair(R.color.primary_blue_grey, R.style.BaseTheme_BlueGrey)),
        Pair(R.string.black ,Pair(R.color.primary_default, R.style.BaseTheme_Default))

    )

    @JvmStatic
    fun getDefaultNightMode(context: Context) = when (mPreferences.theme) {
        context.getString(R.string.theme_pref_light) -> AppCompatDelegate.MODE_NIGHT_NO
        context.getString(R.string.theme_pref_dark) -> AppCompatDelegate.MODE_NIGHT_YES
        else -> if (VersioningHelper.isQ()) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
        }
    }

    @JvmStatic
    fun resolveThemeIcon(context: Context) = when (mPreferences.theme) {
        context.getString(R.string.theme_pref_light) -> R.drawable.ic_day
        context.getString(R.string.theme_pref_auto) -> R.drawable.ic_auto
        else -> R.drawable.ic_night
    }

    @JvmStatic
    @SuppressLint("DefaultLocale")
    fun getAccentName(accent: Int, context: Context): SpannableStringBuilder {
        var accentName = context.resources.getResourceEntryName(accent).removeRange(0, 8)
        accentName = accentName.split("_").joinToString(" ") { it.capitalize() }
        return SpannableStringBuilder(accentName).apply {
            setSpan(StyleSpan(Typeface.BOLD), 0, accentName.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            setSpan(ForegroundColorSpan(ContextCompat.getColor(context ,mPreferences.accent)), 0, accentName.length,0)
        }
    }

    @JvmStatic
    fun createColouredRipple(context: Context, rippleColor: Int, rippleId: Int): Drawable {
        val ripple = AppCompatResources.getDrawable(context, rippleId) as RippleDrawable
        return ripple.apply {
            setColor(ColorStateList.valueOf(rippleColor))
        }
    }

    // Search theme from accents array of Pair, returns a Pair(theme, position)
    @JvmStatic
    fun getAccentedTheme() = try {
        val pair = accents.find { pair -> pair.second.first == mPreferences.accent }
        val theme = pair!!.second.second
        val position = accents.indexOf(pair)
        Pair(theme, position)
    } catch (e: Exception) {
        Pair(R.style.BaseTheme_DeepPurple, 3)
    }

    @ColorInt
    @JvmStatic
    fun getColor(context: Context, color: Int, emergencyColor: Int) = try {
        color.decodeColor(context)
    } catch (e: Exception) {
        emergencyColor.decodeColor(context)
    }

    @ColorInt
    @JvmStatic
    fun resolveThemeAccent(context: Context): Int {
        var accent = mPreferences.accent

        // Fallback to default color when the pref is f@#$ed (when resources change)
        if (!accents.map { accentId -> accentId.second.first }.contains(accent)) {
            accent = R.color.primary_deep_purple
            mPreferences.accent = accent
        }
        return getColor(
            context,
            accent,
            R.color.primary_deep_purple
        )
    }

    @JvmStatic
    fun getAlphaForAccent() = 150

    @JvmStatic
    fun getAlphaAccent(context: Context, alpha: Int) =
        ColorUtils.setAlphaComponent(
            resolveThemeAccent(
                context
            ), alpha
        )

    @JvmStatic
    fun getRepeatIcon(mediaPlayerHolder: MediaPlayerHolder) = when {
        mediaPlayerHolder.isRepeat1X -> R.drawable.ic_repeat_one
        mediaPlayerHolder.isLooping -> R.drawable.ic_repeat
        else -> R.drawable.ic_repeat_one_notif_disabled
    }
}