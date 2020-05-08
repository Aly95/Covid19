package alyhuggan.covid_19.ui

import alyhuggan.covid_19.R
import android.graphics.Color
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    open fun activateToolbar() {
        val toolbar = activity!!.findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
    }
}