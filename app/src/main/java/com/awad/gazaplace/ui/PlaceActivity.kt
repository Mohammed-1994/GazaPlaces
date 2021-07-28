package com.awad.gazaplace.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.awad.gazaplace.R
import com.awad.gazaplace.adapters.SliderAdapter
import com.awad.gazaplace.data.RestaurantModel
import com.awad.gazaplace.databinding.ActivityPlaceBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PlaceActivity : AppCompatActivity() {

    @Inject
    lateinit var fireStore: FirebaseFirestore

    lateinit var binding: ActivityPlaceBinding

    private var currentPlace = RestaurantModel()
    private var view_count = 0
    private var ref = ""
    private var city = ""
    private var type = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ref = intent.getStringExtra("ref")!!
        city = intent.getStringExtra(getString(R.string.firestore_field_city))!!
        type = intent.getStringExtra(getString(R.string.firestore_field_type))!!


        var scroll = false

        setupToolBar()

        binding.placeFeaturesText.setOnClickListener {

            if (binding.mainLayout.visibility == VISIBLE) {
                binding.mainLayout.visibility = GONE
                binding.root.scrollTo(0, 0)

            } else {
                scroll = true
                binding.mainLayout.visibility = VISIBLE
            }

        }

        binding.mainLayout.tag = binding.mainLayout.visibility

        binding.mainLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val newVisibility = binding.mainLayout.visibility
            if (binding.mainLayout.tag as Int != newVisibility) {
                if (scroll) {
                    binding.scroll.arrowScroll(ScrollView.FOCUS_DOWN)
                    scroll = false
                }
            }
        }




        fireStore.collection(getString(R.string.firestore_collection_cities))
            .document(city)
            .collection(type)
            .document(ref)
            .get()
            .addOnCompleteListener {
                if (!it.isSuccessful)
                    Log.e(TAG, "getModel: Error", it.exception)
                else {
                    if (it.result != null) {
                        currentPlace = it.result.toObject(RestaurantModel::class.java)!!
                        showPlaceDetails(currentPlace)
                        showViewCount()
                    }
                }

            }


    }

    private fun setupToolBar() {
        binding.topToolBar.setNavigationOnClickListener {
            finish()
        }
        binding.topToolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.call_place_menu -> {
                    makeCall()
                    true
                }
                R.id.map_place_menu -> {
                    navigateToMapsActivity()
                    true
                }

                else -> false
            }
        }
    }

    private fun showViewCount() {
        binding.progressBar.visibility = GONE
        binding.placeViewCountText.text = currentPlace.view_count.toString()
        fireStore.collection(getString(R.string.firestore_collection_cities))
            .document(city)
            .collection(type)
            .document(ref)
            .update("view_count", currentPlace.view_count + 1)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showPlaceDetails(currentPlace: RestaurantModel) {

        showMainInfo()
        val mainLayout = binding.mainLayout

        val mainParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )


        val mainFeaturesList = currentPlace.main_features
        val accessibilityList = currentPlace.accessibility
        val amenitiesList = currentPlace.amenities
        val atmosphereList = currentPlace.atmosphere
        val eatOptionsList = currentPlace.eat_options
        val healthAndSafetyList = currentPlace.health_and_safety
        val paymentList = currentPlace.payment
        val planningList = currentPlace.planning
        val publicList = currentPlace.public
        val serviceOptionsList = currentPlace.service_options
        val servicesList = currentPlace.services


        val allList = hashMapOf(
            "المميزات الرئيسية" to mainFeaturesList,
            "امكانية الوصول" to accessibilityList,
            "وسائل الراحة" to amenitiesList,
            "الأجواء" to atmosphereList,
            "خيارات الطعام" to eatOptionsList,
            "الصحة والأمان" to healthAndSafetyList,
            "التخطيط" to planningList,
            "طرق الدفع" to paymentList,
            "الجمهور" to publicList,
            "خيارات الخدمة" to serviceOptionsList,
            "الخدمات المقدمة" to servicesList
        )

        val check: Drawable =
            AppCompatResources.getDrawable(this, R.drawable.ic_check)!!

        check.setBounds(0, 0, 60, 60)

        val bold = resources.getFont(R.font.tajawal_bold)
        val regular = resources.getFont(R.font.tajawal_regular)

        for (list in allList) {


            if (list.value.size > 0) {
                val separator = TextView(this)
                separator.layoutParams = mainParams
                separator.height = 1
                separator.setBackgroundColor(resources.getColor(android.R.color.darker_gray))


                val textView = TextView(this)
                textView.text = list.key
                textView.textSize = 18F
                textView.setTextColor(getColor(android.R.color.black))
                textView.typeface = bold
                mainParams.bottomMargin = 16

                textView.layoutParams = mainParams

                val linearLayout = LinearLayout(this)
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.layoutParams = mainParams

                mainLayout.addView(textView)
                mainLayout.addView(linearLayout)

                for (service in list.value) {
                    val textView = TextView(this)
                    textView.text = service
                    textView.setTextColor(getColor(android.R.color.black))
                    textView.layoutParams = mainParams

                    textView.setCompoundDrawables(
                        null,
                        null,
                        check,
                        null
                    )
                    textView.compoundDrawablePadding = 16
                    textView.typeface = regular
                    linearLayout.addView(textView)
                }
                mainLayout.addView(separator)


            }

        }


    }

    private fun showMainInfo() {


        val imagesList = currentPlace.images

        val sliderView = binding.imageSlider
        val sliderAdapter = SliderAdapter(this)
        if (imagesList.size == 0) {
            imagesList.add("https://firebasestorage.googleapis.com/v0/b/add-place-d0852.appspot.com/o/placeholder.png?alt=media&token=149eda64-4708-4eb5-9763-701d5e1c7ef5")
        }
        sliderAdapter.renewItems(imagesList)
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.startAutoCycle();


        binding.placeNameText.text = currentPlace.main_info?.get("name").toString()
        binding.placeAddressText.text = currentPlace.main_info?.get("address").toString()
        binding.placeDescriptionText.text = currentPlace.main_info?.get("description").toString()


    }
    ///**********////****///***//****


    private fun makeCall() {
        val phoneNO = currentPlace.main_info?.get("phone").toString()
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNO, null))
        startActivity(intent)
    }

    private fun navigateToMapsActivity() {
        val location = currentPlace.main_info?.get("location") as GeoPoint
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("lat", location.latitude)
        intent.putExtra("lng", location.longitude)
        intent.putExtra("name", currentPlace.main_info?.get("name").toString())
        startActivity(intent)
    }


    companion object {
        private const val TAG = "PlaceActivity, myTag"
    }

    fun onWebsiteClick(view: View) {
        Log.d(TAG, "onWebsiteClick:")
        val intent =
            newFacebookIntent(packageManager, currentPlace.main_info?.get("website").toString())
        startActivity(intent)
    }


    fun newFacebookIntent(pm: PackageManager, url: String): Intent? {
        var uri = Uri.parse(url)
        try {
            val applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=$url")
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
        return Intent(Intent.ACTION_VIEW, uri)
    }

}
