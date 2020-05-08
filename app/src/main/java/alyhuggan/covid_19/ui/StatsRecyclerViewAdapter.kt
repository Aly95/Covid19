package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.Stats
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.items_stats.view.*

class StatsViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val title: TextView = view.stats_title
    val updated: TextView = view.stats_updated
    val cases: TextView = view.stats_cases
    val icon: ImageView = view.stats_icon
}

class StatsRecyclerViewAdapter(private val statList: List<Stats>): RecyclerView.Adapter<StatsViewHolder>() {

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

        if(statList.isEmpty()) {
            title.text = ""
            updated.text = ""
            cases.text = ""
            icon.setImageResource(R.drawable.placeholder)
        } else {
            val statItem = statList[position]
            title.text = statItem.title
            updated.text = statItem.updated
            cases.text = statItem.cases.toString()

            Picasso.get().load("https://www.worldometers.info/img/flags/ch-flag.gif")
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .resize(140, 140)
                .into(icon)
        }
    }
}