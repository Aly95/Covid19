package alyhuggan.covid_19.repository.stats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

private const val TAG = "StatsDaoImpl"

class StatsDaoImpl : StatsDao {

    private val statList = mutableListOf<Stats>()
    private val countryStatList = mutableListOf<Stats>()
    private val stats = MutableLiveData<List<Stats>>()
    private val countryStats = MutableLiveData<List<Stats>>()
    private val individualStat = MutableLiveData<CountryStat>()

    init {
        stats.value = statList
        countryStats.value = countryStatList
        getJsonData("https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats")
        getJsonData("https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search")
    }

    override fun getStats() = stats as LiveData<List<Stats>>
    override fun getCountryStats() = countryStats as LiveData<List<Stats>>
    override fun getIndividualStat() = individualStat as LiveData<CountryStat>

    /*
    Retrieves JSON data from given URL and passes it to the respective function to be be parsed
     */
    private fun getJsonData(url: String) {

        val general = "https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats"
        val country = "https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search"
        val request = Request.Builder().url(url).build()
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
    }

    private fun parseIndividualJsonData(body: String?, country: String) {

        val jsonData = JSONObject(body)
        val data = jsonData.getJSONObject("data")
        val countryObject = data.getJSONArray("rows")

        for (i in 1 until countryObject.length()) {

            val stat = countryObject.getJSONObject(i)

            if (stat.get("country") == country) {
                Log.d(TAG, "Individual Country here")
                individualStat.value =
                    CountryStat(
                        stat.get("country") as String,
                        stat.get("total_cases") as String,
                        stat.get("active_cases") as String,
                        stat.get("total_recovered") as String,
                        stat.get("total_deaths") as String,
                        stat.get("flag") as String
                    )
            }
        }
    }
}