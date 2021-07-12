package com.awad.gazaplace.ui.fragments.area_search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awad.gazaplace.data.PlaceMetaData

private const val TAG = "AreaSearchViewModel"
class AreaSearchViewModel : ViewModel() {
    private val mPlaces: MutableLiveData<MutableList<PlaceMetaData>> by lazy {
        MutableLiveData<MutableList<PlaceMetaData>>()
    }

    val placesLiveData: LiveData<MutableList<PlaceMetaData>> get() = mPlaces


    fun setData(places: MutableList<PlaceMetaData>) {
        Log.d(TAG, "setData: ${places.size}")
        mPlaces.value = places
    }

}