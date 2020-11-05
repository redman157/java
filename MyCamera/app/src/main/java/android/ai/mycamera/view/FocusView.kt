package android.ai.mycamera.view

import android.ai.mycamera.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class FocusView : View {
    private var mSize = 0
    private var mCenterX = 0
    private var mCenterY = 0
    private var mLength = 0
    private var mPaint: Paint? = null

    constructor(context: Context, size: Int) : this(context) {
        init(size)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mSize = context.resources.getDimensionPixelSize(R.dimen.focus_view_size)
        init(mSize)
    }

    private constructor(context: Context) : super(context) {}

    private fun init(size: Int) {
        mSize = size
        mPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.WHITE
            strokeWidth = 4f
            style = Paint.Style.STROKE
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mCenterX = (mSize / 2.0).toInt()
        mCenterY = (mSize / 2.0).toInt()
        mLength = (mSize / 2.0).toInt() - 2
        setMeasuredDimension(mSize, mSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(
            mCenterX - mLength.toFloat(),
            mCenterY - mLength.toFloat(),
            mCenterX + mLength.toFloat(),
            mCenterY + mLength.toFloat(),
            mPaint!!
        )
        canvas.drawLine(
            2f, height / 2.toFloat(), mSize / 10.toFloat(), height / 2.toFloat(),
            mPaint!!
        )
        canvas.drawLine(
            width - 2.toFloat(),
            height / 2.toFloat(),
            width - mSize / 10.toFloat(),
            height / 2.toFloat(),
            mPaint!!
        )
        canvas.drawLine(
            width / 2.toFloat(), 2f, width / 2.toFloat(), mSize / 10.toFloat(),
            mPaint!!
        )
        canvas.drawLine(
            width / 2.toFloat(),
            height - 2.toFloat(),
            width / 2.toFloat(),
            height - mSize / 10.toFloat(),
            mPaint!!
        )
    }
}
