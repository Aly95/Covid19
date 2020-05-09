package alyhuggan.covid_19.repository.database

import alyhuggan.covid_19.repository.stats.StatsDao
import alyhuggan.covid_19.repository.stats.StatsDaoImpl

class DatabaseImpl: Database {
    override val statsDao: StatsDao =
        StatsDaoImpl()
}