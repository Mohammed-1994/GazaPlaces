package com.awad.gazaplace

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
import com.awad.gazaplace.adapters.MetDataAdapter
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.ActivityMainBinding
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
    private lateinit var adapter: MetDataAdapter

    private val util: Util = Util(this)
    private val updateLocation: UpdateLocation = UpdateLocation(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: ")
        util.checkForPermissions()

        //query
        val queryCollectionGroup = fireStore.collectionGroup("meta_data").whereEqualTo("type", "مطعم")

        // options
        val options: FirestoreRecyclerOptions<PlaceMetaData> =
            FirestoreRecyclerOptions.Builder<PlaceMetaData>()
                .setQuery(queryCollectionGroup, PlaceMetaData::class.java)
                .build()


        adapter = MetDataAdapter(options, this)

        binding.recyclerView.adapter = adapter
        Log.d(TAG, "onCreate: ${adapter.itemCount}")


    }

    override fun onResume() {
        Log.d(TAG, "onResume: ")
        super.onResume()
        updateLocation.updateLocation()
    }


    override fun onPause() {
        Log.d(TAG, "onPause: ")
        super.onPause()
        updateLocation.removeLocationUpdates()

    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()


    }

    override fun onLocationUpdated(location: Location) {
        Log.d(TAG, "onLocationUpdated: lat= ${location.latitude}, Lng= ${location.longitude}")
        adapter.findDistance(location)
    }

    fun setProgressBar() {
        binding.progressCircular.visibility = GONE
    }


}
