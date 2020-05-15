package alyhuggan.covid_19.repository.database

import alyhuggan.covid_19.repository.stats.StatsDao

/*
Interface for Database Implementation class
 */
interface Database {
    val statsDao: StatsDao
}