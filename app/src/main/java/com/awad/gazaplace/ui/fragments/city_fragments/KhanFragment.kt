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
import com.awad.gazaplace.databinding.FragmentKhanBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.ui.HomeActivity
import com.awad.gazaplace.ui.fragments.filter_search.FilterSearchViewModel

class KhanFragment(val type: String) : Fragment() {


    private val filterSearchViewModel: FilterSearchViewModel by activityViewModels()
    private var city = "null"
    private lateinit var firebaseQueries: FirebaseQueries
    private lateinit var adapter: PlaceAdapter
    private lateinit var observer: Observer<PlaceAdapter>
    private var binding: FragmentKhanBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        city = requireContext().getString(R.string.khan_yunis)

        binding = FragmentKhanBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseQueries = FirebaseQueries(requireContext())
        adapter = firebaseQueries.queryWithCityAndType(city, type)

        search()


        observer = Observer<PlaceAdapter> {
            updateUi(it)
        }


        filterSearchViewModel.count.observe(viewLifecycleOwner, observer)

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun updateUi(adapter: PlaceAdapter) {
        this.adapter = adapter

        binding!!.progressBar.visibility = View.GONE

        if (adapter.itemCount > 0) {
            binding!!.noResultTextView.visibility = View.GONE
            adapter.findDistance((activity as HomeActivity).currentLocation)

            adapter.startListening()
        } else {
            binding!!.noResultTextView.visibility = View.VISIBLE
        }
    }

    private fun search() {

        binding!!.progressBar.visibility = View.VISIBLE
        binding!!.noResultTextView.visibility = View.GONE
        binding!!.recyclerView.adapter = adapter
        adapter.startListening()

    }
}