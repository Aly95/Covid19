package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream

open class BaseFragment: Fragment() {

    fun setUpScreenshot() {
        activity!!.findViewById<ImageView>(R.id.maintoolbar_share).setOnClickListener {
            val bitmap = screenShot(it.rootView)
            val file = saveBitmap(bitmap, "screenshot.png")
            val apkURI = FileProvider.getUriForFile(
                context!!,
                context!!.applicationContext.packageName + ".provider", file
            )

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, apkURI)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "share via"))
        }
    }

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

    private fun screenShot(view: View): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

}