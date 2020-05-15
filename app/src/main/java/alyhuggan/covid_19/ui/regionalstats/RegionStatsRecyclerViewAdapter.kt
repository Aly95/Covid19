package alyhuggan.covid_19.ui.regionalstats

import alyhuggan.covid_19.R
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.ui.bottomsheet.BottomSheetFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_country_stats.view.*

private const val TAG = "RegionStatRecyclerVA"

/*
Class for holding the fields
 */
class RegionStatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.countrystats_title
    val updated: TextView = view.countrystats_updated
    val cases: TextView = view.countrystats_cases
    val icon: ImageView = view.countrystats_icon
}

class RegionStatsRecyclerViewAdapter(
    private val statList: List<CountryStats>,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<RegionStatsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionStatsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_country_stats, parent, false)
        return RegionStatsViewHolder(view)
    }

    override fun getItemCount() = statList.size

    override fun onBindViewHolder(holder: RegionStatsViewHolder, position: Int) {

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
            //Retrieving each country stat object and updating the fields accordingly
            val statItem = statList[position]
            val context = holder.title.context

            title.text = statItem.title
            updated.text = "Last Updated: ${statItem.updated}"
            cases.text = statItem.totalCases

            cases.setTextColor(ContextCompat.getColor(context!!, R.color.colorBlue))

            Picasso.get().load(statItem.icon)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .resize(140, 140)
                .into(icon)

            //Setting an onClickListener to each view and passing the country name to BottomSheet if clicked
            holder.itemView.setOnClickListener() {
                val bottomSheetFragment = BottomSheetFragment()
                val args = Bundle()
                args.putString("COUNTRY", holder.title.text.toString())
                bottomSheetFragment.arguments = args
                bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
            }
        }
    }
}




