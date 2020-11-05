package android.ai.mycamera.utils

import android.graphics.PointF
import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.params.MeteringRectangle

object AutoFocusHelper{
    /**
    * camera2 API metering region weight.
    */

    private val CAMERA2_REGION_WEIGHT: Float = CameraUtil.lerp(
        MeteringRectangle.METERING_WEIGHT_MIN.toFloat(),
        MeteringRectangle.METERING_WEIGHT_MAX.toFloat(),
        CameraConstants.METERING_REGION_FRACTION
    )
    /**
     * Zero weight 3A region, to reset regions per API.
     */
    private val ZERO_WEIGHT_3A_REGION = arrayOf(
        MeteringRectangle(0, 0, 0, 0, 0)
    )
    fun getZeroWeightRegion(): Array<MeteringRectangle> {
        return ZERO_WEIGHT_3A_REGION
    }


    /**
     * Compute 3A regions for a sensor-referenced touch coordinate.
     * Returns a MeteringRectangle[] with length 1.
     *
     * @param nx                x coordinate of the touch point, in normalized portrait
     * coordinates.
     * @param ny                y coordinate of the touch point, in normalized portrait
     * coordinates.
     * @param fraction          Fraction in [0,1]. Multiplied by min(cropRegion.width(),
     * cropRegion.height())
     * to determine the side length of the square MeteringRectangle.
     * @param cropRegion        Crop region of the image.
     * @param sensorOrientation sensor orientation as defined by
     * CameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION).
     */
    private fun regionsForNormalizedCoord(
        nx: Float, ny: Float, fraction: Float, cropRegion: Rect, sensorOrientation: Int
    ): Array<MeteringRectangle> {
        // Compute half side length in pixels.
        val minCropEdge = Math.min(cropRegion.width(), cropRegion.height())
        val halfSideLength = (0.5f * fraction * minCropEdge).toInt()
        // Compute the output MeteringRectangle in sensor space.
        // nx, ny is normalized to the screen.
        // Crop region itself is specified in sensor coordinates.
        // Normalized coordinates, now rotated into sensor space.
        val nsc: PointF? = CameraUtil.normalizedSensorCoordsForNormalizedDisplayCoords(
            nx, ny, sensorOrientation
        )

        val xCenterSensor = (cropRegion.left + nsc!!.x * cropRegion.width()).toInt()
        val yCenterSensor = (cropRegion.top + nsc.y * cropRegion.height()).toInt()
        val meteringRegion = Rect(
            xCenterSensor - halfSideLength,
            yCenterSensor - halfSideLength,
            xCenterSensor + halfSideLength,
            yCenterSensor + halfSideLength
        )
        // Clamp meteringRegion to cropRegion.
        meteringRegion.left = CameraUtil.clamp(
            meteringRegion.left, cropRegion.left,
            cropRegion.right
        )
        meteringRegion.top = CameraUtil.clamp(
            meteringRegion.top, cropRegion.top,
            cropRegion.bottom
        )
        meteringRegion.right = CameraUtil.clamp(
            meteringRegion.right, cropRegion.left,
            cropRegion.right
        )
        meteringRegion.bottom = CameraUtil.clamp(
            meteringRegion.bottom, cropRegion.top,
            cropRegion.bottom
        )
        return arrayOf(
            MeteringRectangle(
                meteringRegion,
                CAMERA2_REGION_WEIGHT.toInt()
            )
        )
    }

    /**
     * Return AF region(s) for a sensor-referenced touch coordinate.
     *
     *
     *
     *
     * Normalized coordinates are referenced to portrait preview window with
     * (0, 0) top left and (1, 1) bottom right. Rotation has no effect.
     *
     *
     * @return AF region(s).
     */
    fun afRegionsForNormalizedCoord(
        nx: Float, ny: Float, cropRegion: Rect, sensorOrientation: Int
    ): Array<MeteringRectangle> {
        return regionsForNormalizedCoord(nx, ny, CameraConstants.METERING_REGION_FRACTION, cropRegion, sensorOrientation)
    }

    /**
     * Return AE region(s) for a sensor-referenced touch coordinate.
     *
     *
     *
     *
     * Normalized coordinates are referenced to portrait preview window with
     * (0, 0) top left and (1, 1) bottom right. Rotation has no effect.
     *
     *
     * @return AE region(s).
     */
    fun aeRegionsForNormalizedCoord(
        nx: Float, ny: Float, cropRegion: Rect, sensorOrientation: Int
    ): Array<MeteringRectangle> {
        return regionsForNormalizedCoord(
            nx, ny, CameraConstants.METERING_REGION_FRACTION,
            cropRegion, sensorOrientation
        )
    }


    /**
     * Calculates sensor crop region for a zoom level (zoom >= 1.0).
     *
     * @return Crop region.
     */
    fun cropRegionForZoom(characteristics: CameraCharacteristics, zoom: Float): Rect {
        val sensor = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        val xCenter = sensor!!.width() / 2
        val yCenter = sensor.height() / 2
        val xDelta = (0.5f * sensor.width() / zoom).toInt()
        val yDelta = (0.5f * sensor.height() / zoom).toInt()
        return Rect(xCenter - xDelta, yCenter - yDelta, xCenter + xDelta, yCenter + yDelta)
    }
}