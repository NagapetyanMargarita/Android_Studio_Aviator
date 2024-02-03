package com.example.aviator.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aviator.data.Airlane
import com.example.aviator.repository.AppRepository
import kotlinx.coroutines.launch
import java.util.*

class AirlaneViewModel : ViewModel() {
    // university содержит список факультетов После получения данных из репозитория
    // класса AppRepository они устанавливаются в этот MutableLiveData объект.
    var university: MutableLiveData<List<Airlane>> = MutableLiveData()
    //слушателя изменений объекта university из репозитория с помощью метода observeForever().
    // После этого, полученный объект факультетов устанавливается в MutableLiveData объект.
    init {
        AppRepository.get().university.observeForever{
            university.postValue(it)//кто получает события
        }
        loadAirlane()
    }
    fun loadAirlane(){//загрузка чисто факультетов без связи
        viewModelScope.launch {
            AppRepository.get().loadAirlane()
        }
    }
}