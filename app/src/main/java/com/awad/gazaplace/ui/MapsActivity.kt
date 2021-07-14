package com.awad.gazaplace.ui

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.awad.gazaplace.R
import com.awad.gazaplace.data.PlaceMetaDataForMap
import com.awad.gazaplace.databinding.ActivityMapsBinding
import com.awad.gazaplace.maps.MyLocationUpdatesCallback
import com.awad.gazaplace.maps.UpdateLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "MapsActivity myTag"

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MyLocationUpdatesCallback {


    @Inject
    lateinit var updateLocation: UpdateLocation

    private var gotLocation = false
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var lat = 0.0
    private var places = ArrayList<PlaceMetaDataForMap>()
    private var lng = 0.0
    private var placeName = ""
    var currentLocation = Location("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Log.d(TAG, "onCreate: ")
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        updateLocation.getSettingsResult()
        updateLocation.getLastKnownLocation()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (intent.hasExtra("name")) {

            placeName = intent.getStringExtra("name")!!
            supportActionBar?.title = placeName
        } else if (intent.hasExtra("places")) {

            placeName = "أماكن بالقرب"
            supportActionBar?.title = placeName

        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady: ")
        mMap = googleMap

        // Add a marker in place location and move the camera
        if (intent.hasExtra("places")) {
            Log.d(TAG, "onMapReady: ${places.size}")
            places = intent.getParcelableArrayListExtra("places")!!
            for (place in places) {
                val placeLocation = LatLng(place.lat, place.lng)
                mMap.addMarker(MarkerOptions().position(placeLocation).title(place.name))
            }
            val myLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        } else {
            val placeLocation = LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(placeLocation).title(placeName))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15f))
        }

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        mMap.clear()
    }

    fun moveToMyLocation(view: View) {

        Toast.makeText(this, "موقعك هنا", Toast.LENGTH_SHORT).show()
        // Add a marker in Sydney and move the camera
        val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13f))


    }

    override fun onLocationUpdated(location: Location) {
        Log.d(TAG, "onLocationUpdated: ")
        this.currentLocation = location
        val myLatLng = LatLng(location.latitude, location.longitude)

        if (!gotLocation) {
            mMap.addMarker(MarkerOptions().position(myLatLng).title("أنت هنا"))
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13f))
        gotLocation = true

    }
}