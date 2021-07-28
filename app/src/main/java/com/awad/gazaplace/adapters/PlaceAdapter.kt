package com.awad.gazaplace.adapters

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.PlaceAdapter.PlaceViewHolder
import com.awad.gazaplace.data.RefCityType
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.awad.gazaplace.ui.HomeActivity
import com.awad.gazaplace.ui.MapsActivity
import com.awad.gazaplace.ui.PlaceActivity
import com.awad.gazaplace.ui.fragments.filter_search.FilterSearchViewModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ActivityContext


private const val TAG = "PlaceAdapter myTag"

class PlaceAdapter(
    options: FirestoreRecyclerOptions<RestaurantModel>?, @ActivityContext var context: Context?
) :
    FirestoreRecyclerAdapter<RestaurantModel, PlaceViewHolder>(options!!) {

    private var location = Location("")
    private lateinit var mBinding: PlaceItemBinding
    private var ref = ""
    private var city = ""
    private var type = ""

    private val filterSearchViewModel = ViewModelProvider(context as HomeActivity).get(
        FilterSearchViewModel::class.java
    )


    inner class PlaceViewHolder(val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()

        filterSearchViewModel.setData(this)

    }


    override fun onBindViewHolder(
        holder: PlaceViewHolder,
        position: Int,
        model: RestaurantModel
    ) {
        with(holder) {

            ref = snapshots.getSnapshot(position).id
            type = model.main_info?.get(context?.getString(R.string.firestore_field_type))
                .toString()
            city = model.main_info?.get(context?.getString(R.string.firestore_field_city))
                .toString()
            binding.root.setOnClickListener {
                navigateToPlaceActivity(snapshots.getSnapshot(position).id)
            }
            binding.showMap.setOnClickListener {
                navigateToMapsActivity(model)
            }
            binding.info.setOnClickListener {
                navigateToPlaceActivity(snapshots.getSnapshot(position).id)

            }
            binding.call.setOnClickListener {
                makeCall(model)
            }

            val address =
                model.main_info?.get(context?.getString(R.string.firestore_field_address))
                    .toString()

            val name =
                model.main_info?.get(context?.getString(R.string.firestore_field_name))
                    .toString()
            try {

                mBinding = binding

                binding.address.text = address
                binding.title.text = name


                val placeGeoPoint: GeoPoint =
                    model.main_info?.get(context?.getString(R.string.firestore_field_location)) as GeoPoint
                val placeLocation = Location("")

                placeLocation.latitude = placeGeoPoint.latitude
                placeLocation.longitude = placeGeoPoint.longitude

                var distance = placeLocation.distanceTo(location) / 1000
                if (distance < 1.0)
                    distance = 1F
                binding.distance.text = "${distance.toInt()}  km"

                var imagesList = ArrayList<String>()
                imagesList = model.images

                val sliderView = binding.imageSlider!!
                val sliderAdapter = SliderAdapter(context!!)


                if (imagesList.size == 0) {

                    imagesList.add("https://firebasestorage.googleapis.com/v0/b/add-place-d0852.appspot.com/o/placeholder.png?alt=media&token=149eda64-4708-4eb5-9763-701d5e1c7ef5")
                }
                sliderAdapter.renewItems(imagesList)
                sliderAdapter.setModel(RefCityType(ref, city, type), true)
                sliderView.setSliderAdapter(sliderAdapter)
                sliderView.startAutoCycle()


            } catch (e: NullPointerException) {
                Log.e(TAG, "onBindViewHolder: Error $name")
                Log.e(TAG, "onBindViewHolder: Error", e)
            }

        }
    }


    private fun navigateToPlaceActivity(id: String) {
        val intent = Intent(context, PlaceActivity::class.java)
        intent.putExtra("ref", id)
        intent.putExtra(context?.getString(R.string.firestore_field_type), type)
        intent.putExtra(context?.getString(R.string.firestore_field_city), city)
        context?.startActivity(intent)
    }

    private fun makeCall(model: RestaurantModel?) {
        val phoneNumber: String? = model?.main_info?.get("phone").toString()
        if (phoneNumber != null) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
            ContextCompat.startActivity(context!!, intent, null)
        } else {
            Toast.makeText(context, "لا يوجد هاتف لهذا المكان", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToMapsActivity(currentMetaData: RestaurantModel) {
        val geoPoint = currentMetaData.main_info?.get("location") as GeoPoint
        val intent = Intent(context, MapsActivity::class.java)
        intent.putExtra("lat", geoPoint.latitude)
        intent.putExtra("lng", geoPoint.longitude)
        intent.putExtra("name", currentMetaData.main_info?.get("name").toString())
        ContextCompat.startActivity(context!!, intent, null)
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