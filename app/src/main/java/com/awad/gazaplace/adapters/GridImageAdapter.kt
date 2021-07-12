package com.awad.gazaplace.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.awad.gazaplace.R
import com.awad.gazaplace.data.GridItem
import dagger.hilt.android.qualifiers.ApplicationContext

class GridImageAdapter constructor(
    @ApplicationContext var context: Context,
    var itemList: ArrayList<GridItem>
) : BaseAdapter() {


    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): GridItem {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater
                .from(context).inflate(R.layout.grid_list_item, parent, false)
        }

        view?.findViewById<ImageView>(R.id.image)?.setImageDrawable(itemList[position].image)
        view?.findViewById<TextView>(R.id.text)?.text = itemList[position].name

        return view!!

    }
}