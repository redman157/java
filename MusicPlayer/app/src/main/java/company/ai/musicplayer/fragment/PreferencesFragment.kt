package company.ai.musicplayer.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import company.ai.musicplayer.controller.UIControlInterface

class PreferencesFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private lateinit var mAccentsDialog: MaterialDialog
    private lateinit var mActiveFragmentsDialog: MaterialDialog
    private lateinit var mFiltersDialog: MaterialDialog
    private lateinit var mUIControlInterface: UIControlInterface

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

    }

    override fun onAttach(context: Context){
        super.onAttach(context)
        try {
            mUIControlInterface = activity as UIControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        @JvmStatic
        fun newInstance() = PreferencesFragment()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return true

    }
}