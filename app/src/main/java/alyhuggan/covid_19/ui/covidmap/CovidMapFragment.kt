package alyhuggan.covid_19.ui.covidmap

import alyhuggan.covid_19.R
import alyhuggan.covid_19.ui.bottomsheet.BottomSheetFragment
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModel
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModelFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_covid_map.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "CovidMapFragment"

class CovidMapFragment : Fragment(), KodeinAware, GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<TotalStatsViewModelFactory>()
    lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_covid_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState != null) {

        }
        map_view.onCreate(savedInstanceState)
        map_view.onResume()
        initializeUi()
    }

    private fun initializeUi() {

        map_view.onResume()
        map_view.getMapAsync(this)

    }

    private fun getGeoDetails(country: String): Pair<Double, Double> {
        val geocoder: Geocoder = Geocoder(context)
        val geoDetails = geocoder.getFromLocationName(country, 1)
        geoDetails.forEach {
            return Pair(it.latitude, it.longitude)
        }
        return Pair(0.0, 0.0)
    }

    fun newInstance(someInt: Int): BottomSheetFragment? {
        val myFragment = BottomSheetFragment()
        val args = Bundle()
        args.putInt("someInt", someInt)
        myFragment.setArguments(args)
        return myFragment
    }

    override fun onMarkerClick(country: Marker?): Boolean {
//        val bottomSheetFragment = BottomSheetFragment(country!!.title)
        val bottomSheetFragment = BottomSheetFragment()
        val args = Bundle()
        args.putString("COUNTRY", country!!.title)
        bottomSheetFragment.arguments = args
        bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
        return true
    }

    override fun onMapReady(map: GoogleMap?) {

        val countryList = ArrayList<String>()
        val coordinates = ArrayList<Pair<String, Pair<Double, Double>>>()
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(TotalStatsViewModel::class.java)

        viewModel.getCountryStats().observe(viewLifecycleOwner, Observer { stat ->
            countryList.clear()
            for (i in stat.indices) {
                if (!stat.isNullOrEmpty()) {
                    Log.d(TAG, "stat = ${stat[i]}")
                    coordinates.add(Pair(stat[i].title, getGeoDetails(stat[i].title)))
                }
            }
        })

        if (!coordinates.isNullOrEmpty()) {
            for (i in coordinates.indices)
                map!!.addMarker(
                    MarkerOptions()
                        .position(LatLng(coordinates[i].second.first, coordinates[i].second.second))
                        .title(coordinates[i].first)
                )
        }
        map!!.setOnMarkerClickListener(this)
                    map_view.visibility = View.VISIBLE
        //Cancel loading bar here
    }
}
