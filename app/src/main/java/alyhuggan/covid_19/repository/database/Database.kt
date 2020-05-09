package alyhuggan.covid_19.repository.database

import alyhuggan.covid_19.repository.stats.StatsDao

interface Database {
    val statsDao: StatsDao
}