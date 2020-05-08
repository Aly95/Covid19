package alyhuggan.covid_19.viewmodel.totalstats

import alyhuggan.covid_19.repository.StatsDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TotalStatsViewModelFactory(private val statsDao: StatsDao)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TotalStatsViewModel(statsDao) as T
    }
}