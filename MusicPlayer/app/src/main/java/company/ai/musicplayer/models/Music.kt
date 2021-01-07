package company.ai.musicplayer.models

import androidx.room.*
import company.ai.musicplayer.models.Music.Companion.TABLE_NAME
import company.ai.musicplayer.models.Music.Companion.TITLE
import java.io.Serializable

@Entity(tableName = TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = Director::class,
        parentColumns = ["did"],
        childColumns = [TITLE],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(TITLE)]
)
data class Music(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TITLE) val displayName: String?,
    @ColumnInfo(name = ARTIST)val artist: String?,
    @ColumnInfo(name = ALBUM)val album: String?,
    val year: Int,
    val track: Int,
    val title: String?,
    val duration: Long,
    val albumID: Long?,
    val relativePath: String?,
    @ColumnInfo(name = "album_id")val id: Long?
) : Serializable{
    companion object {
        const val TABLE_NAME = "music_tb"
        const val TITLE = "name_music"
        const val ARTIST = "artist"
        const val ALBUM = "album"
    }
}


