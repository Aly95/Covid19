package alyhuggan.covid_19.repository

import androidx.lifecycle.LiveData

interface StatsDao {

    fun getStats(): LiveData<List<Stats>>
    fun getCountryStats(): LiveData<Stats>

}