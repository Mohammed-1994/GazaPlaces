package com.awad.gazaplace.firebase

import android.content.Context
import android.location.Location
import com.awad.gazaplace.MainActivity
import com.awad.gazaplace.R
import com.awad.gazaplace.data.PlaceMetaData
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryBounds
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.qualifiers.ActivityContext

private const val TAG = "FirebaseQueries, myTag"
class FirebaseQueries (@ActivityContext var context: Context, var fireStore: FirebaseFirestore){

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

            val q: Query = fireStore.collectionGroup(context.getString(R.string.collection_group_meta_data))
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

                (context as MainActivity).submitNearestPlacesToAdapter(matchingDocs)
            }

    }

}