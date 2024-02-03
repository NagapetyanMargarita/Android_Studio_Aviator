package com.example.aviator.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aviator.dao.AirlaneDAO
import com.example.aviator.data.*

@Database(
    version = 1,
    entities = [
        Airlane::class,
        Place::class,
        Flight::class,
        AirPlane::class,
        Seat::class
    ]
)
abstract  class AirlaneDatabase : RoomDatabase(){
    abstract fun getDao(): AirlaneDAO
}