package com.example.appweather

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appweather.api.Constant
import com.example.appweather.api.NetworkResponce
import com.example.appweather.api.RetrofitInstance
import com.example.appweather.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponce<WeatherModel>>()
    val weatherResult : LiveData<NetworkResponce<WeatherModel>> = _weatherResult

    fun getData(city: String) {
        _weatherResult.value = NetworkResponce.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city, 7)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponce.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponce.Error("Failed to load data")
                }
            }catch (e : Exception){
                _weatherResult.value = NetworkResponce.Error("Failed to load data")
            }

        }

    }
}