package alyhuggan.covid_19.ui.covidmap

import alyhuggan.covid_19.R
import alyhuggan.covid_19.ui.BaseFragment
import alyhuggan.covid_19.ui.bottomsheet.BottomSheetFragment
import alyhuggan.covid_19.ui.totalstats.TotalStatsFragment
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_covid_map.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.io.File
import java.io.FileOutputStream

private const val TAG = "CovidMapFragment"

class CovidMapFragment : BaseFragment(), KodeinAware, GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<ViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retainInstance = true
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction().replace(R.id.main_frame, TotalStatsFragment()).commit()
            }
        })
        return inflater.inflate(R.layout.fragment_covid_map, container, false)
        // Inflate the layout for this fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view.onCreate(savedInstanceState)
        map_view.onResume()
        initializeUi()
        activateToolbar()
    }

    private fun initializeUi() {
        map_view.onResume()
        map_view.getMapAsync(this)
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

            val viewModel =
                ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

            viewModel.retrieveCoordinates().observe(viewLifecycleOwner, Observer { coordinateList ->

                if (coordinateList.isNotEmpty()) {
                    coordinateList.forEach {coordinate ->
                        map.addMarker(MarkerOptions().position(LatLng(coordinate.lat, coordinate.long)).title(coordinate.title))
                    }
                } else {
                    viewModel.getCoordinates(context!!)
                }
            })
            map.setOnMarkerClickListener(this)
        }

        activity!!.maintoolbar_share.setOnClickListener {

            map.snapshot {
                val file = saveBitmap(it, "screenshot.png")
                val apkURI = FileProvider.getUriForFile(context!!, context!!.applicationContext.packageName + ".provider", file)

                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, apkURI)
                shareIntent.type = "image/*"
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(Intent.createChooser(shareIntent, "share via"))
            }
        }
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
