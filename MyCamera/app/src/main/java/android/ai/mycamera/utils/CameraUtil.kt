package android.ai.mycamera.utils

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF

object CameraUtil {

    /**
     * Clamps x to between min and max (inclusive on both ends, x = min --> min,
     * x = max --> max).
     */
    fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) {
            return max
        }
        return if (x < min) {
            min
        } else x
    }

    /**
     * Clamps x to between min and max (inclusive on both ends, x = min --> min,
     * x = max --> max).
     */
    fun clamp(x: Float, min: Float, max: Float): Float {
        if (x > max) {
            return max
        }
        return if (x < min) {
            min
        } else x
    }

    fun inlineRectToRectF(rectF: RectF, rect: Rect) {
        rect.left = Math.round(rectF.left)
        rect.top = Math.round(rectF.top)
        rect.right = Math.round(rectF.right)
        rect.bottom = Math.round(rectF.bottom)
    }

    fun rectFToRect(rectF: RectF): Rect? {
        val rect = Rect()
        inlineRectToRectF(rectF, rect)
        return rect
    }

    fun rectToRectF(r: Rect): RectF? {
        return RectF(r.left.toFloat(), r.top.toFloat(), r.right.toFloat(), r.bottom.toFloat())
    }

    /**
     * Linear interpolation between a and b by the fraction t. t = 0 --> a, t =
     * 1 --> b.
     */
    fun lerp(a: Float, b: Float, t: Float): Float {
        return a + t * (b - a)
    }

    /**
     * Given (nx, ny) \in [0, 1]^2, in the display's portrait coordinate system,
     * returns normalized sensor coordinates \in [0, 1]^2 depending on how the
     * sensor's orientation \in {0, 90, 180, 270}.
     *
     *
     * Returns null if sensorOrientation is not one of the above.
     *
     */
    fun normalizedSensorCoordsForNormalizedDisplayCoords(
        nx: Float, ny: Float, sensorOrientation: Int
    ): PointF? {
        return when (sensorOrientation) {
            0 -> PointF(nx, ny)
            90 -> PointF(ny, 1.0f - nx)
            180 -> PointF(1.0f - nx, 1.0f - ny)
            270 -> PointF(1.0f - ny, nx)
            else -> null
        }
    }
}