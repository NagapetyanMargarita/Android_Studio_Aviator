package com.example.aviator.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aviator.data.AirPlane
import com.example.aviator.data.Flight
import com.example.aviator.data.Place
import com.example.aviator.data.Seat
import com.example.aviator.repository.AppRepository
import kotlinx.coroutines.launch
import java.util.*

class FlightViewModel : ViewModel() {
    var flight: MutableLiveData<List<Flight>> = MutableLiveData()
    private var placeID: Long =-1
    //вызов метода у university, который при изменении переменной будет обновлять place (благодаря id)
    init {
        AppRepository.get().flight.observeForever{
            flight.postValue(it)
        }
    }
    fun setPlaceID(groupID : Long){
        this.placeID = groupID
        loadFlight()
        // faculty.postValue(AppRepository.get().university.value?.find {faculty -> faculty.id==facultyID })
    }

    fun loadFlight() {
        viewModelScope.launch {
            AppRepository.get().getPlaceFlight(placeID)
        }
    }

   /* suspend fun getGroup() : Group?{
        var f : Group?=null
        val job = viewModelScope.launch {
            f = AppRepository.get().getGroup(groupID)
        }
        job.join()
        return f
    }*/
   /*fun setPlace(placeID : Long){
       this.placeID = placeID//передача id факультета
       loadFlight()//прогрузка определенных групп факультета

   }
    private fun loadPlaces(){
        viewModelScope.launch{
            AppRepository.get().getPlaceFlight(placeID)
        }
    }*/
   suspend fun newAirplaneWithSeats(airplane: AirPlane, numberOfRows: Int) {
       val airplaneId = AppRepository.get().newAirplane(airplane)
       val seats = ArrayList<Seat>()
       for (i in 1..numberOfRows) {
           seats.add(Seat(name = i.toString() + "A", isFree = true, airplaneID = airplaneId))
           seats.add(Seat(name = i.toString() + "B", isFree = true, airplaneID = airplaneId))
           seats.add(Seat(name = i.toString() + "C", isFree = true, airplaneID = airplaneId))
       }
       AppRepository.get().insertSeats(seats)
   }
   suspend fun newFlight(flight: Flight, placeID: Long) = AppRepository.get().newFlight(flight, placeID)
    suspend fun editFlight(flight: Flight) = AppRepository.get().editFlight(flight)
    suspend fun deleteFlight(flight: Flight) = AppRepository.get().deleteFlight(flight)

    suspend fun newAirplane(airPlane: AirPlane) = AppRepository.get().newAirplane(airPlane)
    //suspend fun editFlight(flight: Flight) = AppRepository.get().editFlight(flight)
    //suspend fun deleteFlight(flight: Flight) = AppRepository.get().deleteFlight(flight)
   /* fun setAirlaneAndPlaceID(airlaneID: UUID, placeID: UUID) {
        this.airlaneID = airlaneID
        this.placeID = placeID
        place.postValue(AppRepository.get().university.value?.find { airlane -> airlane.id == airlaneID }
            ?.places?.find { place -> place.id == placeID })
    }*/

   suspend fun NameOfPlace(): Place?{
        var f: Place?= null
        val job=viewModelScope.launch {
            f = AppRepository.get().getPlaceName(placeID)//получение факультета по id
        }
        job.join()
        return f
    }
}
//fun NameOfPlace(): String = place.value?.name ?: ""
 //вызов во фрагменте метода. (после репозитория)
/*  funewFlight(flight: Flight) = AppRepository.get().newFlight(airlaneID, placeID, flight)
 fun deleteFlight(flight: Flight) = AppRepository.get().deleteFlight(airlaneID, placeID, flight)
 fun editFlight(flightID: UUID, newFlight: Flight) =
     AppRepository.get().editFlight(airlaneID, placeID, flightID, newFlight)
 fun add_ticket(flightID: UUID, planeDate: String, seatName: String) =
     AppRepository.get().add_ticket(airlaneID, placeID, flightID, planeDate, seatName)

 */
