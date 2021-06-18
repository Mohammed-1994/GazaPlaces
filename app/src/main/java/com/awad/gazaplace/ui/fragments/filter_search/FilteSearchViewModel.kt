package com.awad.gazaplace.ui.fragments.filter_search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awad.gazaplace.adapters.PlaceAdapter

private const val TAG = "DashboardModel myTag"
class FilterSearchViewModel : ViewModel() {


    fun setData(itemCount: PlaceAdapter) {
        Log.d(TAG, "setData: ")
        this.itemCount.value = itemCount
    }

    private val itemCount: MutableLiveData<PlaceAdapter> = MutableLiveData()
    val count: LiveData<PlaceAdapter>
        get() = itemCount


}


