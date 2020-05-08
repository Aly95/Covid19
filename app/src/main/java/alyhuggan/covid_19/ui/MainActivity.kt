package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import alyhuggan.covid_19.ui.covidmap.CovidMapFragment
import alyhuggan.covid_19.ui.regionalstats.RegionStatsFragment
import alyhuggan.covid_19.ui.totalstats.TotalStatsFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        this.supportActionBar?.hide()
        initializeUi()
    }

    private fun initializeUi() {

        changeFragment(TotalStatsFragment()) //setting initial fragment to TotalStats

        /*
        Retrieving bottom nav bar, setting onClickListener to listen for click events and swapping out fragments accordingly
         */
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_totalstats -> {
                    changeFragment(TotalStatsFragment())
                }
                R.id.menu_regionstats -> {
                    changeFragment(RegionStatsFragment())
                }
                R.id.menu_covidmap -> {
                    changeFragment(CovidMapFragment())
                }
            }
            true
        }
    }

    /*
    Function to swap fragments with passed in fragment instance
     */
    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_frame, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .addToBackStack(null)
            .commit()
    }
}
