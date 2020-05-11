package alyhuggan.covid_19.ui.regionalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.repository.stats.Stats
import alyhuggan.covid_19.ui.bottomsheet.BottomSheetFragment
import alyhuggan.covid_19.ui.totalstats.StatsViewHolder
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_country_stats.view.*
import kotlinx.android.synthetic.main.items_stats.view.*

private const val TAG = "RegionStatRecyclerVA"

class RegionStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.countrystats_title
    val updated: TextView = view.countrystats_updated
    val cases: TextView = view.countrystats_cases
    val icon: ImageView = view.countrystats_icon
}

class StatsRecyclerViewAdapter(
    private val statList: List<CountryStats>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<RegionStatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionStatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_country_stats, parent, false)
        return RegionStatsViewHolder(view)
    }

    override fun getItemCount() = statList.size

    override fun onBindViewHolder(holder: RegionStatsViewHolder, position: Int) {
        val title = holder.title
        val updated = holder.updated
        val cases = holder.cases
        val icon = holder.icon

        if (statList.isEmpty()) {
            title.text = ""
            updated.text = ""
            cases.text = ""
            icon.setImageResource(R.drawable.placeholder)
        } else {
            val statItem = statList[position]
            title.text = statItem.title
            updated.text = "Last Updated: ${statItem.updated}"
            cases.text = statItem.totalCases

            if (statItem.icon != null) {

                cases.setTextColor(Color.BLUE)

                Picasso.get().load(statItem.icon)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .centerInside()
                    .resize(140, 140)
                    .into(icon)

                holder.itemView.setOnClickListener() {
                    Log.d(TAG, "Hello ${holder.title.text}")

                    val bottomSheetFragment =
                        BottomSheetFragment(
                            holder.title.text.toString()
                        )
                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
                }

            } else {
                var totalIcon: Int = 0

                when (statItem.title) {
                    "Total Confirmed Cases" -> {
                        totalIcon = R.drawable.ic_globe
                        cases.setTextColor(Color.BLUE)
                    }
                    "Currently Infected" -> {
                        totalIcon = R.drawable.ic_virus
                        cases.setTextColor(Color.RED)
                    }
                    "Recovered" -> {
                        totalIcon = R.drawable.ic_heart
                        cases.setTextColor(Color.GREEN)
                    }
                    "Deaths" -> {
                        Log.d(TAG, "Skull")
                        totalIcon = R.drawable.ic_skull
                        cases.setTextColor(Color.GRAY)
                    }
                }
                Picasso.get().load(totalIcon)
                    .error(R.drawable.placeholder)
                    .centerInside()
                    .resize(140, 140)
                    .into(icon)
            }
        }
    }
}




