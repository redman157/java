package company.ai.musicplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = Director.TABLE_NAME,
    indices = [Index(value = [Director.TITLE], unique = true)])
data class Director(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Int = 0,
    @ColumnInfo(name = TITLE) var displayName: String,
    @ColumnInfo(name = COUNT_LISTENER) var countListen: Int,
    @ColumnInfo(name = TYPE) var type: String) {
    companion object{
        const val ID = "id"
        const val TABLE_NAME = "director"
        const val TITLE = "name_music"
        const val COUNT_LISTENER = "count"
        const val TYPE = "type"
    }
}
