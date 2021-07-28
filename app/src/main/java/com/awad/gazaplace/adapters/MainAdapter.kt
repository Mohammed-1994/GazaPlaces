package com.awad.gazaplace.adapters

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.R
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.data.RefCityType
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.awad.gazaplace.ui.MapsActivity
import com.awad.gazaplace.ui.PlaceActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject


private const val TAG = "MinAdapter, myTag"

class MainAdapter @Inject constructor(
    @ActivityContext var context: Context,


    var fireStore: FirebaseFirestore
) :
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
            binding.showMap.setOnClickListener {
                navigateToMapsActivity(currentPlace)
            }
            binding.info.setOnClickListener {
                navigateToPlaceActivity(currentPlace)
            }
            binding.call.setOnClickListener {
                getPhoneNumber(currentPlace)
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

    private fun makeCall(phoneNumber: String?) {
        if (phoneNumber != null) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
            startActivity(context, intent, null)
        } else {
            Toast.makeText(context, "لا يوجد هاتف لهذا المكان", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPhoneNumber(currentMetaData: PlaceMetaData) {

        var phoneNO: String?
        fireStore.collection(context.getString(R.string.firestore_collection_cities))
            .document(currentMetaData.city)
            .collection(currentMetaData.type)
            .document(currentMetaData.ref_id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val place = it.result.toObject(RestaurantModel::class.java)!!
                    phoneNO = place.main_info!!["phone"].toString()
                    makeCall(phoneNO)
                }
            }

    }

    private fun navigateToMapsActivity(currentMetaData: PlaceMetaData) {
        val location = currentMetaData.location
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra("lat", location.latitude)
        intent.putExtra("lng", location.longitude)
        intent.putExtra("name", currentMetaData.name)
        startActivity(context, intent, null)
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

            val sliderView = binding.imageSlider

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