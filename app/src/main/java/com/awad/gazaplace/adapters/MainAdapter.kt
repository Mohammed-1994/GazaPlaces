package com.awad.gazaplace.adapters

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.R
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.awad.gazaplace.ui.MainActivity
import com.awad.gazaplace.ui.PlaceActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ActivityContext


private const val TAG = "MinAdapter, myTag"

class MainAdapter(@ActivityContext var context: Context) :
    RecyclerView.Adapter<MainAdapter.MainAdapterViewHolder>() {


    private var location = Location("")
    var matchingDocs: MutableList<PlaceMetaData> = ArrayList()

    inner class MainAdapterViewHolder(val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapterViewHolder {
        val view = PlaceItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainAdapterViewHolder(view)

    }

    override fun getItemCount(): Int {
        return matchingDocs.size
    }

    override fun onBindViewHolder(holder: MainAdapterViewHolder, position: Int) {

        val currentPlace = matchingDocs[position]
        with(holder) {

            binding.root.setOnClickListener {
                navigateToPlaceActivity(currentPlace)
            }
            try {
                binding.address.text = currentPlace.address
                binding.title.text = currentPlace.name
                binding.description.text = currentPlace.ref_id

                showDistance(currentPlace, this)

                getImages(currentPlace, holder)

            } catch (e: NullPointerException) {
            }
        }


    }

    private fun navigateToPlaceActivity(currentMetaData: PlaceMetaData) {

        val intent = Intent(context, PlaceActivity::class.java)
        intent.putExtra("ref", currentMetaData.ref_id)
        intent.putExtra(context.getString(R.string.firestore_field_type), currentMetaData.type)
        intent.putExtra(context.getString(R.string.firestore_field_city), currentMetaData.city)
        context.startActivity(intent)

    }

    fun submitPlaces(matchingDocs: MutableList<PlaceMetaData>) {
        Log.d(TAG, "submitPlaces: ${matchingDocs.size}")
        this.matchingDocs = matchingDocs
        (context as MainActivity).setProgressBar(itemCount)
        notifyDataSetChanged()

    }


    private fun getImages(model: PlaceMetaData, holder: MainAdapterViewHolder) {
        (context as MainActivity).fireStore.collection(context.getString(R.string.firestore_collection_cities))
            .document(model.city)
            .collection(model.type)
            .document(model.ref_id).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var imagesList = ArrayList<String>()
                    if (it.result[context.getString(R.string.firestore_field_images)] != null)
                        imagesList = (it.result["images"]) as ArrayList<String>

                    showImages(imagesList, holder)

                }

            }


    }


    private fun showImages(
        imagesList: java.util.ArrayList<*>,
        holder: MainAdapterViewHolder
    ) {

        with(holder) {
            if (imagesList.size > 0) {
                Glide.with(context)
                    .load(imagesList[0])
                    .into(binding.imageView)
            } else {
                binding.imageView.setImageDrawable(context.getDrawable(R.drawable.googleg_disabled_color_18))
            }
        }

    }

    fun findDistance(location: Location) {
        this.location = location

        notifyDataSetChanged()
    }

    private fun showDistance(model: PlaceMetaData, holder: MainAdapterViewHolder) {
        with(holder) {
            val placeGeoPoint: GeoPoint = model.location
            val placeLocation = Location("")

            placeLocation.latitude = placeGeoPoint.latitude
            placeLocation.longitude = placeGeoPoint.longitude

            var distance = placeLocation.distanceTo(location) / 1000
            if (distance < 1.0)
                distance = 1F
            binding.distance.text = "${distance.toInt()}  km"
        }
    }
}