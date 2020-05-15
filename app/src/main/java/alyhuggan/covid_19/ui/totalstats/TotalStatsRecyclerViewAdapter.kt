package alyhuggan.covid_19.ui.totalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.Stat
import alyhuggan.covid_19.ui.bottomsheet.BottomSheetFragment
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.items_stats.view.*

private const val TAG = "TotalStatRecyclerVA"

/*
Class for holding the fields
*/
class TotalStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.stats_title
    val updated: TextView = view.stats_updated
    val cases: TextView = view.stats_cases
    val icon: ImageView = view.stats_icon
}

class TotalStatsRecyclerViewAdapter(
    private val statList: List<Stat>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<TotalStatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalStatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_stats, parent, false)
        return TotalStatsViewHolder(view)
    }

    override fun getItemCount() = statList.size

    override fun onBindViewHolder(holder: TotalStatsViewHolder, position: Int) {
        val title = holder.title
        val updated = holder.updated
        val cases = holder.cases
        val icon = holder.icon

        //Checking if the list is empty and updating the fields accordingly
        if (statList.isEmpty()) {
            title.text = ""
            updated.text = ""
            cases.text = ""
            icon.setImageResource(R.drawable.placeholder)
        } else {
            //Retrieving each stat object and updating the fields accordingly
            val statItem = statList[position]
            var iconResource: Int = 0
            val context = holder.title.context

            title.text = statItem.title
            updated.text = "Last Updated: ${statItem.updated}"
            cases.text = statItem.cases

            //Checking title and updating icon and text colour accordingly
            when (statItem.title) {
                "Confirmed Cases" -> {
                    iconResource = R.drawable.ic_total
                    cases.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
                }
                "Currently Infected" -> {
                    iconResource = R.drawable.ic_current
                    cases.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
                "Recovered" -> {
                    iconResource = R.drawable.ic_recovered
                    cases.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                }
                "Deaths" -> {
                    Log.d(TAG, "Skull")
                    iconResource = R.drawable.ic_deaths
                    cases.setTextColor(ContextCompat.getColor(context, R.color.colorGrey))
                }
            }
            Picasso.get().load(iconResource)
                .error(R.drawable.placeholder)
                .centerInside()
                .resize(180, 180)
                .into(icon)
        }
    }
}





