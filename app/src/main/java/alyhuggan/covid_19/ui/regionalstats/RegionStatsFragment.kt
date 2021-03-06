package alyhuggan.covid_19.ui.regionalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.ui.BaseFragment
import alyhuggan.covid_19.ui.totalstats.TotalStatsFragment
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_region_stats.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "RegionStatsFrag"

class RegionStatsFragment : BaseFragment(), KodeinAware {

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
        return inflater.inflate(R.layout.fragment_region_stats, container, false)
    }

    /*
    Function used to call setUpScreenshot from the Base Fragment class, call the initializeUI and activate the toolbar functions
    */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpScreenshot()
        initializeUi()
        activateToolbar()
    }

    /*
    When fragment is resumed the bottom navigation is set to reflect the correct fragment
    */
    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity!!.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavigation.menu.getItem(1).isChecked = true
    }

    /*
    Function to get country stat LiveData from View Model then pass it to RegionStatsRecyclerViewAdapter to populate the page
     */
    private fun initializeUi() {

        val statList = ArrayList<CountryStats>()

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        viewModel.getCountryStats().observe(viewLifecycleOwner, Observer { stats ->
            statList.clear()
            stats.forEach { stat ->
                statList.add(stat)
            }
            if (!statList.isNullOrEmpty()) {
                //Setting progressbar to gone once the data has been fetched
                region_progressbar.visibility = View.GONE
                updateRecyclerView(statList)
            }
        })
    }

    /*
    Toolbar text set to display the page title
    */
    private fun activateToolbar() {
        val toolbar2: TextView = activity!!.findViewById(R.id.maintoolbar_title)
        toolbar2.text = getString(R.string.region_stats_Text)
    }

    /*
    Retrieves the recyclerview, sets the adapter to RegionStatsRecyclerViewAdapter and passes it the list of country stats
     */
    private fun updateRecyclerView(statList: ArrayList<CountryStats>) {
        regionstats_recyclerview.layoutManager = LinearLayoutManager(context)
        regionstats_recyclerview.adapter =
            RegionStatsRecyclerViewAdapter(statList, parentFragmentManager)
        regionstats_recyclerview.setHasFixedSize(true)
        //Added animation for a more fluid feeling
        animate()
    }

    /*
    Sets the recyclerviews animation
     */
    private fun animate() {
        val resId = R.anim.animation_fall_down
        val animation: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, resId)
        regionstats_recyclerview.layoutAnimation = animation
    }
}
