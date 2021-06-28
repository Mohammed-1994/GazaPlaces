package com.awad.gazaplace.ui

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.awad.gazaplace.R
import com.awad.gazaplace.databinding.ActivityMapsBinding
import com.awad.gazaplace.maps.MyLocationUpdatesCallback
import com.awad.gazaplace.maps.UpdateLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


private const val TAG = "MapsActivity myTag"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, MyLocationUpdatesCallback {


    private var gotLocation = false
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var lat = 0.0
    private var lng = 0.0
    private var placeName = ""
    var currentLocation = Location("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)
        placeName = intent.getStringExtra("name")!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        UpdateLocation(this).getSettingsResult()
        UpdateLocation(this).getLastKnownLocation()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = placeName

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in place location and move the camera
        val placeLocation = LatLng(lat, lng)
        mMap.addMarker(MarkerOptions().position(placeLocation).title(placeName))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15f))

    }

    fun moveToMyLocation(view: View) {

        Toast.makeText(this, "موقعك هنا", Toast.LENGTH_SHORT).show()
        // Add a marker in Sydney and move the camera
        val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13f))


    }

    override fun onLocationUpdated(location: Location) {
        if (!gotLocation) {
            this.currentLocation = location
            val myLatLng = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(myLatLng).title("أنت هنا"))
        }
        gotLocation = true

    }
}