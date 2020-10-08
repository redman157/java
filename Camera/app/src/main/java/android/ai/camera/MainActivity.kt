package android.ai.camera

import android.Manifest
import android.R
import android.ai.camera.databinding.ActivityMainBinding

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.android.synthetic.main.fragment_camera.camera_container


class MainActivity : AppCompatActivity(), IMainActivity {
    companion object{
        private const val REQUEST_CODE = 1234
        private const val CAMERA ="camera"
        private var bundle: Bundle?= null
    }

    fun onSaveInstance(outState: Bundle) { bundle = outState }
    fun onGetInstance(): Bundle? = bundle

    private var mPermissions : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        checkPermission()
    }

    private fun checkPermission(){
        if (mPermissions){
            if (checkCameraHardware(this)){
                startCamera()
            }else{
                showSnackBar(
                    "You need a camera to use this application",
                    Snackbar.LENGTH_INDEFINITE
                )
            }
        }else{
            Log.d("MMM","verify")
            verifyPermissions()
        }
    }

    /** Check if this device has a camera  */
    private fun checkCameraHardware(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    private fun startCamera(){
        Log.d("MMM","Open camera")
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            fragment_container.id,
            CameraFragment.newInstance(),
            CAMERA
        )
        transaction.commit()
    }

    public fun showSnackBar(title: String, length: Int) {
        val view: View = this.findViewById(R.id.content)
        Snackbar.make(view.rootView, title, length).show()
    }

    private fun verifyPermissions(){
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permissions[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mPermissions = true
            checkPermission()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                permissions,
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (mPermissions) {
                checkPermission()
            } else {
                verifyPermissions()
            }
        }
    }

    override fun setCameraFrontFacing() {
        TODO("Not yet implemented")
    }

    override fun setCameraBackFacing() {
        TODO("Not yet implemented")
    }

    override fun isCameraFrontFacing(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCameraBackFacing(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setFrontCameraId(cameraId: String) {
        TODO("Not yet implemented")
    }

    override fun setBackCameraId(cameraId: String) {
        TODO("Not yet implemented")
    }

    override fun getFrontCameraId(): String {
        TODO("Not yet implemented")
    }

    override fun getBackCameraId(): String {
        TODO("Not yet implemented")
    }

    override fun hideStatusBar() {
        TODO("Not yet implemented")
    }

    override fun showStatusBar() {
        TODO("Not yet implemented")
    }

    override fun hideStillShotWidgets() {
        TODO("Not yet implemented")
    }

    override fun showStillShotWidgets() {
        TODO("Not yet implemented")
    }

    override fun toggleViewStickersFragment() {
        TODO("Not yet implemented")
    }

    override fun addSticker(sticker: Drawable) {
        TODO("Not yet implemented")
    }

    override fun setTrashIconSize(width: Int, height: Int) {
        TODO("Not yet implemented")
    }
}