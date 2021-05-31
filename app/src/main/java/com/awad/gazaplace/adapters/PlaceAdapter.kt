package com.awad.gazaplace.adapters

import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.PlaceAdapter.PlaceViewHolder
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.awad.gazaplace.ui.MainActivity
import com.awad.gazaplace.ui.PlaceActivity
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ActivityContext


private const val TAG = "PlaceAdapter myTag"

class PlaceAdapter(
    options: FirestoreRecyclerOptions<RestaurantModel>?, @ActivityContext var context: Context
) :
    FirestoreRecyclerAdapter<RestaurantModel, PlaceViewHolder>(options!!) {

    private var location = Location("")
    private lateinit var mBinding: PlaceItemBinding


    inner class PlaceViewHolder(val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()

        if (context is MainActivity)
            (context as MainActivity).setProgressBar(itemCount)

    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e(TAG, "onError: ", e)
    }

    override fun onBindViewHolder(
        holder: PlaceViewHolder,
        position: Int,
        model: RestaurantModel
    ) {


        with(holder) {


            binding.root.setOnClickListener {
                val intent = Intent(context, PlaceActivity::class.java)
                intent.putExtra("ref", snapshots.getSnapshot(position).id)
                intent.putExtra(
                    context.getString(R.string.firestore_field_type),
                    model.main_info?.get(context.getString(R.string.firestore_field_type))
                        .toString()
                )
                intent.putExtra(
                    context.getString(R.string.firestore_field_city),
                    model.main_info?.get(context.getString(R.string.firestore_field_city))
                        .toString()
                )

                context.startActivity(intent)
            }

            try {

                mBinding = binding
                binding.address.text =
                    model.main_info?.get(context.getString(R.string.firestore_field_address))
                        .toString()
                binding.title.text =
                    model.main_info?.get(context.getString(R.string.firestore_field_name))
                        .toString()
                binding.description.text =
                    model.main_info?.get(context.getString(R.string.firestore_field_description))
                        .toString()

                val placeGeoPoint: GeoPoint =
                    model.main_info?.get(context.getString(R.string.firestore_field_location)) as GeoPoint
                val placeLocation = Location("")

                placeLocation.latitude = placeGeoPoint.latitude
                placeLocation.longitude = placeGeoPoint.longitude

                var distance = placeLocation.distanceTo(location) / 1000
                if (distance < 1.0)
                    distance = 1F
                binding.distance.text = "${distance.toInt()}  km"

                val imagesList = model.images

                if (imagesList.size > 0) {
                    Glide.with(context)
                        .load(imagesList[0])
                        .into(binding.imageView)
                } else {

                }


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