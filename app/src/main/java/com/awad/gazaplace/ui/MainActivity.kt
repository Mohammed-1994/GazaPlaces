package com.awad.gazaplace.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.MainAdapter
import com.awad.gazaplace.adapters.PlaceAdapter
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.databinding.ActivityMainBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.maps.MyLocationUpdatesCallback
import com.awad.gazaplace.maps.UpdateLocation
import com.awad.gazaplace.util.Constants
import com.awad.gazaplace.util.FilterMainResultDialog
import com.awad.gazaplace.util.NoticeDialogListener
import com.awad.gazaplace.util.Util
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


const val TAG = "MainActivity myTag"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MyLocationUpdatesCallback, NoticeDialogListener {

    @Inject
    lateinit var fireStore: FirebaseFirestore

    @Inject
    lateinit var storageReference: StorageReference


    private lateinit var binding: ActivityMainBinding

    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var mainAdapter: MainAdapter

    private val util: Util = Util(this)
    private val updateLocation: UpdateLocation = UpdateLocation(this)
    private var currentLocation = Location("")

    private lateinit var firebaseQueries: FirebaseQueries
    private var gotLocation = false
    var radius = 10000.0
    private var city = "غزة"
    private var type = "مطعم"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        Log.d(TAG, "onCreate: ")
        setContentView(binding.root)

        checkLocationPermission()
        firebaseQueries = FirebaseQueries(this, fireStore)
        placeAdapter = firebaseQueries.queryWithCityAndType(city, type)
        mainAdapter = MainAdapter(this)

        binding.recyclerView.adapter = mainAdapter
        updateLocation.getSettingsResult()
        updateLocation.getLastKnownLocation()


    }


    override fun onLocationUpdated(location: Location) {
        if (!gotLocation) {
            this.currentLocation = location
            Log.d(TAG, "onLocationUpdated: ")
            firebaseQueries.nearestLocations(location, radius)
            mainAdapter.findDistance(location)
        }
        gotLocation = true

    }


    fun submitNearestPlacesToAdapter(nearestPlaces: MutableList<PlaceMetaData>) {
        binding.recyclerView.adapter = mainAdapter
        mainAdapter.submitPlaces(nearestPlaces)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_main_result -> {
                showFilterResultDialog(Constants.FILTER_MAIN_RESULT_OPTION)
                return true
            }
            R.id.search_area -> {
                showFilterResultDialog(Constants.SEARCH_AREA_OPTION)

                return true
            }
            R.id.advanced_search -> {

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun searchArea(radius: Double) {

        firebaseQueries.searchArea(currentLocation, type, radius)
    }

    private fun showFilterResultDialog(dialogType: Int) {

        FilterMainResultDialog(dialogType).show(supportFragmentManager, FilterMainResultDialog.TAG)
    }


    fun onCitiesRadioClicked(view: View) {
        val radio = view as RadioButton
        this.city = radio.text.toString()
    }

    fun onTypesRadioClicked(view: View) {

        val radio = view as RadioButton
        this.type = radio.text.toString()
        Log.d(TAG, "onTypesRadioClicked: $type")
    }

    override fun onDialogPositiveClick(dialogType: Int, distance: Double) {
        binding.progressCircular.visibility = VISIBLE
        if (dialogType == Constants.FILTER_MAIN_RESULT_OPTION)
            queryWithCityAndType(city, type)
        else if (dialogType == Constants.SEARCH_AREA_OPTION)
            searchArea(distance)
    }

    private fun queryWithCityAndType(city: String, type: String) {

        placeAdapter = firebaseQueries.queryWithCityAndType(city, type)
        binding.recyclerView.adapter = placeAdapter
        placeAdapter.startListening()


    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    // *********     ****///***///***///**/     *********//////////

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        placeAdapter.startListening()
        updateLocation.updateLocation()
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        updateLocation.removeLocationUpdates()

    }

    override fun onStop() {
        Log.d(TAG, "onStop: ")
        super.onStop()
        placeAdapter.stopListening()

    }

    override fun onStart() {
        Log.d(TAG, "onStart: ")
        super.onStart()
    }

    fun setProgressBar(placesCount: Int) {
        binding.progressCircular.visibility = GONE
        if (placesCount > 0)
            binding.noResultTextView.visibility = GONE
        else
            binding.noResultTextView.visibility = VISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
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
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
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
                        updateLocation.getLastKnownLocation()
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

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }


}


