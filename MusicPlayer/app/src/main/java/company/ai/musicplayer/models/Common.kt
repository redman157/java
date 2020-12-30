package company.ai.musicplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "common")
data class Common(
    @PrimaryKey @ColumnInfo val displayName: String,
    @ColumnInfo val most: Int
)