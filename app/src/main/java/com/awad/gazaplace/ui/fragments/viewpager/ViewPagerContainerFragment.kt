package com.awad.gazaplace.ui.fragments.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.awad.gazaplace.R
import com.awad.gazaplace.databinding.FragmentViewPagerContainerBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

private const val TAG = "ViewPagerContai, myTag"

class ViewPagerContainerFragment : Fragment() {

    private var binding: FragmentViewPagerContainerBinding? = null
    private lateinit var adapter: ViewPagerFragmentAdapter
    private var type = "مطعم"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewPagerContainerBinding.inflate(inflater, container, false)
        type = arguments?.getString("type")!!

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (type == "مطاعم")
            type = requireContext().getString(R.string.restaurant)

        adapter = ViewPagerFragmentAdapter(type, requireFragmentManager(), lifecycle)
        binding?.pager?.adapter = adapter


        adapter.notifyDataSetChanged()
        binding!!.tabLayout.addTab(binding!!.tabLayout.newTab().setText("غزة"))
        binding!!.tabLayout.addTab(binding!!.tabLayout.newTab().setText("الشمال"))
        binding!!.tabLayout.addTab(binding!!.tabLayout.newTab().setText("خان يونس"))
        binding!!.tabLayout.addTab(binding!!.tabLayout.newTab().setText("الوسطى"))
        binding!!.tabLayout.addTab(binding!!.tabLayout.newTab().setText("رفح"))


        binding?.tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding!!.pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding!!.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(position))
            }
        }

        )

    }

}