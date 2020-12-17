package company.ai.musicplayer.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import company.ai.musicplayer.R
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.models.SavedEqualizerSettings
import company.ai.musicplayer.models.SavedMusic
import java.lang.reflect.Type

class Preferences (context: Context){
    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    private val prefsTheme = context.getString(R.string.theme_pref)
    private val prefsThemeDef = context.getString(R.string.theme_pref_light)
    private val prefsAccent = context.getString(R.string.accent_pref)
    private val prefsEdgeToEdge = context.getString(R.string.edge_pref)

    private val prefsArtistsSorting = context.getString(R.string.artists_sorting_pref)
    private val prefsFoldersSorting = context.getString(R.string.folders_sorting_pref)
    private val prefsAlbumsSorting = context.getString(R.string.albums_sorting_pref)
    private val prefsAllMusicSorting = context.getString(R.string.all_music_sorting_pref)

    private val prefsLatestVolume = context.getString(R.string.latest_volume_pref)
    private val prefsLatestPlayedSong = context.getString(R.string.latest_played_song_pref)

    private val prefsActiveFragments = context.getString(R.string.active_fragments_pref)
    val prefsActiveFragmentsDef = setOf(0, 1, 2, 3, 4)

    private val prefsOnListEnded = context.getString(R.string.on_list_ended_pref)
    private val prefsCover = context.getString(R.string.covers_pref)

    private val prefsFastSeek = context.getString(R.string.fast_seeking_pref)
    private val prefsFastSeekActions = context.getString(R.string.fast_seeking_actions_pref)
    private val prefsPreciseVolume = context.getString(R.string.precise_volume_pref)
    private val prefsFocus = context.getString(R.string.focus_pref)
    private val prefsHeadsetPlug = context.getString(R.string.headset_pref)

    private val prefsFilter = context.getString(R.string.filter_pref)

    // active fragments type
    private val typeActiveFragments = object : TypeToken<Set<Int>>() {}.type

    // last played song is a SavedMusic
    private val typeLastPlayedSong = object : TypeToken<SavedMusic>() {}.type

    // saved equalizer settings is a SavedEqualizerSettings
    private val typeSavedEqualizerSettings = object : TypeToken<SavedEqualizerSettings>() {}.type

    private val prefsSavedEqualizerSettings = context.getString(R.string.saved_eq_settings)

    private val prefsDeviceSongs = context.getString(R.string.device_songs_pref)
    //device songs is a list of Music
    private val typeDeviceSongs = object : TypeToken<MutableList<Music>>() {}.type
    var theme
        get() = mPrefs.getString(prefsTheme, prefsThemeDef)
        set(value) = mPrefs.edit().putString(prefsTheme, value).apply()

    var allMusicSorting
        get() = mPrefs.getInt(prefsAllMusicSorting, Constants.DEFAULT_SORTING)
        set(value) = mPrefs.edit().putInt(prefsAllMusicSorting, value).apply()

    var accent
        get() = mPrefs.getInt(prefsAccent, R.color.accentColor)
        set(value) = mPrefs.edit().putInt(prefsAccent, value).apply()

    var activeFragments: Set<Int>
        get() = getObject(prefsActiveFragments, typeActiveFragments) ?: prefsActiveFragmentsDef
        set(value) = putObject(prefsActiveFragments, value)

    var isFocusEnabled
        get() = mPrefs.getBoolean(prefsFocus, true)
        set(value) = mPrefs.edit().putBoolean(prefsFocus, value).apply()

    var latestVolume: Int
        get() = mPrefs.getInt(prefsLatestVolume, 100)
        set(value) = mPrefs.edit().putInt(prefsLatestVolume, value).apply()

    var latestPlayedSong: SavedMusic?
        get() = getObject(
            prefsLatestPlayedSong,
            typeLastPlayedSong
        )
        set(value) = putObject(prefsLatestPlayedSong, value)

    var isFastSeekingActions: Boolean
        get() = mPrefs.getBoolean(prefsFastSeekActions, false)
        set(value) = mPrefs.edit().putBoolean(prefsFastSeekActions, value).apply()

    var isCovers: Boolean
        get() = mPrefs.getBoolean(prefsCover, false)
        set(value) = mPrefs.edit().putBoolean(prefsCover, value).apply()

    var onListEnded
        get() = mPrefs.getString(prefsOnListEnded, Constants.CONTINUE)
        set(value) = mPrefs.edit().putString(prefsOnListEnded, value).apply()

    var savedEqualizerSettings: SavedEqualizerSettings?
        get() = getObject(
            prefsSavedEqualizerSettings,
            typeSavedEqualizerSettings
        )
        set(value) = putObject(prefsSavedEqualizerSettings, value)

    var fastSeekingStep
        get() = mPrefs.getInt(prefsFastSeek, 5)
        set(value) = mPrefs.edit().putInt(prefsFastSeek, value).apply()

    var isPreciseVolumeEnabled
        get() = mPrefs.getBoolean(prefsPreciseVolume, true)
        set(value) = mPrefs.edit().putBoolean(prefsPreciseVolume, value).apply()

    var isHeadsetPlugEnabled
        get() = mPrefs.getBoolean(prefsHeadsetPlug, true)
        set(value) = mPrefs.edit().putBoolean(prefsHeadsetPlug, value).apply()

    var artistsSorting
        get() = mPrefs.getInt(prefsArtistsSorting, Constants.DESCENDING_SORTING)
        set(value) = mPrefs.edit().putInt(prefsArtistsSorting, value).apply()

    var foldersSorting
        get() = mPrefs.getInt(prefsFoldersSorting, Constants.DEFAULT_SORTING)
        set(value) = mPrefs.edit().putInt(prefsFoldersSorting, value).apply()

    var albumsSorting
        get() = mPrefs.getInt(prefsAlbumsSorting, Constants.DEFAULT_SORTING)
        set(value) = mPrefs.edit().putInt(prefsAlbumsSorting, value).apply()

    var deviceSongs: MutableList<Music>?
        get() = getObject(
            prefsDeviceSongs,
            typeDeviceSongs
        )
        set(value) = putObject(prefsDeviceSongs, value)

    private val mGson = GsonBuilder().create()
    /**
     * Saves object into the Preferences.
     * Only the fields are stored. Methods, Inner classes, Nested classes and inner interfaces are not stored.
     **/
    private fun <T> putObject(key: String, y: T) {
        //Convert object to JSON String.
        val inString = mGson.toJson(y)
        //Save that String in SharedPreferences
        mPrefs.edit().putString(key, inString).apply()
    }

    /**
     * Get object from the Preferences.
     **/
    private fun <T> getObject(key: String, t: Type): T? {
        //We read JSON String which was saved.
        val value = mPrefs.getString(key, null)

        //JSON String was found which means object can be read.
        //We convert this JSON String to model object. Parameter "c" (of type Class<T>" is used to cast.
        return mGson.fromJson(value, t)
    }
}