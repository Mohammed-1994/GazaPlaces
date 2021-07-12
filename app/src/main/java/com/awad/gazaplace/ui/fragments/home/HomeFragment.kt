package com.awad.gazaplace.ui.fragments.home

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.awad.gazaplace.adapters.MainAdapter
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.databinding.FragmentHomeBinding
import com.awad.gazaplace.maps.UpdateLocation
import com.awad.gazaplace.ui.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "HomeFragment, myTag"

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var currentLocation: Location
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentHomeBinding
    private var age = 0


    @Inject
    lateinit var updateLocation: UpdateLocation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        homeViewModel.placesLiveData.observe(viewLifecycleOwner, {
            updateUi(it)
        }

        )

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateLocation.getSettingsResult()
        updateLocation.getLastKnownLocation()
    }

    private fun updateUi(places: MutableList<PlaceMetaData>?) {
        binding.progressBar.visibility = GONE

        if (places?.size!! > 0) {
            binding.noResultTextView.visibility = GONE
            val adapter = MainAdapter(requireContext())
            adapter.submitPlaces(places)
            binding.recyclerView.adapter = adapter
            adapter.findDistance((activity as HomeActivity).currentLocation)
        } else {
            binding.noResultTextView.visibility = VISIBLE
            binding.recyclerView.visibility = GONE
        }

    }



}