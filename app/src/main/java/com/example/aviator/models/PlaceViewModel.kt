package com.example.aviator.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aviator.data.Airlane
import com.example.aviator.data.Place
import com.example.aviator.repository.AppRepository
import kotlinx.coroutines.launch
import java.util.*

class PlaceViewModel : ViewModel() {
    var place: MutableLiveData<List<Place>> = MutableLiveData()
    private var placeID: Long=-1

    init {
        AppRepository.get().place.observeForever{
          place.postValue(it)

        }
    }

    fun setAirlane(facultyID : Long){
        this.placeID = facultyID//передача id факультета
        loadPlaces()//прогрузка определенных групп факультета

    }
    private fun loadPlaces(){
        viewModelScope.launch{
            AppRepository.get().getAirlanePlace(placeID)
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
    suspend fun editPlace( place: Place) = AppRepository.get().updatePlace( place,placeID)
    suspend fun deletePlace(place: Place) = AppRepository.get().deletePlace(placeID, place)
}