package com.awad.gazaplace.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.awad.gazaplace.MainActivity
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "UpdateLocation myTag"

class UpdateLocation @Inject constructor(@ApplicationContext val context: Context) {

    lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    fun updateLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context )
        Log.d(TAG, "updateLocation: ")
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d(TAG, "onLocationResult: result not null,")
                    context as MainActivity
                    context.onLocationUpdated(location)
                }
            }
        }

        startLocationUpdates()
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

    fun removeLocationUpdates() {
        Log.d(TAG, "removeLocationUpdates: ")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}

interface MyLocationUpdatesCallback {
    fun onLocationUpdated(location: Location)
}