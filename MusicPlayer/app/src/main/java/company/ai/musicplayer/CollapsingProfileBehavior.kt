package company.ai.musicplayer

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import company.ai.musicplayer.utils.ThemeHelper


@Suppress("unused")
class CollapsingProfileBehavior(private val context: Context, attrs: AttributeSet) :
        CoordinatorLayout.Behavior<CoordinatorLayout>(context, attrs) {

    private lateinit var appBar: View
    private lateinit var headerProfile: View
    private lateinit var mImageProfile: ImageView
    private lateinit var mTextContainer: View
    private lateinit var mDisplayName: TextView
    private lateinit var mAlbum: TextView
    private lateinit var mArtist: TextView

    private lateinit var windowSize: Point
    private var mAppBarHeight: Int = 0
    private val mImageSizeSmall: Int
    private val mImageSizeBig: Int
    private val mImageMaxMargin: Int
    private var toolBarHeight: Int = 0
    private var profileTextContainerMaxHeight: Int = 0
    private var profileNameHeight: Int = 0

    private var normalizedRange: Float = 0.toFloat()

    private val displaySize: Point
        get() {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size
        }

    init {
        normalizedRange = 0f
        mImageSizeSmall = context.resources.getDimension(R.dimen.profile_small_size).toInt()
        mImageSizeBig = context.resources.getDimension(R.dimen.profile_big_size).toInt()
        mImageMaxMargin = context.resources.getDimension(R.dimen.profile_image_margin_max).toInt()
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: CoordinatorLayout,
        dependency: View
    ): Boolean {
        val isDependencyAnAppBar = dependency is AppBarLayout
        if (isDependencyAnAppBar) {
            initialize(child, dependency)
        }
        return isDependencyAnAppBar
    }

    private fun initialize(child: CoordinatorLayout, dependency: View) {
        windowSize = displaySize
        appBar = dependency
        mAppBarHeight = appBar.height
        headerProfile = child

        mImageProfile = headerProfile.findViewById(R.id.image_player)
        mImageProfile.pivotX = 0f
        mImageProfile.pivotY = 0f

        mTextContainer = headerProfile.findViewById(R.id.profileContainer)
        mTextContainer.pivotX = 0f
        mTextContainer.pivotY = 0f

        mDisplayName = mTextContainer.findViewById(R.id.text_title)
        profileNameHeight = mDisplayName.height
        mAlbum = mTextContainer.findViewById(R.id.text_album)
        mArtist = mTextContainer.findViewById(R.id.text_artist)
        val profileSubtitleMaxHeight = calculateMaxHeightFromTextView((mAlbum as TextView?)!!)
        val profileMiscMaxHeight = calculateMaxHeightFromTextView((mArtist as TextView?)!!)
        profileTextContainerMaxHeight = profileNameHeight + profileSubtitleMaxHeight + profileMiscMaxHeight
    }

    private fun calculateMaxHeightFromTextView(textView: TextView): Int {
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(windowSize.x, View.MeasureSpec.AT_MOST)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(widthMeasureSpec, heightMeasureSpec)
        return textView.measuredHeight
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: CoordinatorLayout,
        dependency: View
    ): Boolean {
        val isDependencyAnAppBar = dependency is AppBarLayout
        if (isDependencyAnAppBar) {
            toolBarHeight = appBar.findViewById<View>(R.id.all_toolbar).height
            Log.d("ZZZ", "onDependentViewChanged: $toolBarHeight");
            updateNormalizedRange()
            updateOffset()
        }
        return isDependencyAnAppBar
    }

    private fun updateNormalizedRange() {
        val abl = appBar as AppBarLayout
        normalizedRange = normalize(
                currentValue = appBar.y + abl.totalScrollRange,
                minValue = 0f,
                maxValue = abl.totalScrollRange.toFloat()
        )

        normalizedRange = 1f - normalizedRange
        if (normalizedRange > 0.54f){
            mDisplayName.setTextColor(ContextCompat.getColorStateList(context, R.color.windowBackground))
        }else{
            mDisplayName.setTextColor(ContextCompat.getColorStateList(context, R.color.windowBackground))
            mAlbum.setTextColor(ContextCompat.getColorStateList(context, R.color.windowBackground))
            mArtist.setTextColor(ContextCompat.getColorStateList(context, R.color.windowBackground))
           /* if (ThemeHelper.getDefaultNightMode(context.applicationContext) == AppCompatDelegate.MODE_NIGHT_YES){
                mDisplayName.setTextColor(ContextCompat.getColorStateList(context, R.color.widgetsColor))
                mAlbum.setTextColor(ContextCompat.getColorStateList(context, R.color.widgetsColor))
                mArtist.setTextColor(ContextCompat.getColorStateList(context, R.color.widgetsColor))
            }*/

        }
        Log.d("MMM", "updateNormalizedRange: $normalizedRange")
    }

    private fun normalize(currentValue: Float, minValue: Float, maxValue: Float): Float {
        val dividend = currentValue - minValue
        val divisor = maxValue - minValue
        return dividend / divisor
    }

    private fun updateOffset() {
        updateHeaderProfileOffset()
        updateProfileImageSize()
        updateProfileImageMargins()
        updateProfileTextContainerHeight()
        updateProfileTextMargin()
        updateSubtitleAndMiscAlpha()
    }

    private fun updateHeaderProfileOffset() {
        headerProfile.y = appBar.y
    }

    private fun updateProfileImageSize() {
        val updatedValue = getUpdatedInterpolatedValue(mImageSizeBig.toFloat(), mImageSizeSmall.toFloat()).toInt()
        Log.d("ZZZ", "updateProfileImageSize: $updatedValue")
        val lp = mImageProfile.layoutParams as LinearLayout.LayoutParams
        lp.height = updatedValue
        lp.width = updatedValue
        mDisplayName.visibility = View.VISIBLE
        mImageProfile.visibility = View.VISIBLE
        mImageProfile.layoutParams = lp
    }

    private fun updateProfileImageMargins() {
        val targetOpenAppbarValue = calculateProfileImageSmallMargin().toFloat()
        val updatedValue = getUpdatedInterpolatedValue(mImageMaxMargin.toFloat(), targetOpenAppbarValue).toInt()

        val layoutParams = mImageProfile.layoutParams as LinearLayout.LayoutParams
        layoutParams.bottomMargin = updatedValue
        layoutParams.leftMargin = updatedValue
        layoutParams.rightMargin = updatedValue
        mImageProfile.layoutParams = layoutParams
    }

    private fun calculateProfileImageSmallMargin(): Int {
        val halfToolbarHeight = toolBarHeight / 2
        val halfProfileImageSmall = mImageSizeSmall / 2
        return halfToolbarHeight - halfProfileImageSmall
    }

    private fun updateProfileTextContainerHeight() {
        val updatedValue = getUpdatedInterpolatedValue(profileTextContainerMaxHeight.toFloat(), toolBarHeight.toFloat()).toInt()

        val layoutParams = mTextContainer.layoutParams as LinearLayout.LayoutParams
        layoutParams.height = updatedValue
        mTextContainer.layoutParams = layoutParams
    }

    private fun updateProfileTextMargin() {
        val targetOpenAppbarValue = calculateProfileTextMargin().toFloat()
        val updatedValue = getUpdatedInterpolatedValue(0f, targetOpenAppbarValue).toInt()

        val lp = mDisplayName.layoutParams as RelativeLayout.LayoutParams
        lp.topMargin = updatedValue
        mDisplayName.layoutParams = lp
    }

    private fun calculateProfileTextMargin(): Int {
        val halfToolbarHeight = toolBarHeight / 2
        val halfProfileTextHeight = profileNameHeight / 2
        return halfToolbarHeight - halfProfileTextHeight
    }

    private fun updateSubtitleAndMiscAlpha() {
        val updatedValue = getUpdatedInterpolatedValue(1f, 0f)
        val poweredValue = Math.pow(updatedValue.toDouble(), 6.0).toFloat()
        Log.d("ZZZ", "updateSubtitleAndMiscAlpha: $poweredValue")
        mAlbum.alpha = poweredValue
        mArtist.alpha = poweredValue
    }

    private fun getIntercept(m: Float, x: Float, b: Float): Float {

        return m * x + b
    }

    private fun getUpdatedInterpolatedValue(openSizeTarget: Float, closedSizeTarget: Float): Float {
        var intercept : Float = getIntercept(closedSizeTarget - openSizeTarget, normalizedRange, openSizeTarget)
        return if (intercept > 0){
            intercept
        }else{
            0f
        }

    }
}
