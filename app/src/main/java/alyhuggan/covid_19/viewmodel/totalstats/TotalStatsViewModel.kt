package alyhuggan.covid_19.viewmodel.totalstats

import alyhuggan.covid_19.repository.stats.StatsDao
import androidx.lifecycle.ViewModel

class TotalStatsViewModel(private val statsDao: StatsDao)
    : ViewModel() {

    fun getStats() = statsDao.getStats()
    fun getCountryStats() = statsDao.getCountryStats()
    fun getIndividualStat() = statsDao.getIndividualStat()

}