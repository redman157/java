package company.ai.musicplayer.utils

import android.util.Log
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.models.Music
import java.util.*
import kotlin.Comparator

object ListsHelper {
    var recentlyMusic: MutableList<Music> = mutableListOf()
    @JvmStatic
    fun getSortedMusicList(
        id: Int,
        list: MutableList<Music>?
    ) = when (id) {
        Constants.DESCENDING_SORTING -> {
            list?.sortBy { it.title }
            list
        }

        Constants.ASCENDING_SORTING -> {
            list?.sortBy { it.title }
            list?.asReversed()
        }

        Constants.TRACK_SORTING -> {
            list?.sortBy { it.track }
            list
        }

        Constants.TRACK_SORTING_INVERTED -> {
            list?.sortBy { it.track }
            list?.asReversed()
        }
        else -> list
    }

    @JvmStatic
    fun getSortedList(id: Int , list: MutableList<String>?
    ) = when (id){
        Constants.DESCENDING_SORTING -> {
            list?.apply {
                Collections.sort(this, String.CASE_INSENSITIVE_ORDER)
            }
            list
        }

        Constants.ASCENDING_SORTING -> {
            list?.apply {
                Collections.sort(this, String.CASE_INSENSITIVE_ORDER)
            }
            list?.asReversed()
        }
        else -> list
    }

    @JvmStatic
    fun getSortedListWithNull(
        id: Int,
        list: MutableList<String?>?
    ): MutableList<String>? {
        val withoutNulls = list?.map {
            transformNullToEmpty(it)
        }?.toMutableList()

        return getSortedList(id, withoutNulls)
    }

    private fun transformNullToEmpty(toTrans: String?): String {
        if (toTrans == null) {
            return ""
        }
        return toTrans
    }


    fun getRecentlyMusicAdd(allMusics: MutableList<Music>): MutableList<Music>{

        return if (mPreferences.deviceSongs != null){
            Log.d("XXX","enter if")
            Log.d("XXX","deviceSongs: ${mPreferences.deviceSongs!!.size}")
            Log.d("XXX", "allMusics: ${allMusics.size}")
            //https://www.techiedelight.com/difference-between-two-lists-kotlin/
            val deviceMusic = mPreferences.deviceSongs!!
            deviceMusic.sortBy { it.displayName }
            allMusics.sortBy { it.displayName }
            val difference = deviceMusic.filterNot { allMusics.contains(it) }
            Log.d("XXX","${difference.size}")
            recentlyMusic = difference.toMutableList()
          /*  if (deviceMusic.size == allMusics.size) {
                val difference = deviceMusic.filterNot { allMusics.contains(it) }
                Log.d("XXX","${difference.size}")
                recentlyMusic = difference.toMutableList()
            }else{
                if (deviceMusic.size < allMusics.size){
                    val a = HashSet<Music>(deviceMusic).containsAll(allMusics)
                }else{

                }
            }*/

            Log.d("XXX", "recentlyMusic: ${recentlyMusic.size}")
            return recentlyMusic
        }else{
            Log.d("XXX","enter else")
            mPreferences.deviceSongs = allMusics
            allMusics
        }
    }



    fun<T> isEqual(first: MutableList<T>, second: MutableList<T>): Boolean {

        if (first.size != second.size) {
            return false
        }

        first.forEachIndexed {
                index, value -> if (second[index] != value) {
                    return false
                }
        }
        return true
    }

}