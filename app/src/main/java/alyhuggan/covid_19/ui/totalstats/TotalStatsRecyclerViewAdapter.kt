package alyhuggan.covid_19.ui.totalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.Stats
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

private const val TAG = "StatRecyclerViewAdapt"

class StatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.stats_title
    val updated: TextView = view.stats_updated
    val cases: TextView = view.stats_cases
    val icon: ImageView = view.stats_icon
}

class StatsRecyclerViewAdapter(
    private val statList: List<Stats>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<StatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_stats, parent, false)
        return StatsViewHolder(view)
    }

    override fun getItemCount() = statList.size

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
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
            cases.text = statItem.cases

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
//                            holder.title.text.toString()
                        )
                    bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
                }

            } else {

                var iconResource: Int = 0
                val context = holder.title.context

                when (statItem.title) {
                    "Total Confirmed Cases" -> {
                        iconResource = R.drawable.ic_globe
                        cases.setTextColor(ContextCompat.getColor(context, R.color.colorBlue))
                    }
                    "Currently Infected" -> {
                        iconResource = R.drawable.ic_virus
                        cases.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                    }
                    "Recovered" -> {
                        iconResource = R.drawable.ic_heart
                        cases.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                    }
                    "Deaths" -> {
                        Log.d(TAG, "Skull")
                        iconResource = R.drawable.ic_skull
                        cases.setTextColor(ContextCompat.getColor(context, R.color.colorGrey))
                    }
                }
                Picasso.get().load(iconResource)
                    .error(R.drawable.placeholder)
                    .centerInside()
                    .resize(140, 140)
                    .into(icon)
            }
        }
    }
}




