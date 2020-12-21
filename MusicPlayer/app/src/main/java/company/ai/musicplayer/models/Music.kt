package company.ai.musicplayer.models

import java.io.Serializable

data class Music(
    val artist: String?,
    val year: Int,
    val track: Int,
    val title: String?,
    val displayName: String?,
    val duration: Long,
    val album: String?,
    val albumID: Long?,
    val relativePath: String?,
    val id: Long?
) : Serializable
