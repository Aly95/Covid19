package alyhuggan.covid_19.repository.stats

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

private const val TAG = "StatsDaoImplementation"

class StatsDaoImplementation : StatsDao, Fragment() {

    //LiveData variables
    private val stats = MutableLiveData<List<Stat>>()
    private val countryStats = MutableLiveData<List<CountryStats>>()

    //Mutable variables the LiveData variables have their values set to
    private val statList = mutableListOf<Stat>()
    private val countryStatList = mutableListOf<CountryStats>()

    /*
    Initialising stats and countryStats values to match statList and countryStatList respectively
    Retrieves the JsonData so it is readily available as soon as possible
     */
    init {
        stats.value = statList
        countryStats.value = countryStatList
        getJsonData("https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats")
        getJsonData("https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search")
    }

    /*
    Specifying the return values for the LiveData functions
     */
    override fun getStats() = stats as LiveData<List<Stat>>
    override fun getCountryStats() = countryStats as LiveData<List<CountryStats>>

    /*
    Retrieves JSON data from the given URL and passes it to the respective function to be be parsed
    */
    private fun getJsonData(url: String) {

        val general = "https://corona-virus-stats.herokuapp.com/api/v1/cases/general-stats"
        val country = "https://corona-virus-stats.herokuapp.com/api/v1/cases/countries-search"

        val request = Request.Builder().url(url.trim()).build()
        val client = OkHttpClient()

        //Using OkHttp to fetch JSON data and comparing the passed URL to the constants defined at the top to pass it to the right function
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

    /*
    Function to convert the JSON data to a list of Stat objects
     */
    private fun parseGeneralJsonData(body: String?) {

        var titleArray = mutableListOf<Pair<String, String>>()
        val total = Pair("Confirmed Cases", "total_cases")
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
            statList.add(Stat(stat.first, updated, data.getString(stat.second), null))
        }
        //stats wasn't being updated without posting value
        stats.postValue(statList)
    }

    /*
    Function to convert the JSON data to a list of CountryStat objects
     */
    private fun parseCountryJsonData(body: String?) {

        val jsonData = JSONObject(body)
        val data = jsonData.getJSONObject("data")
        val countryObject = data.getJSONArray("rows")
        val updated = data.getString("last_update")

        for (i in 1 until countryObject.length()) {

            val stat = countryObject.getJSONObject(i)

            countryStatList.add(
                CountryStats(
                    stat.get("country") as String,
                    stat.get("total_cases") as String,
                    stat.get("active_cases") as String,
                    stat.get("total_recovered") as String,
                    stat.get("total_deaths") as String,
                    stat.get("flag") as String,
                    updated))
        }
        //countryStats wasn't being updated without posting value
        countryStats.postValue(countryStatList)
    }

}

