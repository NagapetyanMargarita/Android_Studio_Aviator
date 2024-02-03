package com.example.aviator.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "airlane")
data class Airlane(
    @PrimaryKey(autoGenerate = true) val id : Long?,
    @ColumnInfo(name = "airlane_name") val name : String?,
    @ColumnInfo (name = "airlane_surname") val surname : String?,
    @ColumnInfo (name = "airlane_date") val date : Long?)