package alyhuggan.covid_19

import alyhuggan.covid_19.repository.StatsDao
import alyhuggan.covid_19.repository.database.Database
import alyhuggan.covid_19.repository.database.DatabaseImpl
import alyhuggan.covid_19.viewmodel.totalstats.TotalStatsViewModelFactory
import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class KodeinInjection: Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        bind<Database>() with singleton { DatabaseImpl() }
        bind<StatsDao>() with singleton { instance<Database>().statsDao }
        bind() from provider { TotalStatsViewModelFactory(instance()) }
        }
    }