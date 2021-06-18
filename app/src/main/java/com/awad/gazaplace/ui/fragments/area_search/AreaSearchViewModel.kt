package com.awad.gazaplace.ui.fragments.area_search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awad.gazaplace.data.PlaceMetaData

class AreaSearchViewModel : ViewModel() {
    private val mPlaces: MutableLiveData<MutableList<PlaceMetaData>> by lazy {
        MutableLiveData<MutableList<PlaceMetaData>>()
    }

    val placesLiveData: LiveData<MutableList<PlaceMetaData>> get() = mPlaces


    fun setData(places: MutableList<PlaceMetaData>) {
        mPlaces.value = places
    }

}