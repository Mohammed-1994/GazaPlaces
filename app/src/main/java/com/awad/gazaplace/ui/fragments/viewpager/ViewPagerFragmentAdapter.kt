package com.awad.gazaplace.ui.fragments.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.awad.gazaplace.ui.fragments.city_fragments.*

private const val TAG = "ViewPagerFra, myTag"

class ViewPagerFragmentAdapter constructor(
    var type: String,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle

) : FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> NorthFragment(type)
            2 -> KhanFragment(type)
            3 -> CentralFragment(type)
            4 -> RafahFragment(type)
            else -> GazaFragment(type)
        }
    }
}