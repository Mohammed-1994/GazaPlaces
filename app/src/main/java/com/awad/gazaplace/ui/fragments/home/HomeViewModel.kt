package com.awad.gazaplace.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awad.gazaplace.data.PlaceMetaData

private const val TAG = "HomeViewModel, myTag"

class HomeViewModel : ViewModel() {

    private val mPlaces: MutableLiveData<MutableList<PlaceMetaData>> = MutableLiveData()


    val placesLiveData: LiveData<MutableList<PlaceMetaData>>
        get() = mPlaces


    fun setData(s: MutableList<PlaceMetaData>) {
        mPlaces.value = s
    }
}