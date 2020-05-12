package alyhuggan.covid_19.viewmodel

import alyhuggan.covid_19.repository.stats.StatsDao
import androidx.lifecycle.ViewModel

class ViewModel(private val statsDao: StatsDao)
    : ViewModel() {

    fun getStats() = statsDao.getStats()
    fun getCountryStats() = statsDao.getCountryStats()
}