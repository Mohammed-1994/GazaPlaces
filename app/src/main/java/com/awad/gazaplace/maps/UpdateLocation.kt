package com.awad.gazaplace.maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "UpdateLocation myTag"
const val REQUEST_CHECK_SETTINGS = 1000

class UpdateLocation @Inject constructor(@ApplicationContext val context: Context) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var listener: MyLocationUpdatesCallback

    private lateinit var locationCallback: LocationCallback

    fun updateLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: result not null,")
                    listener = context as MyLocationUpdatesCallback
                    listener.onLocationUpdated(location)
                    removeLocationUpdates()
                }
            }
        }

        startLocationUpdates()
    }

    fun getLastKnownLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            return
        fusedLocationClient.lastLocation.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.e(TAG, "getLastKnownLocation: Error", it.exception)
                updateLocation()
            } else {
                if (it.result != null) {
                    Log.d(TAG, "getLastKnownLocation: last location not null")
                    listener = context as MyLocationUpdatesCallback
                    listener.onLocationUpdated(it.result)

                } else {
                    Log.d(TAG, "getLastKnownLocation: last location is null")
                    updateLocation()
                }
            }
        }
    }

    private fun startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates: ")
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            return

        fusedLocationClient.requestLocationUpdates(
            createLocationRequest()!!,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    private fun createLocationRequest(): LocationRequest? {
        Log.d(TAG, "createLocationRequest: ")

        return LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    fun getSettingsResult() {

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(createLocationRequest()!!)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val result: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                it.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        e as ResolvableApiException
                        e.startResolutionForResult(
                            context as Activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                } else if (e.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {

                }

                e.printStackTrace()
            }
        }


    }

    fun removeLocationUpdates() {
        Log.d(TAG, "removeLocationUpdates: ")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}

interface MyLocationUpdatesCallback {
    fun onLocationUpdated(location: Location)
}