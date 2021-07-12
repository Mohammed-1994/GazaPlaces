package com.awad.gazaplace.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceMetaDataForMap(
    var city: String = "",
    var type: String = "",
    var geo_hash: String = "",
    var name: String = "",
    var address: String = "",
    var ref_id: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    val images: ArrayList<String> = ArrayList(),

) : Parcelable


