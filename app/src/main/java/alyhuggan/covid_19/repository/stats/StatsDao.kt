package alyhuggan.covid_19.repository.stats

import androidx.lifecycle.LiveData

interface StatsDao {

    /*
    Interface for functions used in the StatsDaoImplementation (Stat Data Object Implementation) class
     */
    fun getStats(): LiveData<List<Stat>>
    fun getCountryStats(): LiveData<List<CountryStats>>
}