package company.ai.musicplayer.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.getRecyclerView
import company.ai.musicplayer.MusicViewModel
import company.ai.musicplayer.R
import company.ai.musicplayer.adapters.AccentsAdapter
import company.ai.musicplayer.controller.UIControlInterface
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.utils.ThemeHelper


class PreferencesFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

    private lateinit var mAccentsDialog: MaterialDialog
    private lateinit var mActiveFragmentsDialog: MaterialDialog
    private lateinit var mFiltersDialog: MaterialDialog
    private lateinit var mUIControlInterface: UIControlInterface

    private var mThemePreference: Preference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) = setPreferencesFromResource(
        R.xml.preferences,
        rootKey
    )

    override fun onAttach(context: Context){
        super.onAttach(context)
        try {
            mUIControlInterface = activity as UIControlInterface
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause(){
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        if (::mAccentsDialog.isInitialized && mAccentsDialog.isShowing){
            mAccentsDialog.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewModelProvider(requireActivity()).get(MusicViewModel::class.java).apply {
            mDeviceMusic.observe(viewLifecycleOwner, { returnedMusic ->
                if (!returnedMusic.isNullOrEmpty()) {
                    findPreference<Preference>(getString(R.string.found_songs_pref))?.apply {
                        title = getString(R.string.found_songs_pref_title, mDatabaseSize)
                    }
                }
            })
        }
        mThemePreference = findPreference<Preference>(getString(R.string.theme_pref))?.apply {
            icon = AppCompatResources.getDrawable(
                requireActivity(),
                ThemeHelper.resolveThemeIcon(requireActivity())
            )
        }

        findPreference<Preference>(getString(R.string.accent_pref))?.apply {
            summary =  ThemeHelper.getAccentName(mPreferences.accent, requireContext())
            onPreferenceClickListener = this@PreferencesFragment
        }
    }

    private fun showAccentsDialog(){
        mAccentsDialog = MaterialDialog(requireActivity()).show {
            title(R.string.accent_pref_title)
            customListAdapter(AccentsAdapter(requireActivity()))
            getRecyclerView().apply {
                layoutManager = GridLayoutManager(requireActivity(), 3)

            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PreferencesFragment()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d("CCC","${javaClass.simpleName} --- onSharedPreferenceChanged: $key")
        when(key){
            getString(R.string.accent_pref) -> {
                mAccentsDialog.dismiss()
                mUIControlInterface.onAppearanceChanged(
                    isAccentChanged = true,
                    restoreSettings = true
                )
            }
        }
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        Log.d("CCC","${javaClass.simpleName} --- onPreferenceClick: ${preference?.key}")
        when(preference?.key){
            getString(R.string.accent_pref) -> {
                showAccentsDialog()
            }
        }
        return false

    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        Log.d("CCC","${javaClass.simpleName} --- onPreferenceChange: ${preference?.key}")
        when(preference?.key){

            getString(R.string.accent_pref) -> {
                mAccentsDialog.dismiss()
                mUIControlInterface.onAppearanceChanged(
                    isAccentChanged = true,
                    restoreSettings = true
                )
            }
        }
        return false
    }
}