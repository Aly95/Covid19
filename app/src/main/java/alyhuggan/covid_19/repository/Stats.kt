package alyhuggan.covid_19.repository

data class Stats(val title: String, val updated: String, val cases: Int, val icon: String) {

    override fun toString(): String {
        return "$title - $updated - $cases - $icon"
    }
}