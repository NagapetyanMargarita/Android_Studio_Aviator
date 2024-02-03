package com.example.aviator.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aviator.data.Airlane
import com.example.aviator.data.Flight
import com.example.aviator.data.Place
import com.example.aviator.repository.AppRepository
import kotlinx.coroutines.launch

class PlaceListViewModel : ViewModel() {
    var flight: MutableLiveData<List<Flight>> = MutableLiveData()
    private var placeID: Long=-1

    init {
        AppRepository.get().flight.observeForever{
            flight.postValue(it)

        }
    }

    fun setPlaceID(facultyID : Long){
        this.placeID = facultyID//передача id факультета
        loadFlight()//прогрузка определенных групп факультета

    }
    private fun loadFlight(){
        viewModelScope.launch{
            AppRepository.get().getPlaceFlight(placeID)
        }
    }
    suspend fun getAirlane(): Airlane?{
        var f: Airlane?= null
        val job=viewModelScope.launch {
            f = AppRepository.get().getFaculty(placeID)//получение факультета по id
        }
        job.join()
        return f
    }
    fun loadFlight(placeID: Long) {
        viewModelScope.launch {
            AppRepository.get().getPlaceFlight(placeID)
        }
    }

    suspend fun deleteFlight(flight: Flight) = AppRepository.get().deleteFlight(flight)
}