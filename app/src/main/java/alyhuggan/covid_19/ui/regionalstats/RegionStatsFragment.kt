package alyhuggan.covid_19.ui.regionalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.ui.generic.BaseFragment
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_region_stats.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "RegionStatsFrag"

class RegionStatsFragment : Fragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<ViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_region_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activateToolbar()
        initializeUi()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity!!.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavigation.menu.getItem(1).isChecked = true
    }

    private fun initializeUi() {

        val statList = ArrayList<CountryStats>()

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        viewModel.getCountryStats().observe(viewLifecycleOwner, Observer { stats ->
            statList.clear()
            stats.forEach { stat ->
                statList.add(stat)
            }
            if(!statList.isNullOrEmpty()) {
                region_progressbar.visibility = View.GONE
                updateRecyclerView(statList)
            }
        })
    }

    private fun activateToolbar() {
        val toolbar2: TextView = activity!!.findViewById(R.id.maintoolbar_title)
        toolbar2.text = getString(R.string.region_stats_Text)
    }

    private fun updateRecyclerView(statList: ArrayList<CountryStats>) {
        regionstats_recyclerview.layoutManager = LinearLayoutManager(context)
        regionstats_recyclerview.adapter =
            StatsRecyclerViewAdapter(
                statList,
                parentFragmentManager
            )
        regionstats_recyclerview.setHasFixedSize(true)
        animate()
    }

    private fun animate() {
        val resId = R.anim.animation_fall_down
        val animation: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, resId)
        regionstats_recyclerview.layoutAnimation = animation
    }

}
