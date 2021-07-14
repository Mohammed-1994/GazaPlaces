package com.awad.gazaplace.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.MainAdapter
import com.awad.gazaplace.data.PlaceMetaData
import com.awad.gazaplace.data.PlaceMetaDataForMap
import com.awad.gazaplace.databinding.FragmentAllTypesBinding
import com.awad.gazaplace.firebase.FirebaseQueries
import com.awad.gazaplace.maps.UpdateLocation
import com.awad.gazaplace.ui.HomeActivity
import com.awad.gazaplace.ui.MapsActivity
import com.awad.gazaplace.ui.fragments.area_search.AreaSearchViewModel
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val PLACE_TYPE = "type"
private const val TAG = "AllTypesFragment myTag"

/**
 * A simple [Fragment] subclass.
 * Use the [AllTypesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class AllTypesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var binding: FragmentAllTypesBinding? = null
    private lateinit var mainAdapter: MainAdapter
    private lateinit var areaObserver: Observer<MutableList<PlaceMetaData>>
    private var places = ArrayList<PlaceMetaData>()
    private val areaSearchViewModel: AreaSearchViewModel by activityViewModels()
    private var type: String = ""
    private val list = ArrayList<PlaceMetaDataForMap>()


    @Inject
    lateinit var updateLocation: UpdateLocation

    @Inject
    lateinit var firebaseQueries: FirebaseQueries
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = it.getString(PLACE_TYPE)!!
            if (type == "مطاعم")
                type = getString(R.string.restaurant)
        }
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllTypesBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createMenu()
        areaSearch(5000.0)
        binding!!.mapFabBtn.setOnClickListener {

            val intent = Intent(requireContext(), MapsActivity::class.java)
            list.clear()
            for (place in places) {
                list.add(getPlaceMapFromPlace(place))
            }

            intent.putParcelableArrayListExtra("places", list)
            requireContext().startActivity(intent)
        }
    }

    private fun createMenu() {
        val list = mutableListOf(
            PowerMenuItem("5 KM"),
            PowerMenuItem("10 KM"),
            PowerMenuItem("15 KM"),
        )
        val powerMenu = PowerMenu.Builder(requireContext())
            .addItemList(list) // list has "Novel", "Poerty", "Art"
            .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
            .setMenuRadius(10f) // sets the corner radius.
            .setMenuShadow(10f) // sets the shadow.
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            .setTextGravity(Gravity.CENTER)
            .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
            .setSelectedTextColor(Color.WHITE)
            .setMenuColor(Color.WHITE)
            .setSelectedMenuColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
            .setLifecycleOwner(viewLifecycleOwner)
            .build()


        val onMenuItemClickListener: OnMenuItemClickListener<PowerMenuItem?> =


            OnMenuItemClickListener<PowerMenuItem?> { position, item ->
                var radius = 5000.0
                when (position) {
                    0 -> {
                        radius = 5000.0
                    }
                    1 -> {
                        radius = 10000.0
                    }
                    2 -> {
                        radius = 15000.0
                    }
                }
                areaSearch(radius)

                powerMenu.selectedPosition = position // change selected item
                powerMenu.dismiss()
            }
        powerMenu.onMenuItemClickListener = onMenuItemClickListener
        binding?.menu?.setOnClickListener {
            powerMenu.showAsDropDown(binding?.menu)
        }
    }


    private fun areaSearch(radius: Double) {
        binding?.recyclerView?.visibility = GONE
        binding?.progressBar?.visibility = VISIBLE
        places.clear()

        list.clear()
        binding!!.mapFabBtn.visibility = View.VISIBLE
        firebaseQueries.searchArea(updateLocation.mLocation, type, radius)
        areaObserver = Observer<MutableList<PlaceMetaData>> {
            updateUi(it)
        }

        areaSearchViewModel.placesLiveData.observe(viewLifecycleOwner, areaObserver)
        binding!!.progressBar.visibility = View.VISIBLE
        binding!!.noResultTextView.visibility = View.GONE


    }

    override fun onStop() {
        super.onStop()
        binding = null
        areaSearchViewModel.placesLiveData.removeObserver(areaObserver)


    }


    private fun updateUi(places: MutableList<PlaceMetaData>?) {
        this.places.clear()

        list.clear()
        places!!
        binding?.recyclerView?.visibility = VISIBLE
        this.places.addAll(places)

        binding!!.progressBar.visibility = View.GONE

        if (places.size > 0) {
            binding!!.noResultTextView.visibility = View.GONE
            mainAdapter = MainAdapter(requireContext())
            mainAdapter.submitPlaces(places)
            binding!!.recyclerView.adapter = mainAdapter
            mainAdapter.findDistance((activity as HomeActivity).currentLocation)
        } else {
            binding!!.noResultTextView.visibility = View.VISIBLE
            binding!!.recyclerView.visibility = View.GONE
        }
    }

    private fun getPlaceMapFromPlace(place: PlaceMetaData): PlaceMetaDataForMap {
        val lat = place.lat
        val address = place.address
        val city = place.city
        val geo_hash = place.geo_hash
        val images = place.images
        val lng = place.lng
        val name = place.name
        val ref_id = place.ref_id
        val type = place.type

        return PlaceMetaDataForMap(
            city = city,
            type = type,
            geo_hash = geo_hash,
            name = name,
            address = address,
            ref_id = ref_id,
            lat = lat,
            lng = lng,
            images = images
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllTypesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            AllTypesFragment().apply {
                arguments = Bundle().apply {
                    putString(PLACE_TYPE, param1)

                }
            }
    }

}





