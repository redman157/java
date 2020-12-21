package company.ai.musicplayer.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
class PreferencesFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        TODO("Not yet implemented")
    }
    companion object {
        @JvmStatic
        fun newInstance() = PreferencesFragment()
    }
}