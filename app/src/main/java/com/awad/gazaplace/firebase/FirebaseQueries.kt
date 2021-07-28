package com.awad.gazaplace.firebase

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.PlaceAdapter
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.ui.HomeActivity
import com.awad.gazaplace.ui.fragments.area_search.AreaSearchViewModel
import com.awad.gazaplace.ui.fragments.home.HomeViewModel
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryBounds
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

private const val TAG = "FirebaseQueries, myTag"

class FirebaseQueries  @Inject constructor(@ActivityContext var context: Context) {
//    private var locationUpdatesCallback: MyLocationUpdatesCallback =
//        context as MyLocationUpdatesCallback
    private var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val homeViewModel =
        ViewModelProvider(context as HomeActivity).get(HomeViewModel::class.java)
    private val areaSearchViewModel =
        ViewModelProvider(context as HomeActivity).get(AreaSearchViewModel::class.java)

    /**
     * get the nearest places with specific radius.
     * @param center the location to search around
     * @param radius radius of search area
     *
     * @return list of places that exists in search area.
     */

    fun nearestLocations(center: Location, radius: Double) {
        val matchingDocs: MutableList<PlaceMetaData> = ArrayList()
        val center = GeoLocation(center.latitude, center.longitude)

        val bounds: List<GeoQueryBounds> = GeoFireUtils.getGeoHashQueryBounds(
            center,
            radius
        )
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {

            val q: Query =
                fireStore.collectionGroup(context.getString(R.string.collection_group_meta_data))
                    .orderBy(context.getString(R.string.firestore_field_geo_hash))
                    .startAt(b.startHash)
                    .endAt(b.endHash)


            tasks.add(q.get())
        }
        // Collect all the query results together into a single list

        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {

                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap.documents) {
                        val location = doc.toObject(PlaceMetaData::class.java)
                        val lat = doc.get("lat")!! as Double
                        val lng = doc.get("lng")!! as Double

                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radius) {
                            matchingDocs.add(location!!)
                        }
                    }

                }

//                (context as MainActivity).submitNearestPlacesToAdapter(matchingDocs)

                homeViewModel.setData(matchingDocs)

            }

    }

    fun queryWithCityAndType(city: String, type: String): PlaceAdapter {
        val q = fireStore.collection(context.getString(R.string.firestore_collection_cities))
            .document(city)
            .collection(type)


        val options: FirestoreRecyclerOptions<RestaurantModel> =
            FirestoreRecyclerOptions.Builder<RestaurantModel>()
                .setQuery(q, RestaurantModel::class.java)
                .build()


        return PlaceAdapter(options, context)


    }


    fun searchArea(location: Location, type: String, radius: Double) {

        val matchingDocs: MutableList<PlaceMetaData> = ArrayList()
        val center = GeoLocation(location.latitude, location.longitude)


        val bounds: List<GeoQueryBounds> = GeoFireUtils.getGeoHashQueryBounds(
            center,
            radius
        )

        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {

            val q: Query =
                fireStore.collectionGroup(context.getString(R.string.collection_group_meta_data))
                    .whereEqualTo(context.getString(R.string.firestore_field_type), type)
                    .orderBy(context.getString(R.string.firestore_field_geo_hash))
                    .startAt(b.startHash)
                    .endAt(b.endHash)


            tasks.add(q.get())
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    Log.e(TAG, "searchArea: Error", it.exception)

                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap.documents) {
                        val location = doc.toObject(PlaceMetaData::class.java)
                        val lat = doc.get("lat")!! as Double
                        val lng = doc.get("lng")!! as Double

                        val docLocation = GeoLocation(lat, lng)
                        val distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center)
                        if (distanceInM <= radius) {
                            matchingDocs.add(location!!)
                        }
                    }

                }
                
                areaSearchViewModel.setData(matchingDocs)


            }
    }
}