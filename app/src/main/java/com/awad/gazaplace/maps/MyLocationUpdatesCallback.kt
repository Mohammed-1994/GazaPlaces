package com.awad.gazaplace.maps

import android.location.Location

interface MyLocationUpdatesCallback {
    fun onLocationUpdated(location: Location)
}