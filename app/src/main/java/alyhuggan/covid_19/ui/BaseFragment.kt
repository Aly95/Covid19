package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream

open class BaseFragment : Fragment() {

    /*
    Function called by TotalStats and RegionStatss when they are created
     */
    fun setUpScreenshot() {
        //Sets up the sharebuttons onClickListener
        activity!!.findViewById<ImageView>(R.id.maintoolbar_share).setOnClickListener {
            //Calls the screenshot function, passing it the rootView
            val bitmap = screenshot(it.rootView)
            val file = saveBitmap(bitmap, "screenshot.png")
            //Retrieves URI as API24 and above will not allow access on file:// path
            val apkURI = FileProvider.getUriForFile(
                context!!,
                context!!.applicationContext.packageName + ".provider",
                file
            )

            //Creates a share intent, adds in the image and starts the intent
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, apkURI)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "share via"))
        }
    }

    /*
    Saves the passed in bitmap image so it is ready for sharing
     */
    fun saveBitmap(bitmap: Bitmap, fileName: String): File {
        val path: String = Environment.getExternalStorageDirectory().absolutePath
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, fileName)
        try {
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
        }
        return file
    }

    /*
    Takes a screenshot of the passed in views width and height
    Function did not work for Google Map View, shows up blank so had to use alternative means
    */
    private fun screenshot(view: View): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}