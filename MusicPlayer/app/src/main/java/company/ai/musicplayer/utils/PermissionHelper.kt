package company.ai.musicplayer.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import company.ai.musicplayer.R
import company.ai.musicplayer.controller.UIControlInterface

object PermissionHelper {
    fun hasToAskStoragePermission(activity: Activity) =
        VersioningHelper.isMarshMallow() && ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED

    @JvmStatic
    fun manageAskForReadStoragePermission(
        activity: Activity,
        uiControlInterface: UIControlInterface
    ) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {

            MaterialDialog(activity).show {

                cancelOnTouchOutside(false)

                title(R.string.app_name)

                message(R.string.perm_rationale)
                positiveButton(android.R.string.ok) {
                    askForReadStoragePermission(
                        activity
                    )
                }
               /* negativeButton {
                    uiControlInterface.onDenyPermission()
                }*/
            }
        } else {
            askForReadStoragePermission(
                activity
            )
        }
    }

    private fun askForReadStoragePermission(activity: Activity){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.PERMISSION_REQUEST_READ_EXTERNAL_STORAGE
        )
    }
}