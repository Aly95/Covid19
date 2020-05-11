package alyhuggan.covid_19.repository.stats

data class CountryStats(val title: String, val totalCases: String, var currentCases: String,
                        var recovered: String, var deaths: String, var icon: String?, var updated: String) {

    //constructor used so CountryStat can be initialized without parameters in StatsDaoImpl()
    constructor(): this("", "", "", "", "", "", "")

    override fun toString(): String {
        return "$title - $totalCases - $currentCases - $recovered - $deaths - $icon - $updated"
    }
}