package alyhuggan.covid_19.ui.regionalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.Stats
import alyhuggan.covid_19.ui.BaseFragment
import alyhuggan.covid_19.ui.StatsRecyclerViewAdapter
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModel
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModelFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_region_stats.*
import kotlinx.android.synthetic.main.fragment_total_stats.*
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_region_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activateToolbar()
        initializeUi()
    }

    private fun initializeUi() {

        val statList = ArrayList<Stats>()

        val viewModel = ViewModelProvider(this, viewModelFactory).get(TotalStatsViewModel::class.java)

        viewModel.getCountryStats().observe(viewLifecycleOwner, Observer { stats ->
            Log.d(TAG, "Stats = $stats")
            stats.forEach { stat ->
                statList.add(stat)
            }
            updateRecyclerView(statList)
        })
    }

    override fun activateToolbar() {
        val toolbar = activity!!.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.title = "Total Stats"
        toolbar.setTitleTextColor(Color.WHITE)
    }

    private fun updateRecyclerView(statList: ArrayList<Stats>) {
        regionstats_recyclerview.layoutManager = LinearLayoutManager(context)
        regionstats_recyclerview.adapter = StatsRecyclerViewAdapter(statList)
        regionstats_recyclerview.setHasFixedSize(true)
        animate()
    }

    private fun animate() {
        val resId = R.anim.animation_fall_down
        val animation: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, resId)
        regionstats_recyclerview.layoutAnimation = animation
    }

}
