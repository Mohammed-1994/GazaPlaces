package com.awad.gazaplace.adapters

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.MainActivity
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ActivityContext
import java.lang.NullPointerException


private const val TAG = "MetDataAdapter myTag"

class MetDataAdapter(
    options: FirestoreRecyclerOptions<PlaceMetaData>?, @ActivityContext var context: Context
) :
    FirestoreRecyclerAdapter<PlaceMetaData, MetDataAdapter.MetaDataViewHolder>(options!!) {

    private var location = Location("")


    inner class MetaDataViewHolder(val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetaDataViewHolder {
        Log.d(TAG, "onCreateViewHolder: ")
        val view = PlaceItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MetaDataViewHolder(view)

    }


    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()
        Log.d(TAG, "onDataChanged: ")

        if (context is MainActivity)
            (context as MainActivity).setProgressBar()

    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e(TAG, "onError: ", e)
    }



    override fun onBindViewHolder(
        holder: MetDataAdapter.MetaDataViewHolder,
        position: Int,
        model: PlaceMetaData
    ) {


        Log.d(TAG, "onBindViewHolder: ")
        with(holder) {

            binding.root.setOnClickListener{
                Toast.makeText(context, model.name, Toast.LENGTH_SHORT).show()
            }
            try {
                Log.d(TAG, "onBindViewHolder: trying")

                binding.address.text = model.address
                binding.title.text = model.name
                binding.description.text = model.ref_id

                val placeGeoPoint: GeoPoint = model.location
                val placeLocation = Location("")

                placeLocation.latitude = placeGeoPoint.latitude
                placeLocation.longitude = placeGeoPoint.longitude

                var distance = placeLocation.distanceTo(location) / 1000
                if (distance < 1.0)
                    distance = 1F
                binding.distance.text = "${distance.toInt()}  km"

            } catch (e: NullPointerException) {
                Log.e(TAG, "onBindViewHolder: ", e)
            }

        }
    }

    fun findDistance(location: Location) {
        this.location = location
        notifyDataSetChanged()
    }
}