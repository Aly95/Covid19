package alyhuggan.covid_19.repository.stats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

private const val TAG = "StatsDaoImpl"

class StatsDaoImpl : StatsDao {

    //LiveData variables
    private val stats = MutableLiveData<List<Stats>>()
    private val countryStats = MutableLiveData<List<Stats>>()
    private val individualStat = MutableLiveData<List<BottomSheetStats>>()
    //Mutable variables the LiveData variables have their values set to
    private val statList = mutableListOf<Stats>()
    private val countryStatList = mutableListOf<Stats>()
    private var individualCountryStat = mutableListOf<BottomSheetStats>()

    init {
        stats.value = statList
        countryStats.value = countryStatList
        individualStat.value = individualCountryStat
        getJsonData("https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats", null)
        getJsonData("https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search", null)
    }

    override fun getStats() = stats as LiveData<List<Stats>>
    override fun getCountryStats() = countryStats as LiveData<List<Stats>>
    override fun getIndividualStat(country: String): LiveData<List<BottomSheetStats>> {
        getJsonData("https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search ", country)
        return individualStat
    }

//    private fun retrieveCountryName(url: String) = url.substringAfter(" ")

    /*
    Retrieves JSON data from given URL and passes it to the respective function to be be parsed
     */
    private fun getJsonData(url: String, countryName: String?) {

        val general = "https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats"
        val country = "https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search"

        val request = Request.Builder().url(url.trim()).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                when (url) {
                    general -> {
                        parseGeneralJsonData(body)
                    }
                    country -> {
                        parseCountryJsonData(body)
                    }
                    else -> {
                        parseIndividualJsonData(body, countryName!!)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: Failed, exception $e")
            }
        })
    }

    private fun parseGeneralJsonData(body: String?) {

            var titleArray = mutableListOf<Pair<String, String>>()
            val total = Pair("Total Confirmed Cases", "total_cases")
            val current = Pair("Currently Infected", "currently_infected")
            val recovered = Pair("Recovered", "recovery_cases")
            val deaths = Pair("Deaths", "death_cases")
            titleArray.add(total)
            titleArray.add(current)
            titleArray.add(recovered)
            titleArray.add(deaths)

            val jsonData = JSONObject(body)
            val data = jsonData.getJSONObject("data")
            val updated = data.getString("last_update")

            for (i in 0 until titleArray.size) {

                val stat = titleArray[i]

                statList.add(
                    Stats(
                        stat.first,
                        updated,
                        data.getString(stat.second),
                        null
                    )
                )
                Log.d(TAG, "${stats.value}")
            }
            stats.postValue(statList)
    }

    private fun parseCountryJsonData(body: String?) {

        val jsonData = JSONObject(body)
        val data = jsonData.getJSONObject("data")
        val countryObject = data.getJSONArray("rows")
        val updated = data.getString("last_update")

        for(i in 1 until countryObject.length()) {

            val stat = countryObject.getJSONObject(i)

            countryStatList.add(
                Stats(
                    stat.get("country") as String,
                    updated,
                    stat.get("total_cases") as String,
                    stat.get("flag") as String
                )
            )
        }
        countryStats.postValue(countryStatList)
    }

    private fun parseIndividualJsonData(body: String?, country: String) {

        individualCountryStat.clear()

        val total = Pair("Total Confirmed Cases", "total_cases")
        val current = Pair("Currently Infected", "active_cases")
        val recovered = Pair("Recovered", "total_recovered")
        val deaths = Pair("Deaths", "total_deaths")

        val jsonData = JSONObject(body)
        val data = jsonData.getJSONObject("data")
        val countryObject = data.getJSONArray("rows")

        for (i in 1 until countryObject.length()) {

            val stat = countryObject.getJSONObject(i)

            if (stat.get("country") == country) {
                individualCountryStat.add(BottomSheetStats(total.first, stat.getString(total.second), stat.getString("flag")))
                individualCountryStat.add(BottomSheetStats(current.first, stat.getString(current.second), null))
                individualCountryStat.add(BottomSheetStats(recovered.first, stat.getString(recovered.second), null))
                individualCountryStat.add(BottomSheetStats(deaths.first, stat.getString(deaths.second), null))
            }
            individualStat.postValue(individualCountryStat)
        }
    }
}