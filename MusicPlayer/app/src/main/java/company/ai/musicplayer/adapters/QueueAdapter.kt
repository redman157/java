package company.ai.musicplayer.adapters

import android.content.Context
import company.ai.musicplayer.models.Music
import company.ai.musicplayer.player.MediaPlayerHolder

class QueueAdapter(var context: Context, var mediaPlayerHolder: MediaPlayerHolder) {
    private var mQueueSongs = mediaPlayerHolder.queueSongs
    private var mSelectedSong = mediaPlayerHolder.currentSong
    fun swapSelectedSong(song: Music?) {
        mSelectedSong = Pair(song, true)
    }
}