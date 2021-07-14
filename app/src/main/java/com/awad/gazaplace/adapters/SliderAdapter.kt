package com.awad.gazaplace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.SliderAdapter.SliderViewHolder
import com.awad.gazaplace.data.RefCityType
import com.awad.gazaplace.databinding.SliderImageItemBinding
import com.awad.gazaplace.ui.PlaceActivity
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter

private const val TAG = "SliderAdapter myTag"

class SliderAdapter(private val context: Context) : SliderViewAdapter<SliderViewHolder>() {
    private lateinit var mSliderItems: ArrayList<String>
    private lateinit var model: RefCityType
    private var navigable = false
    fun renewItems(sliderItems: ArrayList<String>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: String) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val view = SliderImageItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        val sliderItem = mSliderItems[position]

        with(viewHolder) {


            Glide.with(context)
                .load(sliderItem)
                .placeholder(R.drawable.placeholder)
                .fitCenter()
                .into(binding.ivAutoImageSlider)


            setClickedEvent(this)
        }

    }

    private fun setClickedEvent(holder: SliderViewHolder) {
        if (navigable) {
            holder.binding.root.setOnClickListener {
                navigateToPlaceActivity(model)
            }
        }
    }

    private fun navigateToPlaceActivity(currentMetaData: RefCityType) {

        val intent = Intent(context, PlaceActivity::class.java)
        intent.putExtra("ref", currentMetaData.ref)
        intent.putExtra(context.getString(R.string.firestore_field_type), currentMetaData.type)
        intent.putExtra(context.getString(R.string.firestore_field_city), currentMetaData.city)
        context.startActivity(intent)

    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }


    fun setModel(model: RefCityType, navigable: Boolean) {
        this.navigable = navigable
        this.model = model
    }


    inner class SliderViewHolder(val binding: SliderImageItemBinding) :
        SliderViewAdapter.ViewHolder(binding.root)
}