package alyhuggan.covid_19.ui.bottomsheet

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.viewmodel.ViewModel
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.app.Dialog
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
import kotlinx.android.synthetic.main.bottomsheet_layout.*
import kotlinx.android.synthetic.main.bottomsheet_layout.view.*
import kotlinx.android.synthetic.main.items_bottomsheet.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "BottomSheetFragment"

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

    /*
    Checking a Country name was passed through to initialize the UI with
    */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val name = arguments!!.getString("COUNTRY")
        if (name != null) {
            getLiveData(name)
        }
    }

    /*
    Retrieving LiveData from StatsDaoImplementation and using the matched object to update the view and pie chart
    */
    private fun getLiveData(name: String) {

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        //Checking the passed country name matches a Country Stat object
        viewModel.getCountryStats().observe(viewLifecycleOwner, Observer { stat ->
            for (i in stat.indices) {
                if (stat[i].title == name) {
                    updateView(stat[i])
                    updatePieChart(stat[i])
                }
            }
        })
    }

    /*
    Overriding onCreateDialog and setting sheet to be expanded, maximising sheet in landscape by default
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomsheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomsheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    /*
    Update all the fields in the bottomsheet view
     */
    private fun updateView(stats: CountryStats) {

        val bottomsheet: BottomSheetDialogFragment = this

        //Setting the bottomsheet exit button to dismiss bottomshe
        bottomsheet_toolbar_exit.setOnClickListener {
            bottomsheet.dismiss()
        }

        /*
        Retrieving the individual sheets used in BottomSheet and then proceeding to fill in the data
         */
        val total = view!!.sheet_total
        val confirmed = view!!.sheet_confirmed
        val recovered = view!!.sheet_recovered
        val deaths = view!!.sheet_deaths

        total.bottomsheet_stats_title.text = getString(R.string.item_total_Text)
        total.bottomsheet_stats_cases.text = stats.totalCases
        total.bottomsheet_stats_cases.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorBlue
            )
        )
        total.bottomsheet_stats_icon.setImageResource(R.drawable.ic_total)

        confirmed.bottomsheet_stats_title.text = getString(R.string.item_current_Text)
        confirmed.bottomsheet_stats_cases.text = stats.currentCases
        confirmed.bottomsheet_stats_cases.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorRed
            )
        )
        confirmed.bottomsheet_stats_icon.setImageResource(R.drawable.ic_current)

        recovered.bottomsheet_stats_title.text = getString(R.string.item_recovered_Text)
        recovered.bottomsheet_stats_cases.text = stats.recovered
        recovered.bottomsheet_stats_cases.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorGreen
            )
        )
        recovered.bottomsheet_stats_icon.setImageResource(R.drawable.ic_recovered)

        deaths.bottomsheet_stats_title.text = getString(R.string.item_deaths_Text)
        deaths.bottomsheet_stats_cases.text = stats.deaths
        deaths.bottomsheet_stats_cases.setTextColor(
            ContextCompat.getColor(
                context!!,
                R.color.colorGrey
            )
        )
        deaths.bottomsheet_stats_icon.setImageResource(R.drawable.ic_deaths)

        //Setting the BottomSheet title to the country name
        bottomsheet_toolbar_country.text = stats.title

        //Setting the BottomSheet image to the country flag
        Picasso.get().load(stats.icon)
            .error(R.drawable.placeholder)
            .centerInside()
            .resize(140, 140)
            .into(bottomsheet_toolbar_flag)

    }

    /*
    Function used to updated BottomSheet pie chart with passed in country stat data
     */
    private fun updatePieChart(stat: CountryStats) {

        val pieChart = view!!.findViewById<PieChart>(R.id.totalstats_piechart)
        val statList = ArrayList<Pair<String, String>>()
        var statHolder: Pair<String, String>
        val xValues = ArrayList<PieEntry>()
        var casesReplace: String

        //Adding the data and title to the PieChart
        statList.add(Pair(stat.totalCases, getString(R.string.item_total_Text)))
        statList.add(Pair(stat.currentCases, getString(R.string.item_current_Text)))
        statList.add(Pair(stat.recovered, getString(R.string.item_recovered_Text)))
        statList.add(Pair(stat.deaths, getString(R.string.item_deaths_Text)))

        //Creating an ArrayList of colours to be used by the pie chart
        val colors = ArrayList<Int>()
        colors.add(ContextCompat.getColor(context!!, R.color.colorRed))
        colors.add(ContextCompat.getColor(context!!, R.color.colorGreen))
        colors.add(ContextCompat.getColor(context!!, R.color.colorGrey))

        //Running through statList and removing commas from "cases" so they can be converted to floats
        for (i in 1 until statList.size) {
            statHolder = statList[i]
            casesReplace = statHolder.first.replace(",", "")

            //Used to catch any data which isn't available, as is the case for the United Kingdom
            if (casesReplace == "N/A") {
                casesReplace = "0"
            } else {
                xValues.add(PieEntry(casesReplace.toFloat(), statHolder.second))
            }
        }

        //Checking for countries with no recovered data and removing the colour green
        if (xValues.size < 3) {
            colors.remove(Color.GREEN)
        }

        //Adding the xValues and colours to the dataset
        val dataSet = PieDataSet(xValues, "")
        dataSet.colors = colors
        dataSet.valueTextColor = Color.WHITE

        //General formatting for the PieChart below
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


