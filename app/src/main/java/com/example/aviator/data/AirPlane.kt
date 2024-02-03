package com.example.aviator.data

import androidx.room.*
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "airplane",
    indices = [Index("date")],
    foreignKeys = [
        ForeignKey(
            entity = Flight::class,
            parentColumns = ["id"],
            childColumns = ["flight_id"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class AirPlane(
    @PrimaryKey(autoGenerate = true) val id : Long?,
    val date: String?,
    val num_Seats: Int?,
    val num_Rows: Int?,
    @ColumnInfo(name = "flight_id") val flightID: Long?,
    //var seats: List<Seat> = emptyList()
)
