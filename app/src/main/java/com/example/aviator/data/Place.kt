package com.example.aviator.data

import androidx.room.*
import java.util.*

@Entity(tableName = "place",
    indices = [Index("place_name")],
    foreignKeys = [
        ForeignKey(
            entity = Airlane::class,
            parentColumns = ["id"],
            childColumns = ["airlane_id"],
            onDelete = ForeignKey.CASCADE)
    ]
)
data class Place(
    @PrimaryKey(autoGenerate = true)  val id : Long?,
    @ColumnInfo(name = "place_name") val name : String?,
    @ColumnInfo(name = "airlane_id") val airlaneID : Long?)

