package com.awad.gazaplace.adapters

import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.R
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.data.RefCityType
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.awad.gazaplace.ui.PlaceActivity
import com.google.firebase.firestore.FirebaseFirestore
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

        this.matchingDocs = matchingDocs

        notifyDataSetChanged()

    }


    private fun getImages(model: PlaceMetaData, holder: MainAdapterViewHolder) {
        val fireStore = FirebaseFirestore.getInstance()
        fireStore.collection(context.getString(R.string.firestore_collection_cities))
            .document(model.city)
            .collection(model.type)
            .document(model.ref_id).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    var imagesList = ArrayList<String>()
                    if (it.result[context.getString(R.string.firestore_field_images)] != null)
                        imagesList = (it.result["images"]) as ArrayList<String>

                    showImages(imagesList, holder, model)

                }

            }


    }


    private fun showImages(
        imagesList: java.util.ArrayList<String>,
        holder: MainAdapterViewHolder,
        model: PlaceMetaData
    ) {

        with(holder) {

            val sliderView = binding.imageSlider!!

            val sliderAdapter = SliderAdapter(context)
            if (imagesList.size == 0) {
                imagesList.add("https://firebasestorage.googleapis.com/v0/b/add-place-d0852.appspot.com/o/placeholder.png?alt=media&token=149eda64-4708-4eb5-9763-701d5e1c7ef5")
            }
            sliderAdapter.renewItems(imagesList)
            sliderAdapter.setModel(RefCityType(model.ref_id, model.city, model.type), true)
            sliderView.setSliderAdapter(sliderAdapter)
            sliderView.startAutoCycle();


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