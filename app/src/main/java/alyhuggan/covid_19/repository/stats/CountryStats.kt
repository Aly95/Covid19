package alyhuggan.covid_19.repository.stats

/*
CountryStats class, used to store country statistics in the Regional Stat Class and Google Map page
 */
class CountryStats(val title: String, val totalCases: String, var currentCases: String,
                        var recovered: String, var deaths: String, var icon: String?, var updated: String)