package android.ai.camera

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import java.util.*

class DrawableImageView : AppCompatImageView {
    companion object{
        private const val TAG = "DrawableImageView"

        private const val SIZE_CHANGE_SPEED = 2
        private const val STICKER_STARTING_WIDTH = 300
        private const val STICKER_STARTING_HEIGHT = 300
        private const val MIN_STICKER_WIDTH = 50
        private const val MIN_STICKER_HEIGHT = 50
        private const val TRASH_ICON_ENLARGED_SIZE = 55
        private const val TRASH_ICON_NORMAL_SIZE = 44
    }

    //vars
    private val color = 0
    private val width = 8f
    private val mPenList: ArrayList<DrawableImageView.Pen> =
        ArrayList<DrawableImageView.Pen>()
    private var mActivity: Activity? = null
    private val mIsDrawingEnabled = false


    // Scales
    var mMinWidth = 8f
    var mMaxWidth = 500f
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private val mIsSizeChanging = false
    private val mCircle: DrawableImageView.Circle? = null
    private var mScreenWidth = 0


    // Stickers
    private val mStickers: ArrayList<DrawableImageView.Sticker> =
        ArrayList<DrawableImageView.Sticker>()
    var mPrevStickerX = 0
    var mPrevStickerY: Int = 0
    var mSelectedStickerIndex = -1
    private val mIsStickerResizing = false

    // Trash can location
    var trashRect: Rect? = null

    private inner class Sticker  constructor(var bitmap: Bitmap, var drawable: Drawable, var x: Int, var y: Int) {
        var paint: Paint = Paint()
        var rect: Rect = Rect(x, y, x + STICKER_STARTING_WIDTH, y + STICKER_STARTING_HEIGHT)
        fun adjustRect() {
            rect.left = x
            rect.top = y
            rect.right = x + bitmap.width
            rect.bottom = y + bitmap.height
        }
    }

    private inner class Circle constructor(color: Int, var x: Float, var y: Float) {
        var paint: Paint = Paint()

        init {
            paint.isAntiAlias = true
            paint.strokeWidth = width
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Paint.Cap.ROUND
        }
    }

    private inner class Pen constructor(color: Int, width: Float) {
        var path: Path = Path()
        var paint: Paint = Paint()

        init {
            paint.isAntiAlias = true
            paint.strokeWidth = width
            paint.color = color
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeCap = Paint.Cap.ROUND
        }
    }

    constructor(context: Context) : super(context){
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        init(context)
    }
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ){
        init(context)
    }

    private fun init(context: Context) {
        mPenList.add(Pen(color, width))
        isDrawingCacheEnabled = true
        mActivity = context as Activity
        mScaleGestureDetector = ScaleGestureDetector(mActivity, ScaleListener())
        val displayMetrics = DisplayMetrics()
        mActivity!!.display!!.getRealMetrics(displayMetrics)
        mScreenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val density = displayMetrics.density
        val bottomMargin =
            mActivity!!.resources.getDimension(R.dimen.cam_widget_margin_bottom).toInt()
        val left: Int = mScreenWidth / 2 - (TRASH_ICON_NORMAL_SIZE * density + 0.5f).toInt()
        val top =
            screenHeight - ((bottomMargin + TRASH_ICON_NORMAL_SIZE) * density + 0.5f).toInt()
        val right: Int = mScreenWidth / 2 + (TRASH_ICON_NORMAL_SIZE * density + 0.5f).toInt()
        trashRect = Rect(left, top, right, screenHeight)

    }

   private inner class ScaleListener: ScaleGestureDetector.OnScaleGestureListener {
       override fun onScale(detector: ScaleGestureDetector): Boolean = true

       override fun onScaleBegin(detector: ScaleGestureDetector): Boolean = true

       override fun onScaleEnd(detector: ScaleGestureDetector) {
       }

   }
}