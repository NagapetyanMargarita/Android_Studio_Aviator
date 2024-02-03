package com.example.aviator.dao

import androidx.room.*
import com.example.aviator.data.*

@Dao
interface AirlaneDAO {
    @Insert(entity = Airlane::class,/*onConflict = OnConflictStrategy.REPLACE*/)
    fun insertNewAirlane(airlane: Airlane)//добавление факультета (передаем что вставляем)

    @Delete(entity = Airlane::class)//удаление 2
    fun deleteAirlane (airlane: Airlane)

    @Query("SELECT * FROM airlane order by airlane_name")
    fun loadAirlane(): List<Airlane> //вывод имени и ид факультета

    @Update(entity= Airlane::class)
    fun updateAirlane(faculty: Airlane)

    @Query("SELECT id, airlane_name FROM airlane where id=:id")
    fun getAirlane(id: Long): Airlane

    @Query("SELECT place_name FROM place where id=:id")
    fun getPlaceName(id: Long): Place

    @Query("DELETE FROM airlane")
    fun deleteAllAirlane()

    @Query("SELECT * FROM place where airlane_id=:airlaneID order by place_name")
    fun loadAirlanePlace(airlaneID: Long): List<Place>//переда

    @Insert(entity = Place::class,onConflict = OnConflictStrategy.REPLACE)
    fun insertNewPlace(place: Place)//вставка с передачей группы

    @Delete(entity = Place::class)
    fun deletePlace(place: Place)

    @Query("SELECT * FROM place order by place_name")
    fun loadPlace(): List<Place>//вывод группы

    @Update(entity =Place::class)
    fun updatePlace(place: Place)

    @Query("SELECT * FROM flight where place_id=:placeID order by timev")
    fun loadPlaceFlight(placeID: Long): List<Flight>//вывод определенных студентов

    @Insert(entity = Flight::class,onConflict = OnConflictStrategy.REPLACE)
    fun insertNewFlight(flight: Flight)

    @Delete(entity = Flight::class)
    fun deleteFlight(flight: Flight)

    @Query("SELECT * FROM flight order by timev")
    fun loadFlight(): List<Flight>//вывод студента

    @Update(entity= Flight::class)
    fun updateFlight(student: Flight)

    @Query("SELECT * FROM flight where id=:id")
    fun getFlight(id: Long): Flight?

    @Insert(entity = AirPlane::class,onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insertNewAirplane(airPlane: AirPlane): Long

   // @Query("SELECT * FROM flight where place_id=:placeID order by timev")
    //fun loadFlightAirplane(flightID: Long): List<Flight>//вывод определенных студентов

    @Insert(entity = Seat::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeats(seats: List<Seat>)
}