package alyhuggan.covid_19.ui.covidmap

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.ui.bottomsheet.BottomSheetFragment
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_covid_map.*
import kotlinx.coroutines.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import kotlin.concurrent.thread

private const val TAG = "CovidMapFragment"

class CovidMapFragment : Fragment(), KodeinAware, GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<ViewModelFactory>()
//    private val map: MapView = map_view

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_covid_map, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
        outState.putString("test", "test")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view.onCreate(savedInstanceState)
        map_view.onResume()
        map_progressbar.visibility = View.VISIBLE
        initializeUi()
        activateToolbar()
    }

    private fun getGeoDetails(countryList: List<CountryStats>): List<Pair<String, Pair<Double, Double>>> {

        val geocoder: Geocoder = Geocoder(context)
        val coordinate = ArrayList<Pair<String, Pair<Double, Double>>>()

        for (i in countryList.indices) {
            val geoDetails = geocoder.getFromLocationName(countryList[i].title, 1)
            geoDetails.forEach {
                coordinate.add(Pair(countryList[i].title, Pair(it.latitude, it.longitude)))
            }
        }
        return coordinate
    }

    private fun getCoordinates(countryList: List<CountryStats>): ArrayList<Pair<String, Pair<Double, Double>>> {

        val coordinates = ArrayList<Pair<String, Pair<Double, Double>>>()

        if (!countryList.isNullOrEmpty()) {
            val result = getGeoDetails(countryList)
            result.forEach {
                coordinates.add(Pair(it.first, it.second))
            }
        }
        Log.d(TAG, "getCoordinates $coordinates")
        return coordinates
    }

    private fun initializeUi() {
        map_view.onResume()
        map_view.getMapAsync(this)
    }

    private fun shareScreenshot() {
        maintoolbar_share.setOnClickListener {
        }
    }

    fun newInstance(someInt: Int): BottomSheetFragment? {
        val myFragment = BottomSheetFragment()
        val args = Bundle()
        args.putInt("someInt", someInt)
        myFragment.arguments = args
        return myFragment
    }

    override fun onMarkerClick(country: Marker?): Boolean {
        val bottomSheetFragment = BottomSheetFragment()
        val args = Bundle()
        args.putString("COUNTRY", country!!.title)
        bottomSheetFragment.arguments = args
        bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
        return true
    }

    override fun onMapReady(map: GoogleMap?) {

        map_view.visibility = View.VISIBLE

        map!!.setOnMapLoadedCallback {
            Log.d(TAG, "Map loaded")

            val countryList = ArrayList<String>()
            var countries = mutableListOf<CountryStats>()
            var coordinates = mutableListOf<Pair<String, Pair<Double, Double>>>()
            val viewModel =
                ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

            viewModel.getCountryStats().observe(viewLifecycleOwner, Observer { stat ->
                if (!stat.isNullOrEmpty()) {
                    stat.forEach {
                        countries.add(it)
                    }
                }
            })

            viewModel.retrieveCoordinates().observe(viewLifecycleOwner, Observer { coordinate ->

                if(coordinate.isNotEmpty()) {
                        coordinate.forEach {
                            map.addMarker(MarkerOptions()
                                .position(LatLng(it.second.first, it.second.second))
                                .title(it.first))
                        }
                    } else {
                    viewModel.getCoordinates(countries, context!!)
                }
            })

            if (!coordinates.isNullOrEmpty()) {
                for (i in coordinates.indices)
                    map.addMarker(MarkerOptions()
                            .position(LatLng(coordinates[i].second.first, coordinates[i].second.second))
                            .title(coordinates[i].first)
                    )}
            map.setOnMarkerClickListener(this)
            map_progressbar.visibility = View.VISIBLE
        }
    }

    private fun addcoords(coordinates: ArrayList<Pair<String, Pair<Double, Double>>>, map: GoogleMap?) {
        if (!coordinates.isNullOrEmpty()) {
            for (i in coordinates.indices)
                map!!.addMarker(MarkerOptions()
                    .position(LatLng(coordinates[i].second.first, coordinates[i].second.second))
                    .title(coordinates[i].first)
                )}
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity!!.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavigation.menu.getItem(2).isChecked = true
    }

    private fun activateToolbar() {
        val toolbar2: TextView = activity!!.findViewById(R.id.maintoolbar_title)
        toolbar2.text = getString(R.string.covid_map_Text)
    }
}
