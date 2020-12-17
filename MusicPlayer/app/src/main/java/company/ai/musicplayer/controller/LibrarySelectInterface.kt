package company.ai.musicplayer.controller

import company.ai.musicplayer.models.Music

interface LibrarySelectInterface {
    fun onSelectMusic(song: Music?)
    fun onSelectGroup(songs: List<Music>?, launchedBy: String)
}