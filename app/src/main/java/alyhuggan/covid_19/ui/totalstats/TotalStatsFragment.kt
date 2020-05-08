package alyhuggan.covid_19.ui.totalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.Stats
import alyhuggan.covid_19.ui.BaseFragment
import alyhuggan.covid_19.ui.StatsRecyclerViewAdapter
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModel
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModelFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import kotlinx.android.synthetic.main.fragment_total_stats.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "TotalStatsFrag"

class TotalStatsFragment : BaseFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<TotalStatsViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_total_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activateToolbar()
        initializeUi()
    }

    private fun initializeUi() {

        val statList = ArrayList<Stats>()
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(TotalStatsViewModel::class.java)

        viewModel.getStats().observe(viewLifecycleOwner, Observer { stats ->
            stats.forEach { stat ->
                statList.add(stat)
            }
            updateRecyclerView(statList)
            updatePieChart(statList)
        })
    }

    override fun activateToolbar() {
        val toolbar = activity!!.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.title = "Total Stats"
        toolbar.setTitleTextColor(Color.WHITE)
    }

    private fun updateRecyclerView(statList: ArrayList<Stats>) {
        totalstats_recyclerview.layoutManager = LinearLayoutManager(context)
        totalstats_recyclerview.adapter = StatsRecyclerViewAdapter(statList)
        totalstats_recyclerview.setHasFixedSize(true)
//        val resId = R.anim.layout_animation_fall_down
//        val animation: LayoutAnimationController =
//            AnimationUtils.loadLayoutAnimation(context, resId)
//        activityblog_RV.layoutAnimation = animation
    }

    private fun updatePieChart(statList: ArrayList<Stats>) {

        val pieChart = view!!.findViewById<PieChart>(R.id.totalstats_piechart)
        val xValues = ArrayList<PieEntry>()
        var stat: Stats

        for (i in 1 until statList.size) {
            stat = statList[i]
            xValues.add(PieEntry(stat.cases.toFloat(), stat.title))
        }

        val dataSet = PieDataSet(xValues, "")
        dataSet.valueLinePart1OffsetPercentage = 200f
        dataSet.valueLinePart1Length = 0.1f
        dataSet.valueLinePart2Length = 0.5f

        val data = PieData(dataSet)
        data.setValueTextSize(8f)

        pieChart.setUsePercentValues(true)
        data.setValueFormatter(PercentFormatter())

        pieChart.data = data

    }

}
