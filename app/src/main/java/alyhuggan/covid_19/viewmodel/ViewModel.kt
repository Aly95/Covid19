package alyhuggan.covid_19.viewmodel

import alyhuggan.covid_19.repository.stats.Coordinate
import alyhuggan.covid_19.repository.stats.CountryStats
import alyhuggan.covid_19.repository.stats.StatsDao
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "ViewModel"

class ViewModel(private val statsDao: StatsDao)
    : ViewModel() {

    private val coordinates = MutableLiveData<List<Coordinate>>()
    private val coordinateList = mutableListOf<Coordinate>()
    private var countryStats = ArrayList<CountryStats>()

    init {
        coordinates.value = coordinateList
    }

    fun getStats() = statsDao.getStats()
    fun getCountryStats() = statsDao.getCountryStats()
    fun retrieveCoordinates() = coordinates

    fun getCoordinates(context: Context) {

        countryStats = getCountryStats().value as ArrayList<CountryStats>

        val coordinatesList = mutableListOf<Coordinate>()

        val geocoder: Geocoder = Geocoder(context)

        if (!countryStats.isNullOrEmpty()) {
            for (i in countryStats.indices) {
                val geoDetails = geocoder.getFromLocationName(countryStats[i].title, 1)
                geoDetails.forEach {
                    coordinatesList.add(Coordinate(countryStats[i].title, it.latitude, it.longitude))
                }
            }
            coordinates.postValue(coordinatesList)
        }
    }
}