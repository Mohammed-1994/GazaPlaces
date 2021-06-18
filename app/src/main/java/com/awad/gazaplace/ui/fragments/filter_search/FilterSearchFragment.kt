package com.awad.gazaplace.ui.fragments.filter_search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.awad.gazaplace.adapters.PlaceAdapter
import com.awad.gazaplace.databinding.FragmentFilteSearchBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.ui.HomeActivity


private const val TAG = "DashboardFragment myTag"

class DashboardFragment : Fragment() {


    private val filterSearchViewModel: FilterSearchViewModel by activityViewModels()
    private var binding: FragmentFilteSearchBinding? = null
    private var city = "null"
    private var type = "null"
    private lateinit var firebaseQueries: FirebaseQueries
    private lateinit var adapter: PlaceAdapter
    private lateinit var observer: Observer<PlaceAdapter>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilteSearchBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseQueries = FirebaseQueries(requireContext())
        adapter = firebaseQueries.queryWithCityAndType(city, type)

        binding!!.searchButton.setOnClickListener {
            search()
        }

        observer = Observer<PlaceAdapter> {
            Log.d(TAG, "onViewCreated: observing")
            updateUi(it)
        }

        filterSearchViewModel.count.observe(viewLifecycleOwner, observer)
        Log.d(TAG, "onViewCreated: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun updateUi(adapter: PlaceAdapter) {
        this.adapter = adapter
        Log.d(TAG, "updateUi: ")
        binding!!.progressBar.visibility = GONE
        binding!!.searchHintTextView.visibility = GONE
        Log.d(TAG, "updateUi: ${adapter.itemCount}")
        if (adapter.itemCount > 0) {
            binding!!.noResultTextView.visibility = GONE
            adapter.findDistance((activity as HomeActivity).currentLocation)
            binding!!.recyclerView.adapter = adapter
            adapter.startListening()
        } else {
            binding!!.noResultTextView.visibility = VISIBLE
        }
    }

    private fun search() {
        Log.d(TAG, "search: ")
        binding!!.searchHintTextView.visibility = GONE
        binding!!.progressBar.visibility = VISIBLE
        binding!!.noResultTextView.visibility = GONE
        adapter = (activity as HomeActivity).searchFilter()
        binding!!.recyclerView.adapter = adapter
        adapter.startListening()

    }


}