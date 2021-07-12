package com.awad.gazaplace.ui.fragments.city_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.PlaceAdapter
import com.awad.gazaplace.databinding.FragmentRafahBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.ui.HomeActivity
import com.awad.gazaplace.ui.fragments.filter_search.FilterSearchViewModel

private const val TAG = "RafahFragment myTag"
private const val FILTER_SEARCH = 0
private const val AREA_SEARCH = 1

class RafahFragment(val type: String) : Fragment() {


    private val filterSearchViewModel: FilterSearchViewModel by activityViewModels()
    private lateinit var firebaseQueries: FirebaseQueries
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var filterObserver: Observer<PlaceAdapter>
    private var binding: FragmentRafahBinding? = null
    private var city = "null"

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        city = requireContext().getString(R.string.rafah)
        binding = FragmentRafahBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseQueries = FirebaseQueries(requireContext())

        filterSearch()


    }


    private fun updateUi(adapter: PlaceAdapter) {
        this.placeAdapter = adapter

        binding!!.progressBar.visibility = View.GONE

        if (adapter.itemCount > 0) {
            binding!!.noResultTextView.visibility = View.GONE
            adapter.findDistance((activity as HomeActivity).currentLocation)
            adapter.startListening()
        } else {
            binding!!.noResultTextView.visibility = View.VISIBLE
        }
    }

    private fun filterSearch() {

        placeAdapter = firebaseQueries.queryWithCityAndType(city, type)
        filterObserver = Observer<PlaceAdapter> {
            updateUi(it)
        }
        filterSearchViewModel.count.observe(viewLifecycleOwner, filterObserver)
        binding!!.progressBar.visibility = View.VISIBLE
        binding!!.noResultTextView.visibility = View.GONE
        binding!!.recyclerView.adapter = placeAdapter
        placeAdapter.startListening()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}