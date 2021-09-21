package sg.searchhouse.agentconnect.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// TODO: Include into dagger with context constructor
object PermissionUtil {
    const val REQUEST_CODE_CALL = 101
    const val REQUEST_CODE_LOCATION = 102
    const val REQUEST_CODE_CAMERA = 103
    const val REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE = 104
    const val REQUEST_CODE_READ_CONTACTS = 105

    //Call permission
    fun requestCallPermission(activity: Activity): Boolean {
        val permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE)

        return if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CODE_CALL
            )
            //not granted
            false
        } else {
            //granted
            true
        }
    }

    fun isLocationPermissionsGranted(context: Context): Boolean {
        val coarseLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fineLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermission == PackageManager.PERMISSION_GRANTED
    }

    // Return true if permission already granted
    fun maybeRequestLocationPermissions(activity: Activity): Boolean {
        return if (!isLocationPermissionsGranted(activity)) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_LOCATION
            )
            //not granted
            false
        } else {
            //granted
            true
        }
    }

    fun requestCameraPermission(activity: Activity): Boolean {
        val permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA), REQUEST_CODE_CAMERA
            )
            //not granted
            false
        } else {
            //granted
            true
        }
    }

    fun requestReadWriteExternalStoragePermission(activity: Activity): Boolean {
        //For both fragment and activity
        val readExternalStoragePermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeExternalStoragePermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED ||
            writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_READ_WRITE_EXTERNAL_STORAGE
            )
            //not granted
            false
        } else {
            //granted
            true
        }
    }

    fun requestReadContactsPermission(activity: Activity): Boolean {
        val permission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS
            )
            false
        } else {
            true
        }
    }

    fun handlePermissionResult(
        requestedPermission: String,
        permissions: Array<String>,
        grantResults: IntArray,
        action: () -> Unit
    ) {
        val callPhoneIndex = permissions.indexOfFirst {
            TextUtils.equals(
                it,
                requestedPermission
            )
        }
        if (callPhoneIndex != -1) {
            if (grantResults[callPhoneIndex] == PackageManager.PERMISSION_GRANTED) {
                action.invoke()
            }
        }
    }
}