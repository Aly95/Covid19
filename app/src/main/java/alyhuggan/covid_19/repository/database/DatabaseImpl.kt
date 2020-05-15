package alyhuggan.covid_19.repository.database

import alyhuggan.covid_19.repository.stats.StatsDao
import alyhuggan.covid_19.repository.stats.StatsDaoImplementation

/*
Database implementation class, binded as a singleton for Kodein
 */
class DatabaseImpl: Database {
    override val statsDao: StatsDao =
        StatsDaoImplementation()
}