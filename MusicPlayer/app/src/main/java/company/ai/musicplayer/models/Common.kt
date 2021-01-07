package company.ai.musicplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


data class Common(
    val displayName: String,
    val most: Int
)