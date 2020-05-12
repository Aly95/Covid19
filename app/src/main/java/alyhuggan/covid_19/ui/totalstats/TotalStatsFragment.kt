package alyhuggan.covid_19.ui.totalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.Stats
import alyhuggan.covid_19.ui.generic.BaseFragment
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_total_stats.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "TotalStatsFrag"

class TotalStatsFragment : Fragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<ViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_total_stats, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activateToolbar()
        initializeUi()
    }

    private fun initializeUi() {

        val statList = ArrayList<Stats>()

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        viewModel.getStats().observe(viewLifecycleOwner, Observer { stats ->
            statList.clear()
            stats.forEach { stat ->
                statList.add(stat)
            }
            if(!statList.isNullOrEmpty()) {
                total_progressbar.visibility = View.GONE
                updateRecyclerView(statList)
                updatePieChart(statList)
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    private fun activateToolbar() {
        val title: TextView = activity!!.findViewById(R.id.maintoolbar_title)
        title.text = getString(R.string.total_stats_Text)
    }

    private fun updateRecyclerView(statList: ArrayList<Stats>) {
        totalstats_recyclerview.layoutManager = LinearLayoutManager(context)
        totalstats_recyclerview.adapter =
            StatsRecyclerViewAdapter(
                statList,
                parentFragmentManager
            )
        totalstats_recyclerview.setHasFixedSize(true)
        animate()
    }

    private fun animate() {
        val resId = R.anim.animation_fall_down
        val animation: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, resId)
        totalstats_recyclerview.layoutAnimation = animation
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity!!.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavigation.menu.getItem(0).isChecked = true
    }

    private fun updatePieChart(statList: ArrayList<Stats>) {

        val pieChart = view!!.findViewById<PieChart>(R.id.totalstats_piechart)
        val xValues = ArrayList<PieEntry>()
        var stat: Stats
        var casesReplace: String

        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(context!!, R.color.colorRed))
        colors.add(ContextCompat.getColor(context!!, R.color.colorGreen))
        colors.add(ContextCompat.getColor(context!!, R.color.colorGrey))

        for (i in 1 until statList.size) {
            stat = statList[i]
            casesReplace = stat.cases.replace(",", "")
            xValues.add(PieEntry(casesReplace.toFloat(), stat.title))
        }

        val dataSet = PieDataSet(xValues, "")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.WHITE
        val data = PieData(dataSet)
        data.setValueTextSize(8f)
        data.setValueFormatter(PercentFormatter())

        val l = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.textColor = ContextCompat.getColor(context!!, R.color.secondary_text)
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.textSize = 15f

        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.setHoleColor(Color.TRANSPARENT)

        pieChart.data = data
        pieChart.invalidate()
    }

}
