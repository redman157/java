package company.ai.musicplayer.models

import androidx.room.*

import java.io.Serializable

@Entity(tableName = Music.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = Director::class,
        parentColumns = [Director.TITLE],
        childColumns = [Music.TITLE],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index(Music.TITLE)]
)
data class Music(
    @PrimaryKey @ColumnInfo(name = TITLE) val displayName: String,
    @ColumnInfo(name = ARTIST)val artist: String,
    @ColumnInfo(name = ALBUM)val album: String,
    val year: Int,
    val track: Int,
    val title: String?,
    val duration: Long,
    val albumID: Long?,
    val relativePath: String?,
    val audioId: Long?

//    @ColumnInfo(name = "album_id")val id: Long?
) : Serializable{
    companion object {
        const val ID = "id"
        const val TABLE_NAME = "music_tb"
        const val TITLE = "name_music"
        const val ARTIST = "artist"
        const val ALBUM = "album"
    }

    override fun toString(): String {
        return displayName!!
    }
}


