package com.awad.gazaplace.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.awad.gazaplace.R
import com.awad.gazaplace.data.RestaurantModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PlaceActivity : AppCompatActivity() {

    @Inject
    lateinit var fireStore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        val ref = intent.getStringExtra("ref")!!
        val city = intent.getStringExtra(getString(R.string.firestore_field_city))!!
        val type = intent.getStringExtra(getString(R.string.firestore_field_type))!!

        fireStore.collection(getString(R.string.firestore_collection_cities))
            .document(city)
            .collection(type)
            .document(ref)
            .get()
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    Log.e(TAG, "getModel: Error", it.exception)
                else {
                    val currentPlace = it.result.toObject(RestaurantModel::class.java)!!
                    Toast.makeText(
                        this,
                        currentPlace.main_info?.get("name").toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
    }

    companion object {
        private const val TAG = "PlaceActivity, myTag"
    }
}