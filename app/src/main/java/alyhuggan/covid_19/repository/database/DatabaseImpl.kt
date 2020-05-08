package alyhuggan.covid_19.repository.database

import alyhuggan.covid_19.repository.StatsDao
import alyhuggan.covid_19.repository.StatsDaoImpl

class DatabaseImpl: Database {
    override val statsDao: StatsDao =
        StatsDaoImpl()
}