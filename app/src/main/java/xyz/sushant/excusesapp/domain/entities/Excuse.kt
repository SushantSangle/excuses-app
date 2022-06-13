package xyz.sushant.excusesapp.domain.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "excuses") // Defining custom table names, if not provided, it defaults to lowercase classname
data class Excuse(
    val excuse: String,
    val category: String,
    @PrimaryKey val id: Int, //You need to provide a primary key for a table
    @ColumnInfo(name = "time_stamp") val timeStamp: Long = Date().time // You can also provide different names for fields in you table
)
