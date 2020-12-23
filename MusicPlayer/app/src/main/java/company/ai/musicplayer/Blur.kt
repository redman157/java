package company.ai.musicplayer

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.squareup.picasso.Transformation


open class Blur(protected val context: Context, radius: Int) : Transformation {
    private var blurRadius = 0
    override fun transform(source: Bitmap): Bitmap {
        val blurredBitmap: Bitmap = Bitmap.createBitmap(source)
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(
            renderScript,
            source,
            Allocation.MipmapControl.MIPMAP_FULL,
            Allocation.USAGE_SCRIPT
        )
        val output = Allocation.createTyped(renderScript, input.type)
        val script = ScriptIntrinsicBlur.create(
            renderScript,
            Element.U8_4(renderScript)
        )
        script.setInput(input)
        script.setRadius(blurRadius.toFloat())
        script.forEach(output)
        output.copyTo(blurredBitmap)
        source.recycle()
        return blurredBitmap
    }

    override fun key(): String {
        return "blurred"
    }

    companion object {
        protected const val UP_LIMIT = 25
        protected const val LOW_LIMIT = 1
    }

    init {
        blurRadius = when {
            radius < LOW_LIMIT -> {
                LOW_LIMIT
            }
            radius > UP_LIMIT -> {
                UP_LIMIT
            }
            else -> radius
        }
    }
}