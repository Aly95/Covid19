package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import alyhuggan.covid_19.ui.covidmap.CovidMapFragment
import alyhuggan.covid_19.ui.regionalstats.RegionStatsFragment
import alyhuggan.covid_19.ui.totalstats.TotalStatsFragment
import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

/*
Application requires read and write permissions for screenshot functionality
 */
private const val REQUEST_EXTERNAL_STORAGE = 1
private val PERMISSIONS_STORAGE = arrayOf<String>(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeUi(savedInstanceState)
    }

    private fun initializeUi(instance: Bundle?) {

        //Opens Total Fragment if there is not saved instance
        if (instance == null) {
            changeFragment(TotalStatsFragment(), "total")
        }

//        Retrieving bottom nav bar, setting onClickListener to listen for click events and swapping out fragments accordingly
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
    Swaps out fragment with passed in fragment
     */
    private fun changeFragment(fragment: Fragment, name: String) {

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        val checkFragment: Fragment? = manager.findFragmentByTag(name)

        //Checks if the fragment already exists, swaps it out if so. If not fragment is added to backstack
        if(checkFragment != null) {
            transaction.replace(R.id.main_frame, fragment, name)
                .commit()
        } else {
            transaction.replace(R.id.main_frame, fragment, name)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit()
        }
    }
}

//Class needed for screenshot functionality, see Android Manifest for provider details
class FileProvider : FileProvider() {}