package alyhuggan.covid_19.repository

import alyhuggan.covid_19.repository.stats.StatsDao
import alyhuggan.covid_19.repository.database.Database
import alyhuggan.covid_19.repository.database.DatabaseImpl
import alyhuggan.covid_19.viewmodel.ViewModelFactory
import android.app.Application
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class KodeinInjection : Application(), KodeinAware {

    /*
    Provides Kodein with the bindings in order to allow dependency injection
    */
    override val kodein = Kodein.lazy {
        bind<Database>() with singleton { DatabaseImpl() }
        bind<StatsDao>() with singleton { instance<Database>().statsDao }
        bind() from provider {
            ViewModelFactory(
                instance()
            )
        }
    }
}