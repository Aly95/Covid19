package alyhuggan.covid_19.ui.bottomsheet

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.BottomSheetStats
import alyhuggan.covid_19.repository.stats.CountryStat
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.items_bottomsheet.view.*
import kotlinx.android.synthetic.main.items_stats.view.*
import kotlinx.android.synthetic.main.items_stats.view.stats_cases
import kotlinx.android.synthetic.main.items_stats.view.stats_icon

private const val TAG = "BottomSheetRecyclerVA"

class SheetStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.bottomsheet_stats_title
    val cases: TextView = view.bottomsheet_stats_cases
    val icon: ImageView = view.bottomsheet_stats_icon
}

class StatsRecyclerViewAdapter(private val statList: List<BottomSheetStats>) :
    RecyclerView.Adapter<SheetStatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetStatsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_bottomsheet, parent, false)
        return SheetStatsViewHolder(view)
    }

    override fun getItemCount() = statList.size

    override fun onBindViewHolder(holder: SheetStatsViewHolder, position: Int) {

        val title = holder.title
        val cases = holder.cases
        val icon = holder.icon
        val iconHolder: Int

        if (statList.isEmpty()) {
            title.text = ""
            cases.text = ""
        } else {

            val statItem = statList[position]
            title.text = statItem.title
            cases.text = statItem.cases

            when (statItem.title) {
                "Total Confirmed Cases" -> {
                    iconHolder = R.drawable.ic_globe
                    cases.setTextColor(Color.BLUE)
                }
                "Currently Infected" -> {
                    iconHolder = R.drawable.ic_virus
                    cases.setTextColor(Color.RED)
                }
                "Recovered" -> {
                    iconHolder = R.drawable.ic_heart
                    cases.setTextColor(Color.GREEN)
                }
                "Deaths" -> {
                    iconHolder = R.drawable.ic_skull
                    cases.setTextColor(Color.GRAY)
                }
                else -> {
                    iconHolder = R.drawable.placeholder
                }
            }
            Picasso.get().load(iconHolder)
                .error(R.drawable.placeholder)
                .centerInside()
                .resize(140, 140)
                .into(icon)
        }
    }
}





