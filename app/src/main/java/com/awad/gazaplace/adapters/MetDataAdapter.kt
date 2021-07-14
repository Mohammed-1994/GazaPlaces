package com.awad.gazaplace.adapters

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.awad.gazaplace.R
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.databinding.PlaceItemBinding
import com.awad.gazaplace.ui.HomeActivity
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "MetDataAdapter myTag"

class MetDataAdapter(
    options: FirestoreRecyclerOptions<PlaceMetaData>?, @ActivityContext var context: Context
) :
    FirestoreRecyclerAdapter<PlaceMetaData, MetDataAdapter.MetaDataViewHolder>(options!!) {

    @Inject
    @Singleton
    lateinit var fireStore: FirebaseFirestore

    private var location = Location("")


    inner class MetaDataViewHolder(val binding: PlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetaDataViewHolder {

        val view = PlaceItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MetaDataViewHolder(view)

    }


    override fun onDataChanged() {
        super.onDataChanged()
        notifyDataSetChanged()


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

        with(holder) {

            binding.root.setOnClickListener {
                Toast.makeText(context, model.name, Toast.LENGTH_SHORT).show()
            }
            try {

                binding.address.text = model.address
                binding.title.text = model.name


                showDistance(model, this)

                getImages(model, holder)

            } catch (e: NullPointerException) {
                Log.e(TAG, "onBindViewHolder: ", e)
            }

        }
    }

    private fun getImages(model: PlaceMetaData, holder: MetDataAdapter.MetaDataViewHolder) {
        (context as HomeActivity).fireStore.collection(context.getString(R.string.firestore_collection_cities))
            .document(model.city)
            .collection(model.type)
            .document(model.ref_id).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
//                    Log.d(TAG, "getImages: ${model.ref_id}")


                    var imagesList = ArrayList<String>()
                    if (it.result[context.getString(R.string.firestore_field_images)] != null)
                        imagesList =
                            (it.result[context.getString(R.string.firestore_field_images)]) as ArrayList<String>

                    showImages(imagesList, holder)

                } else
                    Log.e(TAG, "showImage: Error", it.exception)

            }


    }

    private fun showImages(
        imagesList: java.util.ArrayList<String>,
        holder: MetaDataViewHolder
    ) {

        with(holder) {
            val sliderView = binding.imageSlider!!
            val sliderAdapter = SliderAdapter(context)
            if (imagesList.size > 0) {
                sliderAdapter.renewItems(imagesList)
                sliderView.setSliderAdapter(sliderAdapter)
                sliderView.startAutoCycle();

            } else {
                sliderView.background = AppCompatResources.getDrawable(context,R.drawable.placeholder)



            }
        }

    }

    fun findDistance(location: Location) {
        this.location = location

        notifyDataSetChanged()
    }

    private fun showDistance(model: PlaceMetaData, holder: MetDataAdapter.MetaDataViewHolder) {
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