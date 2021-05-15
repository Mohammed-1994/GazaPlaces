package com.awad.gazaplace.data

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class PlaceMetaData(
    var city: String = "",
    var type: String = "",
    var geo_hash: String = "",
    var name: String = "",
    var address: String = "",
    var ref_id: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    val images: ArrayList<String> = ArrayList(),
    var location: @RawValue GeoPoint = GeoPoint(0.0, 0.0)


) : Parcelable

