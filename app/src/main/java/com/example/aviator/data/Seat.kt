package com.example.aviator.data

import androidx.room.*

@Entity(tableName = "seat",
    indices = [Index("seat_name")],
    foreignKeys = [
        ForeignKey(
            entity = AirPlane::class,
            parentColumns = ["id"],
            childColumns = ["airplane_id"],
            onDelete = ForeignKey.CASCADE)
    ]
)

data class Seat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "seat_name") val name: String="",
    var isFree: Boolean=true,
    @ColumnInfo(name = "airplane_id") var airplaneID: Long?
){
    override fun toString(): String {
        return name
    }
}
