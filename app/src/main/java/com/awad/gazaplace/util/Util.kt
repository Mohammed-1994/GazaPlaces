package com.awad.gazaplace.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.awad.gazaplace.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Util @Inject constructor(@ApplicationContext val context: Context) {


    fun checkForPermissions() {
        val permissions =
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        for (s in permissions) {
            this.requestPermissions(s)
        }
    }

    private fun requestPermissions(permission: String) {
        if (
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )    // You can use the API that requires the permission.

        else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.

            ActivityCompat.requestPermissions(
                context as MainActivity,
                arrayOf(permission), 1
            )
        }
    }

}