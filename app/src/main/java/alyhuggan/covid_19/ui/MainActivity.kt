package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import alyhuggan.covid_19.ui.covidmap.CovidMapFragment
import alyhuggan.covid_19.ui.regionalstats.RegionStatsFragment
import alyhuggan.covid_19.ui.totalstats.TotalStatsFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.kodein.di.android.retainedKodein

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeUi(savedInstanceState)
    }

    private fun initializeUi(instance: Bundle?) {

        if(instance == null) {
            changeFragment(TotalStatsFragment(), "total")
        }

        /*
        Retrieving bottom nav bar, setting onClickListener to listen for click events and swapping out fragments accordingly
         */
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_totalstats -> {
                    changeFragment(TotalStatsFragment(), "total")
                }
                R.id.menu_regionstats -> {
                    changeFragment(RegionStatsFragment(), "region")
                }
                R.id.menu_covidmap -> {
                    changeFragment(CovidMapFragment(), "map")
                }
            }
            true
        }
    }

    /*
    Function to swap fragments with passed in fragment instance
     */
    private fun changeFragment(fragment: Fragment, name: String) {

        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frame, fragment, name)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (supportFragmentManager.findFragmentByTag(name) == null) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}
