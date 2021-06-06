package com.awad.gazaplace.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.awad.gazaplace.adapters.SliderAdapter.SliderViewHolder
import com.awad.gazaplace.databinding.SliderImageItemBinding
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private val context: Context) : SliderViewAdapter<SliderViewHolder>() {
    private lateinit var mSliderItems: ArrayList<String>
    public fun renewItems(sliderItems: ArrayList<String>) {
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
                .fitCenter()
                .into(binding.ivAutoImageSlider)
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }


    inner class SliderViewHolder(val binding: SliderImageItemBinding) :
        SliderViewAdapter.ViewHolder(binding.root)
}