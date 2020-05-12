package alyhuggan.covid_19.ui.bottomsheet

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottomsheet_layout.*
import kotlinx.android.synthetic.main.bottomsheet_layout.view.*
import kotlinx.android.synthetic.main.items_bottomsheet.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


private const val TAG = "BottomSheetFragment"

//class BottomSheetFragment(private val country: String?) : BottomSheetDialogFragment(), KodeinAware {
class BottomSheetFragment() : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<ViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_layout, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomsheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomsheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val name = arguments!!.getString("COUNTRY")
        if(name != null) {
            initializeUi(name)
        }
    }

    private fun initializeUi(name: String) {

        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        viewModel.getCountryStats().observe(viewLifecycleOwner, Observer { stat ->
            for (i in stat.indices) {
//                if (stat[i].title == country) {
                if (stat[i].title == name) {
                    updateView(stat[i])
                    updatePieChart(stat[i])
                }
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChange")
    }

    private fun updateView(stats: CountryStats) {

        val bottomsheet: BottomSheetDialogFragment = this

        val total = view!!.sheet_total
        val confirmed = view!!.sheet_confirmed
        val recovered = view!!.sheet_recovered
        val deaths = view!!.sheet_deaths

        total.bottomsheet_stats_title.text = getString(R.string.item_total_Text)
        total.bottomsheet_stats_cases.text = stats.totalCases
        total.bottomsheet_stats_cases.setTextColor(ContextCompat.getColor(context!!, R.color.colorBlue))
        total.bottomsheet_stats_icon.setImageResource(R.drawable.ic_globe)

        confirmed.bottomsheet_stats_title.text = getString(R.string.item_current_Text)
        confirmed.bottomsheet_stats_cases.text = stats.currentCases
        confirmed.bottomsheet_stats_cases.setTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        confirmed.bottomsheet_stats_icon.setImageResource(R.drawable.ic_virus)

        recovered.bottomsheet_stats_title.text = getString(R.string.item_recovered_Text)
        recovered.bottomsheet_stats_cases.text = stats.recovered
        recovered.bottomsheet_stats_cases.setTextColor(ContextCompat.getColor(context!!, R.color.colorGreen))
        recovered.bottomsheet_stats_icon.setImageResource(R.drawable.ic_heart)

        deaths.bottomsheet_stats_title.text = getString(R.string.item_deaths_Text)
        deaths.bottomsheet_stats_cases.text = stats.deaths
        deaths.bottomsheet_stats_cases.setTextColor(ContextCompat.getColor(context!!, R.color.colorGrey))
        deaths.bottomsheet_stats_icon.setImageResource(R.drawable.ic_skull)

        bottomsheet_toolbar_country.text = stats.title

        Picasso.get().load(stats.icon)
            .error(R.drawable.placeholder)
            .centerInside()
            .resize(140, 140)
            .into(bottomsheet_toolbar_flag)

        bottomsheet_toolbar_exit.setOnClickListener {
            bottomsheet.dismiss()
        }
    }

    private fun updatePieChart(stat: CountryStats) {

        val pieChart = view!!.findViewById<PieChart>(R.id.totalstats_piechart)
        val statList = ArrayList<Pair<String, String>>()
        var statHolder: Pair<String, String>

        val xValues = ArrayList<PieEntry>()
        var casesReplace: String

        statList.add(Pair(stat.totalCases, getString(R.string.item_total_Text)))
        statList.add(Pair(stat.currentCases, getString(R.string.item_current_Text)))
        statList.add(Pair(stat.recovered, getString(R.string.item_recovered_Text)))
        statList.add(Pair(stat.deaths, getString(R.string.item_deaths_Text)))

        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(context!!, R.color.colorRed))
        colors.add(ContextCompat.getColor(context!!, R.color.colorGreen))
        colors.add(ContextCompat.getColor(context!!, R.color.colorGrey))

        for (i in 1 until statList.size) {
            statHolder = statList[i]
            casesReplace = statHolder.first.replace(",", "")
            Log.d(TAG, "casesReplace $casesReplace")

            if (casesReplace == "N/A") {
                casesReplace = "0"
            } else {
                xValues.add(PieEntry(casesReplace.toFloat(), statHolder.second))
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


