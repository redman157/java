package company.ai.musicplayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import company.ai.musicplayer.utils.Preferences
import company.ai.musicplayer.utils.ThemeHelper

val mPreferences: Preferences by lazy {
    MusicApplication.prefs
}

class MusicApplication: Application(){
    companion object {
        lateinit var prefs: Preferences
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Preferences(applicationContext)
        AppCompatDelegate.setDefaultNightMode(ThemeHelper.getDefaultNightMode(applicationContext))
    }
}