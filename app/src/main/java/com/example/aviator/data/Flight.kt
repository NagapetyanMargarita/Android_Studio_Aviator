package com.example.aviator.data

import androidx.room.*
import java.util.*

@Entity(tableName = "flight",
    indices = [Index("fromPlace","inPlace","timev")],
    foreignKeys = [
        ForeignKey(
            entity = Place::class,
            parentColumns = ["id"],
            childColumns = ["place_id"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class Flight(
    @PrimaryKey(autoGenerate = true) val id : Long?,
    var fromPlace: String?,
    var inPlace: String?,
    var nameOfPlane: String?,
    var prices: Int?,
    var timev: String?,
    var dayOfWeek: String?,
    @ColumnInfo(name = "place_id") val placeID: Long?
)