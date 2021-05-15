package com.awad.gazaplace

import android.location.Location
import android.os.Bundle
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import com.awad.gazaplace.adapters.MainAdapter
import com.awad.gazaplace.adapters.MetDataAdapter
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.databinding.ActivityMainBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.maps.MyLocationUpdatesCallback
import com.awad.gazaplace.maps.UpdateLocation
import com.awad.gazaplace.util.Util
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "MainActivity myTag"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MyLocationUpdatesCallback {

    @Inject
    lateinit var fireStore: FirebaseFirestore

    @Inject
    lateinit var storageReference: StorageReference


    private lateinit var binding: ActivityMainBinding
    private lateinit var metaDataAdapter: MetDataAdapter
    private lateinit var mainAdapter: MainAdapter
    private lateinit var query: FirebaseQueries
    private var gotLocation = false
    private var radius = 10000.0

    private val util: Util = Util(this)
    private val updateLocation: UpdateLocation = UpdateLocation(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        util.checkForPermissions()

        query = FirebaseQueries(this, fireStore)
        //query
        val queryCollectionGroup =
            fireStore.collectionGroup(getString(R.string.collection_group_meta_data))
                .whereEqualTo(
                    getString(R.string.firestore_field_type),
                    getString(R.string.firetore_field_restaurant)
                )


        // options
        val options: FirestoreRecyclerOptions<PlaceMetaData> =
            FirestoreRecyclerOptions.Builder<PlaceMetaData>()
                .setQuery(queryCollectionGroup, PlaceMetaData::class.java)

                .build()


        metaDataAdapter = MetDataAdapter(options, this)
        mainAdapter = MainAdapter(this)
        binding.recyclerView.adapter = mainAdapter


    }


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
        metaDataAdapter.stopListening()
    }

    override fun onStart() {
        super.onStart()
        metaDataAdapter.startListening()


    }

    override fun onLocationUpdated(location: Location) {
        if (!gotLocation) {
            query.nearestLocations(location, radius)
            mainAdapter.findDistance(location)
        }
        gotLocation = true

    }

    fun setProgressBar() {
        binding.progressCircular.visibility = GONE
    }

    fun submitNearestPlacesToAdapter(nearestPlaces: MutableList<PlaceMetaData>) =
        mainAdapter.submitPlaces(nearestPlaces)


}
