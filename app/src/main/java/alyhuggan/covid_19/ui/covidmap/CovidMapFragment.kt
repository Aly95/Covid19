package alyhuggan.covid_19.ui.covidmap

import alyhuggan.covid_19.R
import alyhuggan.covid_19.ui.BaseFragment
import alyhuggan.covid_19.ui.bottomsheet.BottomSheetFragment
import alyhuggan.covid_19.ui.totalstats.TotalStatsFragment
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
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

private const val TAG = "CovidMapFragment"

class CovidMapFragment : BaseFragment(), KodeinAware, GoogleMap.OnMarkerClickListener,
    OnMapReadyCallback {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<ViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Handling any presses of the back button
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, TotalStatsFragment()).commit()
            }
        })
        return inflater.inflate(R.layout.fragment_covid_map, container, false)
        // Inflate the layout for this fragment
    }

    /*
    Function used to create a map instance, call the initializeUI and activate the toolbar functions
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view.onCreate(savedInstanceState)
        map_view.onResume()
        initializeUi()
        activateToolbar()
    }

    /*
    When fragment is resumed the bottom navigation is set to reflect the correct fragment
    */
    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity!!.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavigation.menu.getItem(2).isChecked = true
    }

    /*
     Retrieves the map view
     */
    private fun initializeUi() {
        map_view.getMapAsync(this)
    }

    /*
    Toolbar text set to display the page title
     */
    private fun activateToolbar() {
        val toolbar2: TextView = activity!!.findViewById(R.id.maintoolbar_title)
        toolbar2.text = getString(R.string.covid_map_Text)
    }

    /*
    Handles the opening of the BottomSheet if a marker is clicked
     */
    override fun onMarkerClick(country: Marker?): Boolean {
        val bottomSheetFragment = BottomSheetFragment()
        val args = Bundle()
        args.putString("COUNTRY", country!!.title)
        bottomSheetFragment.arguments = args
        bottomSheetFragment.show(fragmentManager!!, bottomSheetFragment.tag)
        return true
    }

    /*
    Function called when map is ready, used to set up markers and provide screenshot capabilities
     */
    override fun onMapReady(map: GoogleMap?) {

        //Set map_view to visible once the map has been loaded
        map_view.visibility = View.VISIBLE

        //Function called when map is loaded, not just ready
        map!!.setOnMapLoadedCallback {

            val viewModel =
                ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

            //Retrieves coordinateList from View Model
            viewModel.retrieveCoordinates().observe(viewLifecycleOwner, Observer { coordinateList ->

                if (coordinateList.isNotEmpty()) {
                    coordinateList.forEach { coordinate ->
                        map.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    coordinate.lat,
                                    coordinate.long
                                )
                            ).title(coordinate.title)
                        )
                    }
                } else {
                    //Calls View Model to set up coordinates to be retrieved if coordinateList has not been created
                    viewModel.getCoordinates(context!!)
                }
            })
            //Click listener needed to handle the opening and closing of the bottom sheet
            map.setOnMarkerClickListener(this)
        }

        //Creating a listener object for the share button
        activity!!.maintoolbar_share.setOnClickListener {

            if (checkPermissions()) {
                //Using map.snapshot as Google Map Api2 shows blank on a full screen screenshot
                map.snapshot {
                    val file = saveBitmap(it, "screenshot.png")
                    val apkURI = FileProvider.getUriForFile(
                        context!!,
                        context!!.applicationContext.packageName + ".provider",
                        file
                    )

                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.putExtra(Intent.EXTRA_STREAM, apkURI)
                    shareIntent.type = "image/*"
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivity(Intent.createChooser(shareIntent, "share via"))
                }
            }
        }
    }
}
