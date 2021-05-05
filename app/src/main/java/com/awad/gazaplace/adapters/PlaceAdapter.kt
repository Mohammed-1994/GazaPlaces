package com.awad.gazaplace.adapters

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.MainActivity
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ActivityContext
import java.lang.NullPointerException


private const val TAG = "PlaceAdapter myTag"

class PlaceAdapter(
    options: FirestoreRecyclerOptions<RestaurantModel>?, @ActivityContext var context: Context
) :
    FirestoreRecyclerAdapter<RestaurantModel, PlaceAdapter.PlaceViewHolder>(options!!) {

    private var location = Location("")
    private lateinit var mBinding: PlaceItemBinding


    inner class PlaceViewHolder(val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root)


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

    override fun getItemCount(): Int {

        return super.getItemCount()
    }

    override fun onBindViewHolder(
        holder: PlaceViewHolder,
        position: Int,
        model: RestaurantModel
    ) {


        Log.d(TAG, "onBindViewHolder: ")
        with(holder) {


            try {
                Log.d(TAG, "onBindViewHolder: trying")
                mBinding = binding
                binding.address.text = model.main_info?.get("address").toString()
                binding.title.text = model.main_info?.get("name").toString()
                binding.description.text = model.main_info?.get("description").toString()

                val placeGeoPoint: GeoPoint = model.main_info?.get("location") as GeoPoint
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceViewHolder {
        val view = PlaceItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(view)
    }
}