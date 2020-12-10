package company.ai.musicplayer.utils

import android.support.v4.media.session.PlaybackStateCompat


object Constants {
    const val TAG_FRAGMENT: String = "TAG_FRAGMENT"
    const val RESTORE_SETTINGS_FRAGMENT = "RESTORE_SETTINGS_FRAGMENT"
    const val DIALOG_FRAGMENT: String = "DIALOG_FRAGMENT"
    const val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 2588

    // on list ended option
    const val CONTINUE = "0"
    // Notification
    const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID_GO"
    const val NOTIFICATION_INTENT_REQUEST_CODE = 100
    const val NOTIFICATION_ID = 101
    const val FAST_FORWARD_ACTION = "FAST_FORWARD_GO"
    const val PREV_ACTION = "PREV_GO"
    const val PLAY_PAUSE_ACTION = "PLAY_PAUSE_GO"
    const val NEXT_ACTION = "NEXT_GO"
    const val REWIND_ACTION = "REWIND_GO"
    const val REPEAT_ACTION = "REPEAT_GO"
    const val CLOSE_ACTION = "CLOSE_GO"

    // error tags
    const val TAG_NO_PERMISSION = "NO_PERMISSION"
    const val TAG_NO_MUSIC = "NO_MUSIC"
    const val TAG_NO_MUSIC_INTENT = "NO_MUSIC_INTENT"
    const val TAG_SD_NOT_READY = "SD_NOT_READY"

    // sorting
    const val DEFAULT_SORTING = 0
    const val DESCENDING_SORTING = 1
    const val ASCENDING_SORTING = 2
    const val TRACK_SORTING = 3
    const val TRACK_SORTING_INVERTED = 4

    // launched by, used to determine which MusicContainerListFragment is instantiated by the ViewPager
    const val ALL_MUSIC_VIEW = "ALL_MUSIC_VIEW"
    const val ARTIST_VIEW = "ARTIST_VIEW"
    const val ALBUM_VIEW = "ALBUM_VIEW"
    const val FOLDER_VIEW = "FOLDER_VIEW"

    // Player playing statuses
    const val PLAYING = PlaybackStateCompat.STATE_PLAYING
    const val PAUSED = PlaybackStateCompat.STATE_PAUSED
    const val RESUMED = PlaybackStateCompat.STATE_NONE
}