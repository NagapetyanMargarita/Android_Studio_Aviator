package com.example.aviator.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.aviator.Second352_2023Application
import com.example.aviator.data.*
import com.example.aviator.database.AirlaneDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList
const val SHARED_PREFERENCES_NAME = "UniversityAppPrefs"
const val AIRLANE_TAG = "AirlaneFragment"
const  val FACULTY_TITLE="Авиакомпания"

class AppRepository private constructor() {
    var university: MutableLiveData<List<Airlane>> = MutableLiveData()//airlane
    var place: MutableLiveData<List<Place>> = MutableLiveData()
    var flight: MutableLiveData<List<Flight>> = MutableLiveData()
    var airplane: MutableLiveData<List<AirPlane>> = MutableLiveData()
    var seat: MutableLiveData<List<Seat>> = MutableLiveData()

    companion object {
        private var INSTANCE: AppRepository? = null

        fun newInstance() {
            if (INSTANCE == null) {
                INSTANCE = AppRepository()
            }
        }

        fun get(): AppRepository {
            return INSTANCE ?: throw IllegalAccessException("Репозиторий не инициализирован")
        }
    }

   val db = Room.databaseBuilder(
       Second352_2023Application.applicationContext(),
       AirlaneDatabase::class.java, "uniDB.db"
   ).build()

    val airlaneDao = db.getDao()

    suspend fun newAirlane (name: String, surname: String, date: Long){
        val airlane =Airlane(id=null,name=name, surname=surname, date=date)
        withContext(Dispatchers.IO){
            airlaneDao.insertNewAirlane(airlane)//добавление факультета
            university.postValue(airlaneDao.loadAirlane())//вывод списка факультетов
        }
    }
    suspend fun deleteAirlane (airlane: Airlane ){
        withContext(Dispatchers.IO){
            airlaneDao.deleteAirlane(airlane)//удаление факультета
            university.postValue(airlaneDao.loadAirlane())//вывод списка факультетов
        }
    }
    suspend fun updateAirlane (airlane: Airlane){
        withContext(Dispatchers.IO){
            airlaneDao.updateAirlane(airlane)//добавление факультета
            university.postValue(airlaneDao.loadAirlane())//вывод списка факультетов
        }
    }
    suspend fun loadAirlane (){
        withContext(Dispatchers.IO){
            university.postValue(airlaneDao.loadAirlane())
        }
    }
    suspend fun getAirlanePlace (facultyID: Long){
        withContext(Dispatchers.IO){
            place.postValue(airlaneDao.loadAirlanePlace(facultyID))//вывод группы с определенным id
        }
    }

    suspend fun getFaculty(facultyID: Long): Airlane?{
        var f : Airlane?=null
        val job= CoroutineScope(Dispatchers.IO).launch {
            f=airlaneDao.getAirlane(facultyID)//вывод факультета
        }
        job.join()
        return f
    }
    suspend fun newPlace(airlaneID: Long, name:String){
        val place = Place(id=null,name=name, airlaneID=airlaneID)
        withContext(Dispatchers.IO){
            airlaneDao.insertNewPlace(place)
            getAirlanePlace(airlaneID)
        }
    }
    suspend fun deletePlace(airlaneID: Long, place: Place){
        withContext(Dispatchers.IO){
            airlaneDao.deletePlace(place)//удаление факультета
            getAirlanePlace(airlaneID)//вывод списка факультетов
        }
    }
    suspend fun updatePlace(place: Place, airlaneID: Long){
        withContext(Dispatchers.IO){
            airlaneDao.updatePlace(place)
            getAirlanePlace(airlaneID)
        }
    }
    suspend fun getPlaceFlight(placeID: Long) /*:List<Student> */ {
        withContext(Dispatchers.IO) {
            flight.postValue(airlaneDao.loadPlaceFlight(placeID))
        }
    }
   /* suspend fun getFlightAirplane(flightID: Long) /*:List<Student> */ {
        withContext(Dispatchers.IO) {
            airplane.postValue(airlaneDao.loadFlightAirplane(flightID))
        }
    }*/
    suspend fun getPlaceName(placeID: Long) : Place?{
        var f : Place?=null
        val job= CoroutineScope(Dispatchers.IO).launch {
            f=airlaneDao.getPlaceName(placeID)//вывод факультета
        }
        job.join()
        return f
    }
    suspend fun newFlight(flight: Flight, placeID: Long) {
        withContext(Dispatchers.IO) {
            airlaneDao.insertNewFlight(flight)
            getPlaceFlight(placeID!!)
        }
    }

   /* suspend fun updateStudent(flight: Flight, groupID: Long){
        withContext(Dispatchers.IO){
            airlaneDao.updateStudent(flight)
            getPlaceFlight(flight.placeID!!)
        }
    }*/
    suspend fun editFlight(flight: Flight) {
        withContext(Dispatchers.IO){
            airlaneDao.updateFlight(flight)
            getPlaceFlight(flight.placeID!!)
        }
    }

    suspend fun deleteFlight(flight: Flight) {
        withContext(Dispatchers.IO) {
            airlaneDao.deleteFlight(flight)
            getPlaceFlight(flight.placeID!!)
        }
    }
    suspend fun newAirplane(airplane: AirPlane): Long {
          return airlaneDao.insertNewAirplane(airplane)
            //getFlightAirplane(flight.placeID!!)
    }
    suspend fun insertSeats(seat: List<Seat>)  {
        return airlaneDao.insertSeats(seat)
        //getFlightAirplane(flight.placeID!!)
    }
   /* suspend fun getGroup(groupID: Long): Group? {
        var f : Group?=null
        val job= CoroutineScope(Dispatchers.IO).launch {
            f=universityDao.getGroup(groupID)
        }
        job.join()
        return f
    }*/

    //передача н.факультета, определение списка(пустой или с уже созданными), добавление в него, обновление общее
    /*fun newAirline(name: String, surname: String,date: Long) {
        val airline = Airlane(name = name, surname=surname, date = date)
        val list: MutableList<Airlane> =
            if (university.value != null) {
                (university.value as ArrayList<Airlane>)
            } else
                ArrayList<Airlane>()
        list.add(airline)
        university.postValue(list)
    }
    fun deleteAirlane(airlane: Airlane) {
        val list: ArrayList<Airlane> = university.value as ArrayList<Airlane>
        list.remove(airlane)
        university.postValue(list)
    }*/

    /*fun editAirlane(id: UUID, name: String, surname: String,date: Date){
        val list: ArrayList<Airlane> = university.value as ArrayList<Airlane>

        val _airline = list.find { it.id == id }
        if (_airline == null) {
            newAirline(name, surname,date)
            return
        }
        val airline = Airlane(id = id, name = name, surname=surname, date = date)
        airline.places = _airline.places
        val i = list.indexOf(_airline)
        list.remove(_airline)
        list.add(i, airline)
        university.postValue(list)
    }*/
    //Добавление, удаление и редактирование Городов
   /* fun newPlace(airlaneID: UUID, name: String){
        // равно `null`, то `return` -выход из функции и возврат `null` иначе `value` присваивается переменной `u`.
        val u = university.value?: return
        val airlane = u.find{it. id== airlaneID} ?: return
        val place = Place(name=name)
        val list: ArrayList<Place> =
            if (airlane.places.isEmpty())
                ArrayList()
            else
                airlane.places as ArrayList<Place>
        list.add(place)
        airlane.places=list
        university.postValue(u)
    }
    fun deletePlace(airlineId: UUID, place: Place) {
        val u = university.value ?: return
        val airline = u.find { it.id == airlineId } ?: return
        val list: ArrayList<Place> = if (airline.places.isEmpty())
            ArrayList()
        else
            airline.places as ArrayList<Place>
        list.remove(place)
        airline.places = list
        university.postValue(u)
    }
    fun editPlace(airlineId: UUID, cityId: UUID, name: String) {
        val u = university.value ?: return
        val airline = u.find { it.id == airlineId } ?: return
        val list: ArrayList<Place> = if (airline.places.isEmpty())
            ArrayList()
        else
            airline.places as ArrayList<Place>
        val _city = list.find { it.id == cityId }
        if(_city == null){
            newPlace(airlineId, name)
            return
        }
        val city = Place(id = cityId, name = name)
        city.flights = _city.flights
        val i = list.indexOf(_city)
        list.remove(_city)
        list.add(i, city)
        airline.places = list
        university.postValue(u)
    }

    //Добавление Рейсов
    fun newFlight(airlaneID: UUID?, placeID: UUID?, flight: Flight){
        val u = university.value ?: return
        val airlane = u.find { it.id == airlaneID } ?: return
        val place = airlane.places.find { it.id == placeID } ?: return
        val list: ArrayList<Flight> = if (place.flights.isEmpty())
            ArrayList()
        else
            place.flights as ArrayList<Flight>
        list.add(flight)
        place.flights = list
        university.postValue(u)
    }
    fun deleteFlight(airlineID: UUID?, cityID: UUID?, flight: Flight) {
        val u = university.value ?: return
        val airline = u.find { it.id == airlineID } ?: return
        val place = airline.places.find { it.id == cityID } ?: return
        val list: ArrayList<Flight> = if (place.flights.isEmpty())
            ArrayList()
        else
            place.flights as ArrayList<Flight>
        list.remove(flight)
        place.flights = list
        university.postValue(u)
    }
    fun editFlight(airlineID: UUID?, cityID: UUID?, flightID: UUID, newFlight: Flight) {
        val u = university.value ?: return
        val airline = u.find { it.id == airlineID } ?: return
        val city = airline.places.find { it.id == cityID } ?: return
        val list: ArrayList<Flight> = if (city.flights.isEmpty())
            ArrayList()
        else
            city.flights as ArrayList<Flight>
        val _flight = list.find { it.id == flightID }
        if (_flight == null) {
            newFlight(airlineID, cityID, newFlight)
            return
        }
        val i = list.indexOf(_flight)
        list.remove(_flight)
        list.add(i, newFlight)
        city.flights = list
        university.postValue(u)
    }

    //создание билетов
    fun add_ticket(airlaneID: UUID?, placeID: UUID?, flightID: UUID, planeDate: String, seatName: String
    ) {
        val u = university.value ?: return
        val airlane = u.find { it.id == airlaneID } ?: return
        val place = airlane.places.find { it.id == placeID } ?: return
        val flight = place.flights.find { it.id == flightID } ?: return
        val plane = flight.airplanes.find { it.date == planeDate } ?: return
        val list: ArrayList<Seat> = if (plane.seats.isEmpty())
            ArrayList()
        else
            plane.seats
        val _seat = list.find { it.name == seatName }
        val newSeat = Seat(name = seatName, isFree = false)
        val i = list.indexOf(_seat)
        list.remove(_seat)
        list.add(i, newSeat)
        plane.seats = list
        university.postValue(u)
    }*/

}