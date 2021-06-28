package com.awad.gazaplace.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity.CENTER
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        ref = intent.getStringExtra("ref")!!
        city = intent.getStringExtra(getString(R.string.firestore_field_city))!!
        type = intent.getStringExtra(getString(R.string.firestore_field_type))!!


        var scroll = false

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
                    binding.root.arrowScroll(ScrollView.FOCUS_DOWN)
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
                    currentPlace = it.result.toObject(RestaurantModel::class.java)!!
                    showPlaceDetails(currentPlace)
                    showViewCount()
                }

            }

    }

    private fun showViewCount() {
        binding.placeViewCountText.text = currentPlace.view_count.toString()
        fireStore.collection(getString(R.string.firestore_collection_cities))
            .document(city)
            .collection(type)
            .document(ref)
            .update("view_count", currentPlace.view_count + 1)

    }

    @RequiresApi(Build.VERSION_CODES.M)
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


        for (list in allList) {

            if (list.value.size > 0) {
                val textView = TextView(this)
                textView.text = list.key
                textView.textSize = 20f
                textView.gravity = CENTER
                textView.setTextColor(getColor(android.R.color.black))

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
                    linearLayout.addView(textView)
                }
            }

        }


    }

    private fun showMainInfo() {


        val imagesList = currentPlace.images

        val sliderView = binding.imageSlider
        val sliderAdapter = SliderAdapter(this)
        sliderAdapter.renewItems(imagesList)
        sliderView.setSliderAdapter(sliderAdapter)
        sliderView.startAutoCycle();


        binding.placeNameText.text = currentPlace.main_info?.get("name").toString()
        binding.placeAddressText.text = currentPlace.main_info?.get("address").toString()
        binding.placeDescriptionText.text = currentPlace.main_info?.get("description").toString()
//        binding.placeWebsiteText.text = currentPlace.main_info?.get("website").toString()


    }
    ///**********////****///***//****


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.place_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.call_place_menu -> {
                val phoneNO = currentPlace.main_info?.get("phone").toString()

                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNO, null))
                startActivity(intent)
                return true
            }
            R.id.map_place_menu -> {
                val location = currentPlace.main_info?.get("location") as GeoPoint

                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra("lat", location.latitude)
                intent.putExtra("lng", location.longitude)
                intent.putExtra("name", currentPlace.main_info?.get("name").toString())
                startActivity(intent)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
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
