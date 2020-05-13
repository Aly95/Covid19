package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import alyhuggan.covid_19.ui.covidmap.CovidMapFragment
import alyhuggan.covid_19.ui.regionalstats.RegionStatsFragment
import alyhuggan.covid_19.ui.totalstats.TotalStatsFragment
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream


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

        if (instance == null) {
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

        maintoolbar_share.setOnClickListener {

//            val bitMap2 = screenShot(findViewById(R.id.map_view))
//
//            val callback = GoogleMap.SnapshotReadyCallback() {
//                Log.d(TAG, "snapshotReady")
//                Log.d(TAG, "check ${it.height}")
//            }
//
            val bitmap = screenShot(it.rootView)
            val file = saveBitmap(bitmap, "screenshot.png")
            val apkURI = FileProvider.getUriForFile(this,
            this.applicationContext.packageName + ".provider", file)

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, apkURI)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "share via"))
        }
    }

    private fun saveBitmap(bitmap: Bitmap, fileName: String): File {
        val path: String = Environment.getExternalStorageDirectory().absolutePath
        val dir = File(path)
        if(!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, fileName)
        try {
            var fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut)
            fOut.flush()
            fOut.close()
        } catch(e: Exception) {
            Log.d(TAG, "error: ${e.message}")
        }
        return file
    }

    private fun screenShot(view: View): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

    /*
    Function to swap fragments with passed in fragment instance
     */
    private fun changeFragment(fragment: Fragment, name: String) {

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        if (manager.findFragmentByTag(name) != null) {
            Log.d(TAG, "Cool stuff, fragment: $name is already on the backstack")
            transaction.replace(R.id.main_frame, fragment, name)
        } else {
            Log.d(TAG, "Oh, it seems fragment: $name is not on the backstack")
            transaction.replace(R.id.main_frame, fragment, name)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
        }
        transaction.commit()
    }
}

class FileProvider: FileProvider() {}