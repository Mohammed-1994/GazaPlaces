package com.awad.gazaplace.data

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


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
    var location: @RawValue GeoPoint = GeoPoint(0.0, 0.0)


) : Parcelable

