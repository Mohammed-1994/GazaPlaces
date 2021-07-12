package com.awad.gazaplace.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.PlaceAdapter
import com.awad.gazaplace.databinding.ActivityHomeBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.maps.MyLocationUpdatesCallback
import com.awad.gazaplace.maps.UpdateLocation
import com.awad.gazaplace.ui.fragments.home.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), MyLocationUpdatesCallback {
    private lateinit var binding: ActivityHomeBinding

    companion object {
        private const val TAG = "HomeActivity myTag"
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99

    }

    @Inject
    lateinit var fireStore: FirebaseFirestore

    private var gotLocation = false

    @Inject
    lateinit var firebaseQueries: FirebaseQueries

    @Inject
    lateinit var updateLocation: UpdateLocation

    private lateinit var homeViewModel: HomeViewModel
    private var city = "null"
    private var type = "null"
    var currentLocation = Location("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val navView = binding.navView

        checkLocationPermission()

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
        )
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home)
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

    }
//

    override fun onResume() {
        super.onResume()
        updateLocation.updateLocation()
    }


    override fun onPause() {
        super.onPause()
        updateLocation.removeLocationUpdates()

    }

    override fun onStop() {
        super.onStop()
        updateLocation.removeLocationUpdates()


    }

    fun onTypeRadioGroupClicked(view: View) {
        type = (view as RadioButton).text.toString()
    }

    fun onCityRadioGroupClicked(view: View) {
        city = (view as RadioButton).text.toString()
    }


    // *********     ****///***///***///**/     *********//////////
    // location permissions ************

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
//                        updateLocation.getLastKnownLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    fun searchFilter(): PlaceAdapter {
        return firebaseQueries.queryWithCityAndType(city, type)
    }

    fun searchArea(type: String, radius: Double) {

        firebaseQueries.searchArea(updateLocation.mLocation, type, radius)
    }

    override fun onLocationUpdated(location: Location) {

        this.currentLocation = location
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        if (!gotLocation) {
            this.currentLocation = location
            updateLocation.mLocation = location
            firebaseQueries.nearestLocations(updateLocation.mLocation, 10000.0)

        }
        gotLocation = true

    }


}