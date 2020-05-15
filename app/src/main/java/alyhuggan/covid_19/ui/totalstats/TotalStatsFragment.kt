package alyhuggan.covid_19.ui.totalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.Stat
import alyhuggan.covid_19.ui.BaseFragment
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
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
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
import kotlinx.android.synthetic.main.fragment_total_stats.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

private const val TAG = "TotalStatsFrag"

class TotalStatsFragment : BaseFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory by instance<ViewModelFactory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Handling any presses of the back button
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
        return inflater.inflate(R.layout.fragment_total_stats, container, false)
    }

    /*
    Function used to call setUpScreenshot from the Base Fragment class, call the initializeUI and activate the toolbar functions
    */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpScreenshot()
        activateToolbar()
        initializeUi()
    }

    /*
    When fragment is resumed the bottom navigation is set to reflect the correct fragment
    */
    override fun onResume() {
        super.onResume()
        val bottomNavigation = activity!!.findViewById<BottomNavigationView>(R.id.bottom_nav_bar)
        bottomNavigation.menu.getItem(0).isChecked = true
    }

    /*
    Function to get stat LiveData from View Model then pass it to TotalStatsRecyclerViewAdapter to populate the page
    */
    private fun initializeUi() {

        val statList = ArrayList<Stat>()

        val viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModel::class.java)

        viewModel.getStats().observe(viewLifecycleOwner, Observer { stats ->
            statList.clear()
            stats.forEach { stat ->
                statList.add(stat)
            }
            if (!statList.isNullOrEmpty()) {
                //Setting progressbar to gone once the data has been fetched
                total_progressbar.visibility = View.GONE
                //Updating RecyclerView and PieChart with the list of Stat objects
                updateRecyclerView(statList)
                updatePieChart(statList)
            }
        })
    }

    /*
    Toolbar text set to display the page title
    */
    private fun activateToolbar() {
        val title: TextView = activity!!.findViewById(R.id.maintoolbar_title)
        title.text = getString(R.string.total_stats_Text)
    }

    /*
    Retrieves the recyclerview, sets the adapter to TotalStatsRecyclerViewAdapter and passes it the list of stats
    */
    private fun updateRecyclerView(statList: ArrayList<Stat>) {
        totalstats_recyclerview.layoutManager = LinearLayoutManager(context)
        totalstats_recyclerview.adapter = TotalStatsRecyclerViewAdapter(statList, parentFragmentManager)
        totalstats_recyclerview.setHasFixedSize(true)
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
        totalstats_recyclerview.layoutAnimation = animation
    }

    private fun updatePieChart(statList: ArrayList<Stat>) {

        val pieChart = view!!.findViewById<PieChart>(R.id.totalstats_piechart)
        val xValues = ArrayList<PieEntry>()
        var stat: Stat
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
