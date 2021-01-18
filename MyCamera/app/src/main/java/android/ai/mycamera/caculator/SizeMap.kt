package android.ai.mycamera.caculator

import android.util.Log
import java.util.*
import kotlin.collections.HashMap

/**
 * A collection class that automatically groups [SizeCamera]s by their [AspectRatio]s.
 */
class SizeMap {
    private val mRatios: HashMap<AspectRatio, SortedSet<SizeCamera>> = HashMap()

    /**
     * Add a new [SizeCamera] to this collection.
     *
     * @param sizeCamera The size to add.
     * @return `true` if it is added, `false` if it already exists and is not added.
     */
    fun add(sizeCamera: SizeCamera): Boolean {
        for (ratio in mRatios.keys) {
            if (ratio.matches(sizeCamera)) {
                val sizes = mRatios[ratio]!!
                return if (sizes.contains(sizeCamera)) {
                    false
                } else {
                    sizes.add(sizeCamera)
                    Log.d("QQQ","add: ${sizes.size}")
                    true
                }
            }
        }
        // None of the existing ratio matches the provided size; add a new key
        val sizeCameras: SortedSet<SizeCamera> = TreeSet()
        sizeCameras.add(sizeCamera)
        mRatios[AspectRatio.of(sizeCamera.width, sizeCamera.height)] = sizeCameras
        return true
    }

    /**
     * Removes the specified aspect ratio and all sizes associated with it.
     *
     * @param ratio The aspect ratio to be removed.
     */
    fun remove(ratio: AspectRatio) {
        mRatios.remove(ratio)
    }

    fun ratios(): Set<AspectRatio?> {
        return mRatios.keys
    }

    fun sizes(ratio: AspectRatio): SortedSet<SizeCamera>? {
        return mRatios[ratio]
    }

    fun clear() {
        mRatios.clear()
    }

    fun isEmpty(): Boolean {
        return mRatios.isEmpty()
    }
}