package com.awad.gazaplace.ui.fragments.filter_search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.GridImageAdapter
import com.awad.gazaplace.databinding.FragmentFilteSearchBinding
import com.awad.gazaplace.util.Util


private const val TAG = "DashboardFragment myTag"
private const val FILTER_SEARCH = 0

class DashboardFragment : Fragment() {
    private var binding: FragmentFilteSearchBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilteSearchBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGridView()
    }

    private fun setGridView() {
        val gridView = binding?.gridview
        val placeList = Util(requireContext()).getGridPlacesList()
        val adapter = GridImageAdapter(requireContext(), placeList)
        gridView?.adapter = adapter

        gridView?.setOnItemClickListener { _, view, position, _ ->
            requireContext()
            val bundle = bundleOf(
                "type" to placeList[position].name

            )
            view.findNavController()
                .navigate(R.id.startMyFragment, bundle)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}