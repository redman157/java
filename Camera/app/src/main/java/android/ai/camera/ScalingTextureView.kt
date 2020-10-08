package android.ai.camera

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.TextureView
import android.ai.camera.gestures.MoveGestureDetector
import kotlin.Boolean
import kotlin.math.max
import kotlin.math.min

class ScalingTextureView : TextureView {
    companion object {
        private const val TAG = "ScalingTextureView"
    }
    var mRatioWidth = 0
    var mRatioHeight = 0
    private var mScreenWidth = 0
    private var mScreenHeight = 0
    private var mRoundedScreenAspectRatio = ""
    private var mRoundedPreviewAspectRatio = ""

    private var mMatrix: Matrix? = null

    private var mScaleDetector: ScaleGestureDetector? = null

    private var mMoveDetector: MoveGestureDetector? = null

    // scaling
    private var mScaleFactor = 1f
    var mScaleFactorX = 1f
    var mScaleFactorY = 1f
    private var mWidthCorrection = 0f
    private var mScreenAspectRatio = 1f
    private var mPreviewAspectRatio = 1f
    private var mImageCenterX = 0f
    private var mImageCenterY = 0f
    var mFocusX = 0f
    var mFocusY = 0f


    constructor(context: Context) : super(context, null) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) :super(context, attrs, 0){
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {

        init(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor (context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    fun setAspectRatio(width: Int, height: Int, screenWidth: Int, screenHeight: Int) {
        require(!(width < 0 || height < 0)) { "Size cannot be negative." }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
        mScreenWidth = screenWidth
        mScreenHeight = screenHeight
        mScreenAspectRatio = mScreenHeight.toFloat() / mScreenWidth.toFloat()
        mPreviewAspectRatio = mRatioHeight.toFloat() / mRatioWidth.toFloat()
        mRoundedScreenAspectRatio = String.format("%.2f", mScreenAspectRatio)
        mRoundedPreviewAspectRatio = String.format("%.2f", mPreviewAspectRatio)
        getWidthCorrection()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        }
        setMeasuredDimension(mScreenWidth, mScreenHeight)
    }


    private fun init(context: Context) {
        mMatrix = Matrix()

        // Setup Gesture Detectors
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mMoveDetector = MoveGestureDetector(context, MoveListener())
    }

    private fun getWidthCorrection() {
        val roundedScreenAspectRatio = String.format("%.2f", mScreenAspectRatio)
        val roundedPreviewAspectRatio = String.format("%.2f", mPreviewAspectRatio)
        if (roundedPreviewAspectRatio != roundedScreenAspectRatio) {
            val scaleFactor = mScreenAspectRatio / mPreviewAspectRatio
            Log.d(TAG, "configureTransform: scale factor: $scaleFactor")
            mWidthCorrection = (mScreenWidth.toFloat() * scaleFactor - mScreenWidth) / 2
            Log.d(TAG, "getWidthCorrection: width correction: $mWidthCorrection")
        }
    }


    fun onTouch(motionEvent: MotionEvent): Boolean {
        mScaleDetector!!.onTouchEvent(motionEvent)
        mMoveDetector!!.onTouchEvent(motionEvent)
        if (mScaleFactor > 1.011111 || mScaleFactor < 0.99999) {
            mMatrix!!.reset()
            Log.d(TAG, "onTouch: scale factor: $mScaleFactor")
            mScaleFactorY = mScaleFactor
            mScaleFactorX = mScaleFactor
            if (mRoundedPreviewAspectRatio != mRoundedScreenAspectRatio) {
                mScaleFactorX *= mScreenAspectRatio / mPreviewAspectRatio
                Log.d(TAG, "configureTransform: scale factor: $mScaleFactorX")
            }
            val scaledImageCenterX = width * mScaleFactorX / 2
            val scaledImageCenterY = height * mScaleFactorY / 2
            mMatrix!!.postScale(mScaleFactorX, mScaleFactorY)
            var dx = mImageCenterX - scaledImageCenterX
            var dy = mImageCenterY - scaledImageCenterY
            Log.d(TAG, "onTouch: dx: $dx, dy: $dy")


            // BOUNDARY 1: Right
            if (dx < width - (mScreenWidth.toFloat() - mWidthCorrection) * mScaleFactorX) {
                dx = width - (mScreenWidth.toFloat() - mWidthCorrection) * mScaleFactorX
                mImageCenterX = dx + scaledImageCenterX // reverse the changes
            }

            //BOUNDARY 2: Bottom
            if (dy < height - mScreenHeight.toFloat() * mScaleFactorY) {
                dy = height - mScreenHeight.toFloat() * mScaleFactorY
                mImageCenterY = dy + scaledImageCenterY
            }


            // BOUNDARY 3: Left
            if (dx > -mWidthCorrection) {
                dx = -mWidthCorrection
                mImageCenterX = dx + scaledImageCenterX
            }


            // BOUNDARY 4: Top
            if (dy > 0) {
                dy = 0f
                mImageCenterY = dy + scaledImageCenterY
            }
            mMatrix!!.postTranslate(dx, dy)
            setTransform(mMatrix)
            alpha = 1f
            mFocusX = -1 * (dx / mScaleFactorX)
            mFocusY = -1 * (dy / mScaleFactorY)
        }
        return true // indicate event was handled
    }


    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor // scale change since previous event

            // Don't let the object get too small or too large.
            mScaleFactor = max(1f, min(mScaleFactor, 4.0f))
            return true
        }
    }

    private inner class MoveListener : MoveGestureDetector.SimpleOnMoveGestureListener() {
        override fun onMove(detector: MoveGestureDetector?): Boolean {
            var d: PointF = detector!!.focusDelta
            mImageCenterX += d.x
            mImageCenterY += d.y
            Log.d(TAG, "onMove: image center x: $mImageCenterX")
            Log.d(TAG, "onMove: image canter y: $mImageCenterY")
            return true
        }
    }

    fun resetScale() {
        mScaleFactor = 1.0f
        mScaleFactorX = 1f
        mScaleFactorY = 1f
        mImageCenterX = mRatioWidth / 2.toFloat()
        mImageCenterX = mRatioHeight / 2.toFloat()
        mFocusX = 0f
        mFocusY = 0f
    }
}