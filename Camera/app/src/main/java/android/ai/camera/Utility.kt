package android.ai.camera

import android.util.Size
import java.util.*

class Utility {
    companion object{}
}
class CompareSizesByArea : Comparator<Size> {
    override fun compare(lhs: Size, rhs: Size): Int {
        // We cast here to ensure the multiplications won't overflow
        return java.lang.Long.signum(
            lhs.width.toLong() * lhs.height -
                    rhs.width.toLong() * rhs.height
        )
    }

    companion object {
        fun newInstance(): CompareSizesByArea {
            return CompareSizesByArea()
        }
    }
}
/**
 * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
 * is at least as large as the respective texture view size, and that is at most as large as the
 * respective max size, and whose aspect ratio matches with the specified value. If such size
 * doesn't exist, choose the largest one that is at most as large as the respective max size,
 * and whose aspect ratio matches with the specified value.
 *
 * @param choices           The list of sizes that the camera supports for the intended output
 *                          class
 * @param textureViewWidth  The width of the texture view relative to sensor coordinate
 * @param textureViewHeight The height of the texture view relative to sensor coordinate
 * @param maxWidth          The maximum width that can be chosen
 * @param maxHeight         The maximum height that can be chosen
 * @param aspectRatio       The aspect ratio
 * @return The optimal {@code Size}, or an arbitrary one if none were big enough
 */
fun Utility.Companion.chooseOptimalSize(
    choices: Array<Size>, textureViewWidth: Int, textureViewHeight: Int, maxWidth: Int,
    maxHeight: Int, aspectRatio: Size
): Size {
    // Collect the supported resolutions that are at least as big as the preview Surface
    val bigEnough: MutableList<Size> = ArrayList()
    // Collect the supported resolutions that are smaller than the preview Surface
    // Collect the supported resolutions that are smaller than the preview Surface
    val notBigEnough: MutableList<Size> = ArrayList()
    val w = aspectRatio.width
    val h = aspectRatio.height
    for (option in choices) {
    // Log.d(TAG, "chooseOptimalSize: w: " + option.getWidth() + ", h: " + option.getHeight());
        if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w) {
            if (option.width >= textureViewWidth &&
                option.height >= textureViewHeight
            ) {
                bigEnough.add(option)
            } else {
                notBigEnough.add(option)
            }
        }
    }

    // Pick the smallest of those big enough. If there is no one big enough, pick the
    // largest of those not big enough.
    return when {
        bigEnough.size > 0 -> {
            Collections.min(
                bigEnough, CompareSizesByArea()
            )
        }
        notBigEnough.size > 0 -> {
            Collections.max(
                notBigEnough, CompareSizesByArea()
            )
        }
        else -> {
            choices[0]
        }
    }
}