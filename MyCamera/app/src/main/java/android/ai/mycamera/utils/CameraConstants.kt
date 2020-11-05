package android.ai.mycamera.utils

import android.ai.mycamera.caculator.AspectRatio

object CameraConstants {
    val DEFAULT_ASPECT_RATIO: AspectRatio = AspectRatio.of(16, 9)
    const val AUTO_FOCUS_TIMEOUT_MS: Long =
        800 //800ms timeout, Under normal circumstances need to a few hundred milliseconds
    const val OPEN_CAMERA_TIMEOUT_MS: Long = 2500 //2.5s
    const val FOCUS_HOLD_MILLIS = 3000
    const val METERING_REGION_FRACTION = 0.1225f
    const val ZOOM_REGION_DEFAULT = 1

    const val FLASH_OFF = 0
    const val FLASH_ON = 1
    const val FLASH_TORCH = 2
    const val FLASH_AUTO = 3
    const val FLASH_RED_EYE = 4

    const val FACING_BACK = 0
    const val FACING_FRONT = 1
}