package com.awad.gazaplace.ui

import android.location.Location
import android.os.Bundle
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

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var gotLocation = false
    private var lat = 0.0
    private var lng = 0.0
    private var currentLocation = Location("")
    private var places = ArrayList<PlaceMetaDataForMap>()
    private var placeName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        if (intent.hasExtra("name"))
            placeName = intent.getStringExtra("name")!!
        else if (intent.hasExtra("places"))
            placeName = "أماكن بالقرب"
        binding.topToolBar.title = placeName
        binding.topToolBar.setNavigationOnClickListener {
            finish()
        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        updateLocation.getSettingsResult()
        updateLocation.getLastKnownLocation()

        // Add a marker in place location and move the camera
        if (intent.hasExtra("places")) {
            places = intent.getParcelableArrayListExtra("places")!!
            for (place in places) {
                val placeLocation = LatLng(place.lat, place.lng)
                mMap.addMarker(MarkerOptions().position(placeLocation).title(place.name))
            }
            val myLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        }

    }

    override fun onStop() {
        super.onStop()
        mMap.clear()
    }

    override fun onResume() {
        super.onResume()
        updateLocation.getSettingsResult()
        updateLocation.getLastKnownLocation()
    }

    fun moveToMyLocation(view: View) {

        Toast.makeText(this, "موقعك هنا", Toast.LENGTH_SHORT).show()
        // Add a marker in Sydney and move the camera
        val myLocation = LatLng(currentLocation.latitude, currentLocation.longitude)

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13f))


    }

    override fun onLocationUpdated(location: Location) {
        this.currentLocation = location
        val myLatLng = LatLng(location.latitude, location.longitude)

        if (!gotLocation) {
            mMap.addMarker(MarkerOptions().position(myLatLng).title("أنت هنا"))
        } else if (intent.hasExtra("places"))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13f))
        else {
            val placeLocation = LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(placeLocation).title(placeName))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLocation, 15f))
        }
        gotLocation = true

    }
}