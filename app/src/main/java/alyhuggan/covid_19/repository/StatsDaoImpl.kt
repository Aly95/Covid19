package alyhuggan.covid_19.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "StatsDaoImpl"

class StatsDaoImpl: StatsDao {

    private val statList = mutableListOf<Stats>()
    private val stats = MutableLiveData<List<Stats>>()
    private val countryStats = MutableLiveData<Stats>()

    init {
        statList.add(Stats("Total Confirmed Cases", "Last Updated: May, 07 2020, 12:27, UTC", 10000, "Icon =:"))
        statList.add(Stats("Currently Infected", "Last Updated: May, 07 2020, 12:27, UTC", 8000, "Icon =:"))
        statList.add(Stats("Recovered", "Last Updated: May, 07 2020, 12:27, UTC", 2000, "Icon =:"))
        statList.add(Stats("Deaths", "Last Updated: May, 07 2020, 12:27, UTC", 500, "Icon =:"))
        stats.value = statList
    }

    override fun getStats(): LiveData<List<Stats>> = stats

    override fun getCountryStats(): LiveData<Stats> = countryStats

}