package com.awad.gazaplace.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.awad.gazaplace.R
import com.awad.gazaplace.data.GridItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Util @Inject constructor(@ApplicationContext val context: Context) {

    fun getGridPlacesList(): ArrayList<GridItem> {
        val places = ArrayList<GridItem>()
        places.add(GridItem(ContextCompat.getDrawable(context,R.drawable.restaurant), "مطاعم"))
        places.add(GridItem(ContextCompat.getDrawable(context,R.drawable.coffe), "كفي شوب"))
        places.add(GridItem(ContextCompat.getDrawable(context,R.drawable.sweet), "حلويات"))
        places.add(GridItem(ContextCompat.getDrawable(context,R.drawable.phone), "هواتف"))
        places.add(GridItem(ContextCompat.getDrawable(context,R.drawable.mall), "مولات"))
        places.add(GridItem(ContextCompat.getDrawable(context,R.drawable.ice), "مرطبات"))
        places.add(GridItem(ContextCompat.getDrawable(context,R.drawable.shop), "ملابس"))
        return places
    }

}