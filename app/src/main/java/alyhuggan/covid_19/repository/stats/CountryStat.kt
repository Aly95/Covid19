package alyhuggan.covid_19.repository.stats

data class CountryStat(val title: String, val totalCases: String, var currentCases: String,
                       var recovered: String, var deaths: String, var icon: String?) {

    override fun toString(): String {
        return "$title - $totalCases - $currentCases - $recovered - $deaths - $icon"
    }
}