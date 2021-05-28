package com.awad.gazaplace

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.awad.gazaplace.adapters.MainAdapter
import com.awad.gazaplace.adapters.MetDataAdapter
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
    private lateinit var metDataAdapter: MetDataAdapter
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

        setContentView(binding.root)

        util.checkForPermissions()

        firebaseQueries = FirebaseQueries(this, fireStore)
        placeAdapter = firebaseQueries.queryWithCityAndType(city, type)
        mainAdapter = MainAdapter(this)

        binding.recyclerView.adapter = mainAdapter
        updateLocation.updateLocation()


    }


    override fun onLocationUpdated(location: Location) {
        if (!gotLocation) {
            this.currentLocation = location

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
        updateLocation.updateLocation()
    }


    override fun onPause() {
        super.onPause()
        updateLocation.removeLocationUpdates()

    }

    override fun onStop() {
        super.onStop()

        placeAdapter.stopListening()

    }

    fun setProgressBar() {
        binding.progressCircular.visibility = GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

}


