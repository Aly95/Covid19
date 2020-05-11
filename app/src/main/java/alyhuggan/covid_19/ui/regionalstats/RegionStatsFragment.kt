package alyhuggan.covid_19.ui.regionalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.ui.generic.BaseFragment
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModel
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModelFactory
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_region_stats.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "RegionStatsFrag"

class RegionStatsFragment : BaseFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<TotalStatsViewModelFactory>()

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

    private fun initializeUi() {

        val statList = ArrayList<CountryStats>()

        val viewModel = ViewModelProvider(this, viewModelFactory).get(TotalStatsViewModel::class.java)

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

    override fun activateToolbar() {
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
