package company.ai.musicplayer.utils

import company.ai.musicplayer.models.Music

object ListsHelper {
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
}