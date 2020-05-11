package alyhuggan.covid_19.repository.stats

import androidx.lifecycle.LiveData

interface StatsDao {

    fun getStats(): LiveData<List<Stats>>
    fun getCountryStats(): LiveData<List<CountryStats>>

}