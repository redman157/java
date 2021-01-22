package company.ai.musicplayer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import company.ai.musicplayer.database.AppDatabase
//import company.ai.musicplayer.database.AppDatabase
import company.ai.musicplayer.utils.Preferences
import company.ai.musicplayer.utils.ThemeHelper

val mPreferences: Preferences by lazy {
    MusicApplication.prefs
}

val mDatabase: AppDatabase by lazy {
    MusicApplication.database
}

class MusicApplication: Application(){
    companion object {
        lateinit var database: AppDatabase
        lateinit var prefs: Preferences
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Preferences(applicationContext)
        database = AppDatabase.getInstance(this)
        AppCompatDelegate.setDefaultNightMode(ThemeHelper.getDefaultNightMode(applicationContext))
    }
}