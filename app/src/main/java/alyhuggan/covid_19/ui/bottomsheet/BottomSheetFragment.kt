package alyhuggan.covid_19.ui.bottomsheet

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.BottomSheetStats
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModel
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModelFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottomsheet_layout.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "BottomSheetFragment"

class BottomSheetFragment(private val country: String) : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<TotalStatsViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_layout, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeUi()
    }

    private fun initializeUi() {

        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(TotalStatsViewModel::class.java)
        
        viewModel.getIndividualStat(country).observe(viewLifecycleOwner, Observer { stat ->
            if (stat.isNotEmpty()) {
                updateView(stat)
                updatePieChart(stat)
            }
        })
    }

    private fun updateView(stats: List<BottomSheetStats>) {
        bottomsheet_recyclerview.layoutManager = LinearLayoutManager(context)
        bottomsheet_recyclerview.adapter = StatsRecyclerViewAdapter(stats)
        bottomsheet_recyclerview.setHasFixedSize(true)

        bottomsheet_toolbar_country.text = country

        Picasso.get().load(stats[0].icon)
            .error(R.drawable.placeholder)
            .centerInside()
            .resize(140, 140)
            .into(bottomsheet_toolbar_flag)
    }

    private fun clearView() {
        bottomsheet_toolbar_country.text = ""
        bottomsheet_toolbar_flag.setImageResource(android.R.color.transparent)
    }

    private fun updatePieChart(statList: List<BottomSheetStats>) {

        val pieChart = view!!.findViewById<PieChart>(R.id.totalstats_piechart)

        Log.d(TAG, "statList $statList")

        val xValues = ArrayList<PieEntry>()
        var stat: BottomSheetStats
        var casesReplace: String

        val colors = ArrayList<Int>()
        colors.add(Color.RED)
        colors.add(Color.GREEN)
        colors.add(Color.GRAY)

        for (i in 1 until statList.size) {
            stat = statList[i]
            casesReplace = stat.cases.replace(",", "")
            Log.d(TAG, "casesReplace $casesReplace")

            if (casesReplace == "N/A") {
                casesReplace = "0"
            } else {
                xValues.add(PieEntry(casesReplace.toFloat(), stat.title))
            }
        }

        if (xValues.size < 3) {
            colors.remove(Color.GREEN)
        }

        val dataSet = PieDataSet(xValues, "")
        dataSet.colors = colors

        dataSet.valueTextColor = Color.WHITE
        val data = PieData(dataSet)
        data.setValueTextSize(8f)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter())

        val l = pieChart.legend
        l.isEnabled = false

        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(true)
        pieChart.setDrawEntryLabels(false)
        pieChart.setHoleColor(Color.TRANSPARENT)

        pieChart.data = data
        pieChart.invalidate()
    }

}


