package android.ai.mycamera.caculator

import androidx.annotation.NonNull


/**
 * Create a new immutable Size instance.
 *
 * @param width  The width of the size, in pixels
 * @param height The height of the size, in pixels
 */
class SizeCamera(
    var width: Int, var height: Int
): Comparable<SizeCamera>{

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (this === other) {
            return true
        }
        if (other is SizeCamera) {
            val sizeCamera: SizeCamera = other
            return width == sizeCamera.width && height == sizeCamera.height
        }
        return false
    }

    override fun toString(): String {
        return "Width: $width --- Height: $height"
    }

    override fun hashCode(): Int {
        // assuming most sizes are <2^16, doing a rotate will give us perfect hashing
        return height xor (width shl Integer.SIZE / 2 or (width ushr Integer.SIZE / 2))
    }

    override fun compareTo(@NonNull other: SizeCamera): Int {
        return width * height - other.width * other.height
    }
}