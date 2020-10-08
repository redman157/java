package android.ai.camera

import android.graphics.drawable.Drawable

interface IMainActivity {
    fun setCameraFrontFacing()

    fun setCameraBackFacing()

    fun isCameraFrontFacing(): Boolean

    fun isCameraBackFacing(): Boolean

    fun setFrontCameraId(cameraId: String)

    fun setBackCameraId(cameraId: String)

    fun getFrontCameraId(): String

    fun getBackCameraId(): String

    fun hideStatusBar()

    fun showStatusBar()

    fun hideStillShotWidgets()

    fun showStillShotWidgets()

    fun toggleViewStickersFragment()

    fun addSticker(sticker: Drawable)

    fun setTrashIconSize(width: Int, height: Int)
}