package android.ai.mycamera.ui

import android.ai.mycamera.R
import android.ai.mycamera.caculator.AspectRatio
import android.ai.mycamera.databinding.ActivityMainBinding
import android.ai.mycamera.utils.CameraConstants
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity(), View.OnClickListener, AspectRatioFragment.AspectRatioListener {
    private lateinit var mCamera2Fragment: CameraBasicFragment
    private var mCurrentFlashIndex = 0
    private lateinit var binding: ActivityMainBinding
    private var mState: STATE = STATE.CAMERA
    private lateinit var mBtnCapture: ImageButton
    private lateinit var mBtnStateCamera: ImageButton
    private lateinit var mBtnGallery: ImageButton

    private lateinit var mImgAspect: ImageButton
    private lateinit var mImgFlash: ImageButton
    private lateinit var mImgSwitch: ImageButton

    private lateinit var mFrameLayout: FrameLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        assignView()

        if (null == savedInstanceState) {
            mCamera2Fragment = CameraBasicFragment.newInstance(this)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mCamera2Fragment)
                .commit()
        }else{
            mCamera2Fragment = supportFragmentManager.findFragmentById(R.id.container) as CameraBasicFragment
        }
    }

    private fun initView(){
        mBtnCapture = binding.imgCapture
        mBtnGallery = binding.imgGallery
        mBtnStateCamera = binding.imgState
        mImgAspect = binding.imgAspectRatio
        mImgFlash = binding.imgFlash
        mImgSwitch = binding.imgSwitch
        mFrameLayout = binding.container
    }

    private fun assignView(){
        mBtnStateCamera.setOnClickListener(this)
        mBtnGallery.setOnClickListener(this)
        mBtnCapture.setOnClickListener(this)
        mImgAspect.setOnClickListener(this)
        mImgFlash.setOnClickListener(this)
        mImgSwitch.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v){
            mBtnStateCamera -> {
                changeState(mState)
            }
            mBtnGallery -> {
            /*    val gallery = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
                startActivityForResult(
                    gallery,
                    REQUEST_CODE_PICK_IMAGE
                )*/
              
            }
            mBtnCapture -> {

            }
            mImgAspect -> {
                val fragmentManager: FragmentManager = supportFragmentManager
                if (fragmentManager.findFragmentByTag(FRAGMENT_DIALOG) == null) {
                    val ratios = mCamera2Fragment.getSupportedAspectRatios()
                    val currentRatio = mCamera2Fragment.getAspectRatio()
                    AspectRatioFragment.newInstance(ratios, currentRatio).show(
                        fragmentManager,
                        FRAGMENT_DIALOG
                    )
                }
            }
            mImgFlash -> {
                mCurrentFlashIndex = (mCurrentFlashIndex + 1) % FLASH_OPTIONS.size

                mImgFlash.setImageResource(FLASH_ICONS[mCurrentFlashIndex])
                mCamera2Fragment.setFlash(FLASH_OPTIONS[mCurrentFlashIndex])
            }
            mImgSwitch -> {
                val facing = mCamera2Fragment.getFacing()
                mCamera2Fragment.setFacing(
                    if (facing == CameraConstants.FACING_FRONT)
                        CameraConstants.FACING_BACK
                    else
                        CameraConstants.FACING_FRONT
                )
                invalidateOptionsMenu()
            }
        }
    }

    private fun setFullScreen() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
    override fun onAspectRatioSelected(ratio: AspectRatio) {
        Toast.makeText(this, ratio.toString(), Toast.LENGTH_SHORT).show()
        mCamera2Fragment.setAspectRatio(ratio)
    }

    private fun changeState(state: STATE){
        if (state == STATE.CAMERA){
            mBtnStateCamera.setImageResource(R.drawable.ic_camera)
            mBtnCapture.setImageResource(R.drawable.cirle_video_start)
            mState = STATE.RECORD
        }else if (state == STATE.RECORD){
            mBtnStateCamera.setImageResource(R.drawable.ic_video_camera)
            mBtnCapture.setImageResource(R.drawable.cam_action_stillshot)
            mState = STATE.CAMERA
        }
    }

    enum class STATE{
        CAMERA, RECORD
    }
    companion object{
        private const val FRAGMENT_DIALOG = "aspect_dialog"
        private const val REQUEST_CODE_PICK_IMAGE = 100
        private val FLASH_OPTIONS = intArrayOf(
            CameraConstants.FLASH_AUTO,
            CameraConstants.FLASH_OFF,
            CameraConstants.FLASH_ON
        )

        private val FLASH_ICONS = intArrayOf(
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on
        )

        private val FLASH_TITLES = intArrayOf(
            R.string.flash_auto,
            R.string.flash_off,
            R.string.flash_on
        )
    }
}