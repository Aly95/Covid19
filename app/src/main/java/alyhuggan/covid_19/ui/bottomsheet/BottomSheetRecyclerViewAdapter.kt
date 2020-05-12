package alyhuggan.covid_19.ui.bottomsheet

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.Stats
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.items_bottomsheet.view.*

private const val TAG = "BottomSheetRecyclerVA"

class SheetStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.bottomsheet_stats_title
    val cases: TextView = view.bottomsheet_stats_cases
    val icon: ImageView = view.bottomsheet_stats_icon
}

class StatsRecyclerViewAdapter(private val stat: Stats) :
    RecyclerView.Adapter<SheetStatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SheetStatsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.items_bottomsheet, parent, false)
        return SheetStatsViewHolder(view)
    }

    override fun getItemCount() = 1

    override fun onBindViewHolder(holder: SheetStatsViewHolder, position: Int) {

        val title = holder.title
        val cases = holder.cases
        val icon = holder.icon
        val iconHolder: Int

            title.text = stat.title
            cases.text = stat.cases

            when (stat.title) {
                "Total Confirmed Cases" -> {
                    iconHolder = R.drawable.ic_total
                    cases.setTextColor(Color.BLUE)
                }
                "Currently Infected" -> {
                    iconHolder = R.drawable.ic_current
                    cases.setTextColor(Color.RED)
                }
                "Recovered" -> {
                    iconHolder = R.drawable.ic_recovered
                    cases.setTextColor(Color.GREEN)
                }
                "Deaths" -> {
                    iconHolder = R.drawable.ic_deaths
                    cases.setTextColor(Color.GRAY)
                }
                else -> {
                    iconHolder = R.drawable.placeholder
                }
            }
            Picasso.get().load(iconHolder)
                .error(R.drawable.placeholder)
                .centerInside()
                .resize(60, 60)
                .into(icon)
        }
    }






