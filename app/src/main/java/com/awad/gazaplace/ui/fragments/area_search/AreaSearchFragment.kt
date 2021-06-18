package com.awad.gazaplace.ui.fragments.area_search

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.awad.gazaplace.adapters.MainAdapter
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.databinding.FragmentAreaSearchBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.ui.HomeActivity


private const val TAG = "AreaFragment, myTag"

class AreaSearchFragment : Fragment() {

    private var type = "null"
    private lateinit var firebaseQueries: FirebaseQueries

    private val areaSearchViewModel by activityViewModels<AreaSearchViewModel>()
    private var binding: FragmentAreaSearchBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentAreaSearchBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseQueries = FirebaseQueries(requireContext())
        binding!!.searchButton.setOnClickListener {
            search()
        }

        areaSearchViewModel.placesLiveData.observe(viewLifecycleOwner, {
            updateUi(it)
        })

        binding!!.radiusEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(binding!!.radiusEditText.text.toString())) {
                    return@setOnEditorActionListener false
                } else {
                    search()
                    return@setOnEditorActionListener true
                }
            }
            return@setOnEditorActionListener true

        }


    }

    private fun updateUi(places: MutableList<PlaceMetaData>?) {
        binding!!.progressBar.visibility = GONE

        if (places?.size!! > 0) {
            binding!!.noResultTextView.visibility = GONE
            val adapter = MainAdapter(requireContext())
            adapter.submitPlaces(places)
            binding!!.recyclerView.adapter = adapter
            adapter.findDistance((activity as HomeActivity).currentLocation)
        } else {
            binding!!.noResultTextView.visibility = VISIBLE
            binding!!.recyclerView.visibility = GONE
        }
    }

    private fun search() {

        binding!!.progressBar.visibility = VISIBLE
        binding!!.noResultTextView.visibility = GONE


        val radius = binding!!.radiusEditText.text.toString().toDouble()
        try {
            (activity as HomeActivity).searchArea(radius * 1000)
        } catch (e: NumberFormatException) {
            (activity as HomeActivity).searchArea(5000.0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}