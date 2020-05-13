package alyhuggan.covid_19.viewmodel

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

    private val coordinates = MutableLiveData<List<Pair<String, Pair<Double, Double>>>>()
    private val coordinateList = mutableListOf<Pair<String, Pair<Double, Double>>>()

    init {
        coordinates.value = coordinateList
    }

    fun getStats() = statsDao.getStats()
    fun getCountryStats() = statsDao.getCountryStats()
    fun retrieveCoordinates() = coordinates

    fun getCoordinates(countryList: List<CountryStats>, context: Context) {

        val coordinatesList = mutableListOf<Pair<String, Pair<Double, Double>>>()

        val geocoder: Geocoder = Geocoder(context)

        if (!countryList.isNullOrEmpty()) {
            coordinatesList.clear()
            for (i in countryList.indices) {
                val geoDetails = geocoder.getFromLocationName(countryList[i].title, 1)
                geoDetails.forEach {
                    coordinatesList.add(Pair(countryList[i].title, Pair(it.latitude, it.longitude)))
                    Log.d(TAG, "Coordinate list = $coordinatesList")
                }
            }
            coordinates.postValue(coordinatesList)
        }
    }

}