package com.example.appweather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appweather.api.Constant
import com.example.appweather.api.Forecastday
import com.example.appweather.api.NetworkResponce
import com.example.appweather.api.RetrofitInstance
import com.example.appweather.api.WeatherModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException

class WeatherViewModel : ViewModel() {

    private val _city = MutableLiveData<String>()
    val city: LiveData<String> get() = _city

    fun updateCity(newCity: String) {
        _city.value = newCity
    }



    var hasLoadedLocation by mutableStateOf(false)
        private set
    fun setLocationLoaded() {
        hasLoadedLocation = true
    }

    // Функция для загрузки и обработки локации
    fun loadLocationData(locationHelper: LocationHelper, coroutineScope: CoroutineScope) {
        locationHelper.loadLastLocation { latitude, longitude ->
            if (latitude != 0.0 && longitude != 0.0) {
                // Если локация существует, загружаем данные
                coroutineScope.launch {
                    getData("$latitude,$longitude")

                }
            } else {
                // Если локации нет, запрашиваем разрешение
                locationHelper.checkLocationPermission()
            }
            // Устанавливаем флаг после выполнения
            setLocationLoaded()
        }

    }

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponce<WeatherModel>>()
    val weatherResult : LiveData<NetworkResponce<WeatherModel>> = _weatherResult


    fun getData(city: String) {

        _weatherResult.value = NetworkResponce.Loading
        viewModelScope.launch {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city, 7)
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    if (weatherData != null) {
                        _weatherResult.value = NetworkResponce.Success(weatherData)
                    } else {
                        _weatherResult.value = NetworkResponce.Error("Нет данных")
                    }
                } else {
                    _weatherResult.value = NetworkResponce.Error("Ошибка сервера: ${response.code()}")
                }
            }catch (e : Exception){
                _weatherResult.value = NetworkResponce.Error("Ошибка загрузки данных")
            }catch (e: IOException) {
                _weatherResult.value = NetworkResponce.Error("Ошибка сети")
            }

        }

    }

    fun getForecastDay(dayIndex: Int): Forecastday? {
        val forecastDays = (_weatherResult.value as? NetworkResponce.Success)?.data?.forecast?.forecastday
        return forecastDays?.getOrNull(dayIndex)
    }

}