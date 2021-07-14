package com.awad.gazaplace.adapters

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.PlaceAdapter.PlaceViewHolder
import com.awad.gazaplace.data.RefCityType
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.awad.gazaplace.ui.HomeActivity
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

            val ref = snapshots.getSnapshot(position).id
            val type = model.main_info?.get(context?.getString(R.string.firestore_field_type))
                .toString()
            val city = model.main_info?.get(context?.getString(R.string.firestore_field_city))
                .toString()
            binding.root.setOnClickListener {
                val intent = Intent(context, PlaceActivity::class.java)
                intent.putExtra("ref", snapshots.getSnapshot(position).id)
                intent.putExtra(context?.getString(R.string.firestore_field_type), type)
                intent.putExtra(context?.getString(R.string.firestore_field_city), city)

                context?.startActivity(intent)
            }

            try {

                mBinding = binding
                binding.address.text =
                    model.main_info?.get(context?.getString(R.string.firestore_field_address))
                        .toString()
                binding.title.text =
                    model.main_info?.get(context?.getString(R.string.firestore_field_name))
                        .toString()


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
                    Log.d(TAG, "onBindViewHolder: empty")
                    imagesList.add("https://firebasestorage.googleapis.com/v0/b/add-place-d0852.appspot.com/o/placeholder.png?alt=media&token=149eda64-4708-4eb5-9763-701d5e1c7ef5")
                }
                sliderAdapter.renewItems(imagesList)
                sliderAdapter.setModel(RefCityType(ref, city, type), true)
                sliderView.setSliderAdapter(sliderAdapter)
                sliderView.startAutoCycle()


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