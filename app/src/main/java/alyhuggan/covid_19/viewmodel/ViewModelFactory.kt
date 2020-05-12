package alyhuggan.covid_19.viewmodel

import alyhuggan.covid_19.repository.stats.StatsDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val statsDao: StatsDao)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModel(statsDao) as T
    }
}