package alyhuggan.covid_19.repository.stats

data class Stats(val title: String, val updated: String, val cases: String, var icon: String?) {

    override fun toString(): String {
        return "$title - $updated - $cases - $icon"
    }
}