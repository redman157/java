package android.ai.mycamera.ui

import android.Manifest
import android.ai.mycamera.R
import android.ai.mycamera.caculator.AspectRatio
import android.ai.mycamera.caculator.SizeCamera
import android.ai.mycamera.caculator.SizeMap
import android.ai.mycamera.databinding.FragmentCameraBasicBinding
import android.ai.mycamera.utils.AutoFocusHelper
import android.ai.mycamera.utils.CameraConstants
import android.ai.mycamera.utils.Utils
import android.ai.mycamera.view.AutoFitTextureView
import android.ai.mycamera.view.FocusView
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.params.MeteringRectangle
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.TextureView.SurfaceTextureListener
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.math.max


class CameraBasicFragment(var mContext: Context) : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {
    companion object{
        private const val SENSOR_ORIENTATION_DEFAULT_DEGREES = 90
        private const val SENSOR_ORIENTATION_INVERSE_DEGREES = 270

        private val DEFAULT_ORIENTATIONS = SparseIntArray().apply {
            append(Surface.ROTATION_0, 90)
            append(Surface.ROTATION_90, 0)
            append(Surface.ROTATION_180, 270)
            append(Surface.ROTATION_270, 180)
        }
        private val INVERSE_ORIENTATIONS = SparseIntArray().apply {
            append(Surface.ROTATION_0, 270)
            append(Surface.ROTATION_90, 180)
            append(Surface.ROTATION_180, 90)
            append(Surface.ROTATION_270, 0)
        }

        private val INTERNAL_FACINGS = SparseIntArray().apply {
            put(CameraConstants.FACING_BACK, CameraCharacteristics.LENS_FACING_BACK)
            put(CameraConstants.FACING_FRONT, CameraCharacteristics.LENS_FACING_FRONT)
        }

        private const val REQUEST_CAMERA = 1
        private const val REQUEST_RECORD_AUDIO = 2
        private const val REQUEST_CODE_PICK_IMAGE = 3

        private const val FRAGMENT_DIALOG = "dialog"
        private val VIDEO_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        /**
         * Tag for the [Log].
         */
        private const val TAG = "Camera2Fragment"

        /**
         * Camera state: Showing camera preview.
         */
        private const val STATE_PREVIEW = 0

        /**
         * Camera state: Waiting for the focus to be locked.
         */
        private const val STATE_WAITING_LOCK = 1

        /**
         * Camera state: Waiting for the exposure to be precapture state.
         */
        private const val STATE_WAITING_PRECAPTURE = 2

        /**
         * Camera state: Waiting for the exposure state to be something other than precapture.
         */
        private const val STATE_WAITING_NON_PRECAPTURE = 3

        /**
         * Camera state: Picture was taken.
         */
        private const val STATE_PICTURE_TAKEN = 4

        /**
         * Max preview width that is guaranteed by Camera2 API
         */
        private const val MAX_PREVIEW_WIDTH = 1920

        /**
         * Max preview height that is guaranteed by Camera2 API
         */
        private const val MAX_PREVIEW_HEIGHT = 1080


        private const val MSG_CAPTURE_PICTURE_WHEN_FOCUS_TIMEOUT = 100

        fun newInstance(context: Context): CameraBasicFragment {
            return CameraBasicFragment(context)
        }
    }

    private lateinit var binding: FragmentCameraBasicBinding

    private var mCropRegion: Rect? = null

    private var mAFRegions = AutoFocusHelper.getZeroWeightRegion()

    private var mAERegions = AutoFocusHelper.getZeroWeightRegion()

    private var mAspectRatio = CameraConstants.DEFAULT_ASPECT_RATIO

    private val mPreviewSizes = SizeMap()

    /**
     * ID of the current [CameraDevice].
     */
    private var mCameraId: String? = null

    /**
     * An [AutoFitTextureView] for camera preview.
     */
    var mTextureView: AutoFitTextureView? = null

    /**
     * The view for manual tap to focus
     */
    var mFocusView: FocusView? = null

    /**
     * A [CameraCaptureSession] for camera preview.
     */
    private var mPreviewSession: CameraCaptureSession? = null

    /**
     * A reference to the opened [CameraDevice].
     */
    private var mCameraDevice: CameraDevice? = null


    /**
     * [CaptureRequest.Builder] for the camera preview
     */
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null

    /**
     * [CaptureRequest] generated by [.mPreviewRequestBuilder]
     */
    private var mPreviewRequest: CaptureRequest? = null


    private var mCameraCharacteristics: CameraCharacteristics? = null

    /**
     * The current state of camera state for taking pictures.
     *
     * @see .mCaptureCallback
     */
    private var mState = STATE_PREVIEW

    /**
     * A [Semaphore] to prevent the app from exiting before closing the camera.
     */
    private val mCameraOpenCloseLock = Semaphore(1)

    /**
     * The current camera auto focus mode
     */
    private var mAutoFocus = true

    /**
     * Whether the current camera device supports auto focus or not.
     */
    private var mAutoFocusSupported = true

    /**
     * The current camera flash mode
     */
    private var mFlash = CameraConstants.FLASH_AUTO

    /**
     * Whether the current camera device supports flash or not.
     */
    private var mFlashSupported = true

    /**
     * The current camera facing mode
     */
    private var mFacing = CameraConstants.FACING_BACK

    /**
     * Whether the current camera device can switch back/front or not.
     */
    private var mFacingSupported = true

    /**
     * Orientation of the camera sensor
     */
    private var mSensorOrientation = 0

    /**
     * The [SizeCamera] of camera preview.
     */
    private var mPreviewSize: android.util.Size? = null

    /**
     * The [SizeCamera] of video recording.
     */
    private var mVideoSizeCamera: Size? = null

    /**
     * MediaRecorder
     */
    private var mMediaRecorder: MediaRecorder? = null

    /**
     * Whether the camera is recording video now
     */
    private var mIsRecordingVideo = false

    /**
     * Whether the camera is manual focusing now
     */
    private var mIsManualFocusing = false

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var mBackgroundThread: HandlerThread? = null

    /**
     * A [Handler] for running tasks in the background.
     */
    private var mBackgroundHandler: Handler? = null

    /**
     * An [ImageReader] that handles still image capture.
     */
    private var mImageReader: ImageReader? = null

    /**
     * The output file path of video recording.
     */
    private var mNextVideoAbsolutePath: String? = null

    /**
     * The output file path of take picture.
     */
    private var mNextPictureAbsolutePath: String? = null


    /**
     * [TextureView.SurfaceTextureListener] handles several lifecycle events on a
     * [TextureView].
     */
    private val mSurfaceTextureListener: SurfaceTextureListener = object : SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            openCamera(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            if (mTextureView != null) {
                mTextureView!!.surfaceTextureListener = null
            }
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }


    /**
     * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
     */
    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(@NonNull cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release()
            mCameraDevice = cameraDevice
            createCameraPreviewSession()
        }

        override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
        }

        override fun onError(@NonNull cameraDevice: CameraDevice, error: Int) {
            mCameraOpenCloseLock.release()
            cameraDevice.close()
            mCameraDevice = null
            val activity: Activity? = activity
            if (null != activity) {
                showToast("Camera is error: $error")
                activity.finish()
            }
        }
    }

    /**
     * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private val mOnImageAvailableListener =
        OnImageAvailableListener { reader ->
            mNextPictureAbsolutePath = getPictureFilePath(activity)
            mBackgroundHandler!!.post(
                ImageSaver(
                    reader.acquireNextImage(),
                    File(mNextPictureAbsolutePath)
                )
            )
            showToast("Picture saved: $mNextPictureAbsolutePath")
            Log.i(TAG, "Picture saved: $mNextPictureAbsolutePath")
        }

    /**
     * A [CameraCaptureSession.CaptureCallback] that handles events related to JPEG capture.
     */
    private val mCaptureCallback: CaptureCallback = object : CaptureCallback() {
        private fun process(result: CaptureResult) {
            //  Log.i(TAG, "CaptureCallback mState: " + mState);
            when (mState) {
                STATE_PREVIEW -> {
                }
                STATE_WAITING_LOCK -> {
                    val afState = result.get(CaptureResult.CONTROL_AF_STATE)
                    Log.i(TAG, "STATE_WAITING_LOCK afState: $afState")
                    if (afState == null) {
                        mState = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                        CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                    ) {
                        // CONTROL_AE_STATE can be null on some devices
                        val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                        if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED
                        ) {
                            mState = STATE_PICTURE_TAKEN
                            captureStillPicture()
                        } else {
                            runPrecaptureSequence()
                        }
                    }
                }
                STATE_WAITING_PRECAPTURE -> {

                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE
                    }
                }
                STATE_WAITING_NON_PRECAPTURE -> {

                    // CONTROL_AE_STATE can be null on some devices
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
                }
            }
        }

        override fun onCaptureProgressed(
            @NonNull session: CameraCaptureSession,
            @NonNull request: CaptureRequest,
            @NonNull partialResult: CaptureResult
        ) {
            process(partialResult)
        }

        override fun onCaptureCompleted(
            @NonNull session: CameraCaptureSession,
            @NonNull request: CaptureRequest,
            @NonNull result: TotalCaptureResult
        ) {
            process(result)
        }
    }

    /**
     * Shows a [Toast] on the UI thread.
     *
     * @param text The message to show
     */
    private fun showToast(text: String) {
        val activity: Activity? = activity
        activity?.runOnUiThread { Toast.makeText(activity, text, Toast.LENGTH_SHORT).show() }
    }

    /**
     * We choose a largest picture size with mAspectRatio
     */
    private fun choosePictureSize(choices: Array<Size>): Size {
        val pictureSizes = mutableListOf(*choices)
        Collections.sort(pictureSizes, CompareSizesByArea())
        val maxIndex = pictureSizes.size - 1
        for (i in maxIndex downTo 0) {
            if (pictureSizes[i].width == pictureSizes[i].height *
                mAspectRatio.x / mAspectRatio.y
            ) {
                return pictureSizes[i]
            }
        }
        return pictureSizes[maxIndex]
    }

    /**
     * We choose a largest video size with mAspectRatio. Also, we don't use sizes
     * larger than 1080p, since MediaRecorder cannot handle such a high-resolution video.
     *
     * @param choices The list of available sizes
     * @return The video size
     */
    private fun chooseVideoSize(choices: Array<Size>): Size {
        val videoSizes = mutableListOf(*choices)
        val supportedVideoSizeCameras: MutableList<Size> = ArrayList()
        Collections.sort(videoSizes, CompareSizesByArea())
        for (i in videoSizes.indices.reversed()) {
            if (videoSizes[i].width <= MAX_PREVIEW_WIDTH &&
                videoSizes[i].height <= MAX_PREVIEW_HEIGHT
            ) {
                supportedVideoSizeCameras.add(videoSizes[i])
                if (videoSizes[i].width == videoSizes[i].height *
                    mAspectRatio.x / mAspectRatio.y
                ) {
                    return videoSizes[i]
                }
            }
        }
        return if (supportedVideoSizeCameras.size > 0) supportedVideoSizeCameras[0] else choices[choices.size - 1]
    }

    /**
     * Given `choices` of `Size`s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     * class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @return The optimal `Size`, or an arbitrary one if none were big enough
     */
    private fun chooseOptimalSize(
        choices: Array<Size>,
        textureViewWidth: Int,
        textureViewHeight: Int,
        maxWidth: Int, maxHeight: Int
    ): Size {
        mPreviewSizes.clear()
        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough: MutableList<Size> = ArrayList()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough: MutableList<Size> = ArrayList()
        val w: Int = mAspectRatio.x
        val h: Int = mAspectRatio.y
        for (option in choices) {
            if (option.width <= maxWidth && option.height <= maxHeight) {
                mPreviewSizes.add(SizeCamera(option.width, option.height))
                if (option.height == option.width * h / w) {
                    if (option.width >= textureViewWidth &&
                        option.height >= textureViewHeight
                    ) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        return if (bigEnough.size > 0) {
            Collections.min(bigEnough, CompareSizesByArea())
        } else if (notBigEnough.size > 0) {
            Collections.max(notBigEnough, CompareSizesByArea())
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size")
            mAspectRatio = AspectRatio.of(4, 3)
            val sortedSet: SortedSet<SizeCamera>? = mPreviewSizes.sizes(mAspectRatio)
            if (sortedSet != null) {
                val lastSizeCamera: SizeCamera = sortedSet.last()
                return Size(lastSizeCamera.width, lastSizeCamera.height)
            }
            mAspectRatio = AspectRatio.of(choices[0].width, choices[0].height)
            choices[0]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCameraBasicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTextureView = binding.cameraContainer
        mFocusView = binding.focusView
        mTextureView!!.setGestureListener(object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                setFocusViewWidthAnimation(e.x, e.y)
                setManualFocusAt(e.x.toInt(), e.y.toInt())
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    override fun onPause() {
        stop()
        super.onPause()
    }

    private fun start() {
        startBackgroundThread()

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView!!.isAvailable) {
            openCamera(mTextureView!!.width, mTextureView!!.height)
        } else {
            mTextureView!!.surfaceTextureListener = mSurfaceTextureListener
        }
    }

    private fun stop() {
        closeCamera()
        stopBackgroundThread()
    }

    /**
     * Focus view animation
     */
    fun setFocusViewWidthAnimation(x: Float, y: Float) {
        mFocusView!!.visibility = View.VISIBLE
        mFocusView!!.x = x - mFocusView!!.width / 2
        mFocusView!!.y = y - mFocusView!!.height / 2
        val scaleX: ObjectAnimator = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1f, 0.5f)
        val scaleY: ObjectAnimator = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1f, 0.5f)
        val alpha = ObjectAnimator.ofFloat(mFocusView, "alpha", 1f, 0.3f, 1f, 0.3f, 1f, 0.3f, 1f)
        val animSet = AnimatorSet()
        animSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mFocusView!!.visibility = View.INVISIBLE
            }
        })
        animSet.play(scaleX).with(scaleY).before(alpha)
        animSet.duration = 300
        animSet.start()
    }

    private fun hasPermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(activity!!, permission)
                !== PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    /**
     * Gets whether you should show UI with rationale for requesting permissions.
     *
     * @param permissions The permissions your app wants to request.
     * @return Whether you can show permission rationale UI.
     */
    private fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mContext as Activity,
                    permission
                )) {
                return true
            }
        }
        return false
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(VIDEO_PERMISSIONS)) {
            ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
        } else {
            ActivityCompat.requestPermissions(
                mContext as Activity,
                VIDEO_PERMISSIONS,
                REQUEST_CAMERA
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            val uri: Uri? = data.data
            uri?.let {
                mContext.contentResolver.notifyChange(it, null)
                val imageFile = File(Utils.getRealPathFromURI(it, mContext))
                val scaleBitmap = Utils.downscaleToMaxAllowedDimension(BitmapFactory.decodeFile(imageFile.absolutePath))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionsResult")
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.size == VIDEO_PERMISSIONS.size) {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ErrorDialog.newInstance(getString(R.string.request_permission))
                            .show(childFragmentManager, FRAGMENT_DIALOG)
                        break
                    }
                }
            } else {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                    .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        } else{
            super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        }
    }

    /**
     * Setup member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private fun setupCameraOutputs(width: Int, height: Int) {
        val activity: Activity? = activity
        val internalFacing = INTERNAL_FACINGS[mFacing]
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val cameraIds = manager.cameraIdList
            mFacingSupported = cameraIds.size > 1
            for (cameraId in cameraIds) {
                mCameraCharacteristics = manager.getCameraCharacteristics(cameraId)
                val facing = mCameraCharacteristics!!.get(CameraCharacteristics.LENS_FACING)
                if (facing == null || facing != internalFacing) {
                    continue
                }
                val map = mCameraCharacteristics!!.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?: continue

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = activity.windowManager.defaultDisplay.rotation
                mSensorOrientation =
                    mCameraCharacteristics!!.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                var swappedDimensions = false
                when (displayRotation) {
                    Surface.ROTATION_0, Surface.ROTATION_180 -> if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                        swappedDimensions = true
                    }
                    Surface.ROTATION_90, Surface.ROTATION_270 -> if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                        swappedDimensions = true
                    }
                    else -> Log.e(TAG, "Display rotation is invalid: $displayRotation")
                }
                val displaySize = Point()
                activity.windowManager.defaultDisplay.getRealSize(displaySize)
                var rotatedPreviewWidth = width
                var rotatedPreviewHeight = height
                var maxPreviewWidth = displaySize.x
                var maxPreviewHeight = displaySize.y
                if (swappedDimensions) {
                    rotatedPreviewWidth = height
                    rotatedPreviewHeight = width
                    maxPreviewWidth = displaySize.y
                    maxPreviewHeight = displaySize.x
                }
                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH
                }
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT
                }

                // Danger, W.R.! Attempting to use too large a preview size could exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(
                    map.getOutputSizes(SurfaceTexture::class.java),
                    rotatedPreviewWidth, rotatedPreviewHeight,
                    maxPreviewWidth,
                    maxPreviewHeight
                )
                mVideoSizeCamera = chooseVideoSize(map.getOutputSizes(MediaRecorder::class.java))

                // For still image captures, we use the largest available size.
                val largest = choosePictureSize(map.getOutputSizes(ImageFormat.JPEG))
                mImageReader = ImageReader.newInstance(
                    largest.width, largest.height,
                    ImageFormat.JPEG,  /*maxImages*/2
                )
                mImageReader!!.setOnImageAvailableListener(
                    mOnImageAvailableListener, mBackgroundHandler
                )

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                val orientation = resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView!!.setAspectRatio(
                        mPreviewSize!!.width, mPreviewSize!!.height
                    )
                } else {
                    mTextureView!!.setAspectRatio(
                        mPreviewSize!!.height, mPreviewSize!!.width
                    )
                }
                checkAutoFocusSupported()
                checkFlashSupported()
                mCropRegion = AutoFocusHelper.cropRegionForZoom(
                    mCameraCharacteristics!!,
                    CameraConstants.ZOOM_REGION_DEFAULT.toFloat()
                )
                mCameraId = cameraId
                Log.i(TAG, "CameraId: $mCameraId ,isFlashSupported: $mFlashSupported")
                return
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                .show(childFragmentManager, FRAGMENT_DIALOG)
        }
    }

    /**
     * Check if the auto focus is supported.
     */
    private fun checkAutoFocusSupported() {
        val modes = mCameraCharacteristics!!.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
        mAutoFocusSupported = !(modes == null || modes.size == 0 ||
                modes.size == 1 && modes[0] == CameraCharacteristics.CONTROL_AF_MODE_OFF)
    }

    /**
     * Check if the flash is supported.
     */
    private fun checkFlashSupported() {
        val available = mCameraCharacteristics!!.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
        mFlashSupported = available ?: false
    }

    /**
     * Opens the camera specified by [Camera2Fragment.mCameraId].
     */
    @SuppressLint("MissingPermission")
    private fun openCamera(width: Int, height: Int) {
        if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
            requestCameraPermission()
            return
        }
        setupCameraOutputs(width, height)
        configureTransform(width, height)
        val activity: Activity? = activity
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            if (!mCameraOpenCloseLock.tryAcquire(
                    CameraConstants.OPEN_CAMERA_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS
                )
            ) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            mMediaRecorder = MediaRecorder()
            manager.openCamera(mCameraId!!, mStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    /**
     * Closes the current [CameraDevice].
     */
    private fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()
            if (null != mPreviewSession) {
                mPreviewSession!!.close()
                mPreviewSession = null
            }
            if (null != mCameraDevice) {
                mCameraDevice!!.close()
                mCameraDevice = null
            }
            if (null != mMediaRecorder) {
                mMediaRecorder!!.release()
                mMediaRecorder = null
            }
            if (null != mImageReader) {
                mImageReader!!.close()
                mImageReader = null
            }
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            mCameraOpenCloseLock.release()
        }
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread!!.start()
        mBackgroundHandler = object : Handler(mBackgroundThread!!.looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MSG_CAPTURE_PICTURE_WHEN_FOCUS_TIMEOUT -> {
                        mState = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private fun stopBackgroundThread() {
        mBackgroundThread!!.quitSafely()
        try {
            mBackgroundThread!!.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * Creates a new [CameraCaptureSession] for camera preview.
     */
    private fun createCameraPreviewSession() {
        try {
            val texture = mTextureView!!.surfaceTexture!!

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder!!.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice!!.createCaptureSession(
                mutableListOf(surface, mImageReader!!.surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (null == mCameraDevice) {
                            return
                        }

                        // When the session is ready, we start displaying the preview.
                        mPreviewSession = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
//                            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
//                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                            updateAutoFocus()
                            // Flash is automatically enabled when necessary.
                            updateFlash(mPreviewRequestBuilder)

                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewRequestBuilder!!.build()
                            mPreviewSession!!.setRepeatingRequest(
                                mPreviewRequest!!,
                                mCaptureCallback, mBackgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(
                        @NonNull cameraCaptureSession: CameraCaptureSession
                    ) {
                        showToast("Create preview configure failed")
                    }
                }, mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Configures the necessary [Matrix] transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setupCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        val activity: Activity? = activity
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return
        }
        val rotation = activity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(
            0f, 0f, mPreviewSize!!.height.toFloat(),
            mPreviewSize!!.width.toFloat()
        )
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
            val scale = Math.max(
                viewHeight.toFloat() / mPreviewSize!!.height,
                viewWidth.toFloat() / mPreviewSize!!.width
            )
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate(90 * (rotation - 2).toFloat(), centerX, centerY)
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        mTextureView!!.setTransform(matrix)
    }

    fun getSupportedAspectRatios(): Set<AspectRatio?> {
        return mPreviewSizes.ratios()
    }

    fun getAspectRatio(): AspectRatio {
        return mAspectRatio
    }

    fun setAspectRatio(aspectRatio: AspectRatio?): Boolean {
        if (aspectRatio == null || aspectRatio == mAspectRatio ||
            !mPreviewSizes.ratios().contains(aspectRatio)
        ) {
            return false
        }
        mAspectRatio = aspectRatio
        if (isCameraOpened()) {
            stop()
            start()
        }
        return true
    }

    fun isCameraOpened(): Boolean {
        return mCameraDevice != null
    }

    fun setFacing(facing: Int) {
        if (mFacing == facing) {
            return
        }
        mFacing = facing
        if (isCameraOpened()) {
            stop()
            start()
        }
    }

    fun getFacing(): Int {
        return mFacing
    }

    /**
     * The facing is supported or not.
     */
    fun isFacingSupported(): Boolean {
        return mFacingSupported
    }

    fun setFlash(flash: Int) {
        if (mFlash == flash) {
            return
        }
        val saved = mFlash
        mFlash = flash
        if (mPreviewRequestBuilder != null) {
            updateFlash(mPreviewRequestBuilder)
            if (mPreviewSession != null) {
                try {
                    mPreviewSession!!.setRepeatingRequest(
                        mPreviewRequestBuilder!!.build(),
                        mCaptureCallback, mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    mFlash = saved // Revert
                }
            }
        }
    }

    fun getFlash(): Int {
        return mFlash
    }

    /**
     * Updates the internal state of flash to [.mFlash].
     */
    fun updateFlash(requestBuilder: CaptureRequest.Builder?) {
        if (!mFlashSupported) {
            return
        }
        when (mFlash) {
            CameraConstants.FLASH_OFF -> {
                requestBuilder!!.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON
                )
                requestBuilder.set(
                    CaptureRequest.FLASH_MODE,
                    CaptureRequest.FLASH_MODE_OFF
                )
            }
            CameraConstants.FLASH_ON -> {
                requestBuilder!!.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH
                )
                requestBuilder.set(
                    CaptureRequest.FLASH_MODE,
                    CaptureRequest.FLASH_MODE_OFF
                )
            }
            CameraConstants.FLASH_TORCH -> {
                requestBuilder!!.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON
                )
                requestBuilder.set(
                    CaptureRequest.FLASH_MODE,
                    CaptureRequest.FLASH_MODE_TORCH
                )
            }
            CameraConstants.FLASH_AUTO -> {
                requestBuilder!!.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                )
                requestBuilder.set(
                    CaptureRequest.FLASH_MODE,
                    CaptureRequest.FLASH_MODE_OFF
                )
            }
            CameraConstants.FLASH_RED_EYE -> {
                requestBuilder!!.set(
                    CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE
                )
                requestBuilder.set(
                    CaptureRequest.FLASH_MODE,
                    CaptureRequest.FLASH_MODE_OFF
                )
            }
        }
    }

    /**
     * The flash is supported or not.
     */
    fun isFlashSupported(): Boolean {
        return mFlashSupported
    }

    fun setAutoFocus(autoFocus: Boolean) {
        if (mAutoFocus == autoFocus) {
            return
        }
        mAutoFocus = autoFocus
        if (mPreviewRequestBuilder != null) {
            updateAutoFocus()
            if (mPreviewSession != null) {
                try {
                    mPreviewSession!!.setRepeatingRequest(
                        mPreviewRequestBuilder!!.build(),
                        mCaptureCallback, mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    mAutoFocus = !mAutoFocus // Revert
                }
            }
        }
    }

    fun getAutoFocus(): Boolean {
        return mAutoFocus
    }

    /**
     * The auto focus is supported or not.
     */
    fun isAutoFocusSupported(): Boolean {
        return mAutoFocusSupported
    }

    /**
     * Updates the internal state of auto-focus to [.mAutoFocus].
     */
    fun updateAutoFocus() {
        if (mAutoFocus) {
            if (!mAutoFocusSupported) {
                mPreviewRequestBuilder!!.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_OFF
                )
            } else {
                if (mIsRecordingVideo) {
                    mPreviewRequestBuilder!!.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO
                    )
                } else {
                    mPreviewRequestBuilder!!.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )
                }
            }
        } else {
            mPreviewRequestBuilder!!.set(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_OFF
            )
        }
        mAFRegions = AutoFocusHelper.getZeroWeightRegion()
        mAERegions = AutoFocusHelper.getZeroWeightRegion()
        mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_REGIONS, mAFRegions)
        mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AE_REGIONS, mAERegions)
    }

    /**
     * Updates the internal state of manual focus.
     */
    private fun updateManualFocus(x: Float, y: Float, focusAreaTouch: MeteringRectangle) {
        val sensorOrientation = mCameraCharacteristics!!.get(CameraCharacteristics.SENSOR_ORIENTATION)!!

        //Now add a new AF trigger with focus region

        mAFRegions = AutoFocusHelper.afRegionsForNormalizedCoord(
            x,
            y,
            mCropRegion!!,
            sensorOrientation
        )
        mAERegions = AutoFocusHelper.aeRegionsForNormalizedCoord(
            x,
            y,
            mCropRegion!!,
            sensorOrientation
        )
        if (isMeteringAreaAFSupported()) {
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_REGIONS, arrayOf(focusAreaTouch))
        }else{
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_REGIONS, mAFRegions)
        }

        mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AE_REGIONS, mAERegions)
        mPreviewRequestBuilder!!.set(
            CaptureRequest.CONTROL_AF_MODE,
            CaptureRequest.CONTROL_AF_MODE_AUTO
        )
    }

    private fun isMeteringAreaAFSupported(): Boolean {
        val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var characteristics: CameraCharacteristics? = null
        characteristics = try {
            manager.getCameraCharacteristics(mCameraId!!)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
            return false
        }
        return characteristics!!.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF)!! >= 1
    }


    fun setManualFocusAt(x: Int, y: Int) {
        val mDisplayOrientation = activity!!.windowManager.defaultDisplay.rotation
        val manager = (mContext as MainActivity).getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = mCameraId?.let { manager.getCameraCharacteristics(it) }!!
        val sensorArraySize: Rect =
            characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)!!
        val points = FloatArray(2)
        points[0] = x.toFloat() / mTextureView!!.width *sensorArraySize.width()
        points[1] = y.toFloat() / mTextureView!!.height * sensorArraySize.height()
        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(mDisplayOrientation.toFloat(), 0.5f, 0.5f)
        rotationMatrix.mapPoints(points)

        val halfTouchWidth = 150 //(int)motionEvent.getTouchMajor();

        // TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
        // TODO: this doesn't represent actual touch size in pixel. Values range in [3, 10]...
        val halfTouchHeight = 150 //(int)motionEvent.getTouchMinor();

        val focusAreaTouch = MeteringRectangle(
            max(x - halfTouchWidth, 0),
            max(y - halfTouchHeight, 0),
            halfTouchWidth * 2,
            halfTouchHeight * 2,
            MeteringRectangle.METERING_WEIGHT_MAX - 1
        )

        if (mPreviewRequestBuilder != null) {
            mIsManualFocusing = true
            updateManualFocus(points[0], points[1], focusAreaTouch)
            if (mPreviewSession != null) {
                try {
                    mPreviewRequestBuilder!!.set(
                        CaptureRequest.CONTROL_AF_TRIGGER,
                        CaptureRequest.CONTROL_AF_TRIGGER_START
                    )
                    mPreviewSession!!.capture(
                        mPreviewRequestBuilder!!.build(),
                        null,
                        mBackgroundHandler
                    )
                    mPreviewRequestBuilder!!.set(
                        CaptureRequest.CONTROL_AF_TRIGGER,
                        CaptureRequest.CONTROL_AF_TRIGGER_IDLE
                    )
                    mPreviewSession!!.setRepeatingRequest(
                        mPreviewRequestBuilder!!.build(),
                        null, mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    Log.e(TAG, "Failed to set manual focus.", e)
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "Failed to set manual focus.", e)
                }
            }
            resumeAutoFocusAfterManualFocus()
        }
    }

    private val mAutoFocusRunnable = Runnable {
        if (mPreviewRequestBuilder != null) {
            mIsManualFocusing = false
            updateAutoFocus()
            if (mPreviewSession != null) {
                try {
                    mPreviewSession!!.setRepeatingRequest(
                        mPreviewRequestBuilder!!.build(),
                        mCaptureCallback, mBackgroundHandler
                    )
                } catch (e: CameraAccessException) {
                    Log.e(TAG, "Failed to set manual focus.", e)
                }
            }
        }
    }

    private fun resumeAutoFocusAfterManualFocus() {
        mBackgroundHandler!!.removeCallbacks(mAutoFocusRunnable)
        mBackgroundHandler!!.postDelayed(
            mAutoFocusRunnable,
            CameraConstants.FOCUS_HOLD_MILLIS.toLong()
        )
    }

    /**
     * Initiate a still image capture.
     */
    fun takePicture() {
        if (!mIsManualFocusing && mAutoFocus && mAutoFocusSupported) {
            Log.i(TAG, "takePicture lockFocus")
            capturePictureWhenFocusTimeout() //Sometimes, camera do not focus in some devices.
            lockFocus()
        } else {
            Log.i(TAG, "takePicture captureStill")
            captureStillPicture()
        }
    }

    /**
     * Capture picture when auto focus timeout
     */
    private fun capturePictureWhenFocusTimeout() {
        if (mBackgroundHandler != null) {
            mBackgroundHandler!!.sendEmptyMessageDelayed(
                MSG_CAPTURE_PICTURE_WHEN_FOCUS_TIMEOUT,
                CameraConstants.AUTO_FOCUS_TIMEOUT_MS
            )
        }
    }

    /**
     * Remove capture message, because auto focus work correctly.
     */
    private fun removeCaptureMessage() {
        if (mBackgroundHandler != null) {
            mBackgroundHandler!!.removeMessages(MSG_CAPTURE_PICTURE_WHEN_FOCUS_TIMEOUT)
        }
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private fun lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder!!.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_START
            )
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK
            mPreviewSession!!.capture(
                mPreviewRequestBuilder!!.build(),
                mCaptureCallback,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private fun unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder!!.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL
            )
            mPreviewSession!!.capture(
                mPreviewRequestBuilder!!.build(), mCaptureCallback,
                mBackgroundHandler
            )
            updateAutoFocus()
            updateFlash(mPreviewRequestBuilder)
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW
            mPreviewRequestBuilder!!.set(
                CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_IDLE
            )
            mPreviewSession!!.setRepeatingRequest(
                mPreviewRequest!!, mCaptureCallback,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in [.mCaptureCallback] from [.lockFocus].
     */
    private fun runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder!!.set(
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
            )
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE
            mPreviewSession!!.capture(
                mPreviewRequestBuilder!!.build(),
                mCaptureCallback,
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * [.mCaptureCallback] from both [.lockFocus].
     */
    private fun captureStillPicture() {
        try {
            removeCaptureMessage()
            val activity: Activity? = activity
            if (null == activity || null == mCameraDevice) {
                return
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            val captureBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(mImageReader!!.surface)

            // Use the same AE and AF modes as the preview.
//            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
//                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//            updateAutoFocus();
            updateFlash(captureBuilder)

            // Orientation
            val rotation = activity.windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation))
            val CaptureCallback: CaptureCallback = object : CaptureCallback() {
                override fun onCaptureCompleted(
                    @NonNull session: CameraCaptureSession,
                    @NonNull request: CaptureRequest,
                    @NonNull result: TotalCaptureResult
                ) {
                    unlockFocus()
                }
            }
            mPreviewSession!!.stopRepeating()
            mPreviewSession!!.capture(captureBuilder.build(), CaptureCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private fun getOrientation(rotation: Int): Int {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from DEFAULT_ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (DEFAULT_ORIENTATIONS[rotation] + mSensorOrientation + 270) % 360
    }

    private fun getVideoFilePath(context: Context?): String? {
        val dir = context!!.getExternalFilesDir(null)
        return ((if (dir == null) "" else dir.absolutePath + "/")
                + System.currentTimeMillis() + ".mp4")
    }

    private fun getPictureFilePath(context: Context?): String? {
        val dir = context!!.getExternalFilesDir(null)
        return ((if (dir == null) "" else dir.absolutePath + "/")
                + System.currentTimeMillis() + ".jpg")
    }

    @Throws(IOException::class)
    private fun setupMediaRecorder() {
        val activity = activity ?: return
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mMediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mNextVideoAbsolutePath = getVideoFilePath(getActivity())
        mMediaRecorder!!.setOutputFile(mNextVideoAbsolutePath)
        mMediaRecorder!!.setVideoEncodingBitRate(10000000)
        mMediaRecorder!!.setVideoFrameRate(30)
        mMediaRecorder!!.setVideoSize(mVideoSizeCamera!!.width, mVideoSizeCamera!!.height)
        mMediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        val rotation = activity.windowManager.defaultDisplay.rotation
        when (mSensorOrientation) {
            SENSOR_ORIENTATION_DEFAULT_DEGREES -> mMediaRecorder!!.setOrientationHint(
                DEFAULT_ORIENTATIONS[rotation]
            )
            SENSOR_ORIENTATION_INVERSE_DEGREES -> mMediaRecorder!!.setOrientationHint(
                INVERSE_ORIENTATIONS[rotation]
            )
        }
        mMediaRecorder!!.prepare()
    }


    /**
     * Start recording video
     */
    fun startRecordingVideo() {
        if (null == mCameraDevice || !mTextureView!!.isAvailable || null == mPreviewSize) {
            return
        }
        try {
            mIsRecordingVideo = true
            setupMediaRecorder()
            val texture = mTextureView!!.surfaceTexture!!
            texture.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)
            mPreviewRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
            val surfaces: MutableList<Surface> = ArrayList()

            // Set up Surface for the camera preview
            val previewSurface = Surface(texture)
            surfaces.add(previewSurface)
            mPreviewRequestBuilder!!.addTarget(previewSurface)

            // Set up Surface for the MediaRecorder
            val recorderSurface = mMediaRecorder!!.surface
            surfaces.add(recorderSurface)
            mPreviewRequestBuilder!!.addTarget(recorderSurface)

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            mCameraDevice!!.createCaptureSession(
                surfaces,
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                        mPreviewSession = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
//                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
//                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                            updateAutoFocus()
                            // Flash is automatically enabled when necessary.
                            updateFlash(mPreviewRequestBuilder)

                            // For test
                            val stabilizationMode =
                                mPreviewRequestBuilder!!.get(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE)
                            if (stabilizationMode != null &&
                                stabilizationMode == CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_OFF
                            ) {
                                mPreviewRequestBuilder!!.set(
                                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE,
                                    CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON
                                )
                            }

                            // Finally, we start displaying the camera preview.
                            mPreviewRequest = mPreviewRequestBuilder!!.build()
                            mPreviewSession!!.setRepeatingRequest(
                                mPreviewRequest!!,
                                null,
                                mBackgroundHandler
                            )
                            activity!!.runOnUiThread { // Start recording
                                mMediaRecorder!!.start()
                            }
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(@NonNull cameraCaptureSession: CameraCaptureSession) {
                        showToast("Start recording video configure failed")
                    }
                },
                mBackgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * Stop recording video
     */
    fun stopRecordingVideo() {
        activity!!.runOnUiThread { // UI
            mIsRecordingVideo = false
            showToast("Video saved: $mNextVideoAbsolutePath")
            Log.i(TAG, "Video saved: $mNextVideoAbsolutePath")
            closeCamera()
            openCamera(mTextureView!!.width, mTextureView!!.height)
        }
    }

    fun isRecordingVideo(): Boolean {
        return mIsRecordingVideo
    }


    /**
     * Saves a JPEG [Image] into the specified [File].
     */
    private class ImageSaver(
        /**
         * The JPEG image
         */
        private val mImage: Image,
        /**
         * The file we save the image into.
         */
        private val mFile: File
    ) :
        Runnable {
        override fun run() {
            val buffer = mImage.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer[bytes]
            var output: FileOutputStream? = null
            try {
                output = FileOutputStream(mFile)
                output.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                mImage.close()
                if (null != output) {
                    try {
                        output.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    /**
     * Compares two `Size`s based on their areas.
     */
    private class CompareSizesByArea : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            return java.lang.Long.signum(
                lhs.width.toLong() * lhs.height -
                        rhs.height.toLong() * rhs.height
            )
        }
    }

    /**
     * Shows an error message dialog.
     */
    class ErrorDialog :DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val activity = activity
            return AlertDialog.Builder(activity)
                .setMessage(arguments!!.getString(ARG_MESSAGE))
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ -> activity!!.finish() }
                .create()
        }

        companion object {
            private const val ARG_MESSAGE = "message"
            fun newInstance(message: String?): ErrorDialog {
                val dialog = ErrorDialog()
                val args = Bundle()
                args.putString(ARG_MESSAGE, message)
                dialog.arguments = args
                return dialog
            }
        }
    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    inner class ConfirmationDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val parent = parentFragment
            return AlertDialog.Builder(activity)
                .setMessage(R.string.request_permission)
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    ActivityCompat.requestPermissions(
                        mContext as Activity, arrayOf(Manifest.permission.CAMERA),
                        REQUEST_CAMERA
                    )
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { dialog, which ->
                    val activity = parent!!.activity
                    activity?.finish()
                }
                .create()
        }
    }
}