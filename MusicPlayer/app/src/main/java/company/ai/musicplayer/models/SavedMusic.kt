package company.ai.musicplayer.models

data class SavedMusic(
    val displayName: String,
    val artist: String,
    val album: String,
    val year: Int,
    val track: Int,
    val title: String?,
    val duration: Long,
    val albumID: Long?,
    val relativePath: String?,
    val audioId: Long?,
    val startFrom: Int,
    val launchedBy: String
)
