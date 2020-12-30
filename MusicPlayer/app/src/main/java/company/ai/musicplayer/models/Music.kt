package company.ai.musicplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "musics")
data class Music(
    @PrimaryKey @ColumnInfo val displayName: String?,
    @ColumnInfo val artist: String?,
    @ColumnInfo val album: String?,
    val year: Int,
    val track: Int,
    val title: String?,
    @ColumnInfo val duration: Long,
    @ColumnInfo val albumID: Long?,
    val relativePath: String?,
    val id: Long?
) : Serializable


