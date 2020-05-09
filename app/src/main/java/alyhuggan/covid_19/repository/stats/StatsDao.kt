package alyhuggan.covid_19.repository.stats

import alyhuggan.covid_19.repository.stats.Stats
import androidx.lifecycle.LiveData

interface StatsDao {

    fun getStats(): LiveData<List<Stats>>
    fun getCountryStats(): LiveData<List<Stats>>
    fun getIndividualStat(): LiveData<CountryStat>

}