package com.awad.gazaplace.data

import android.os.Parcelable
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize


@Parcelize

data class RestaurantModel(
    val service_options: ArrayList<String> = ArrayList(),
    val health_and_safety: ArrayList<String> = ArrayList(),
    val main_features: ArrayList<String> = ArrayList(),
    val accessibility: ArrayList<String> = ArrayList(),
    val eat_options: ArrayList<String> = ArrayList(),
    val services: ArrayList<String> = ArrayList(),
    val payment: ArrayList<String> = ArrayList(),
    val amenities: ArrayList<String> = ArrayList(),
    val public: ArrayList<String> = ArrayList(),
    val atmosphere: ArrayList<String> = ArrayList(),
    val images: ArrayList<String> = ArrayList(),
    val planning: ArrayList<String> = ArrayList(),
    var main_info: @RawValue HashMap<String, Any>? = HashMap(),

    val place_number: Int = 0,
    val view_count: Int = 0

) : Parcelable
