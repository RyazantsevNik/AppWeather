package com.example.appweather.view_models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appweather.location_helper.LocationHelper
import com.example.appweather.api.Constant
import com.example.appweather.api.weather_info.Forecastday
import com.example.appweather.api.NetworkResponce
import com.example.appweather.api.RetrofitInstance
import com.example.appweather.api.weather_info.Hour
import com.example.appweather.api.weather_info.WeatherModel
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
    fun loadLocationData(locationHelper: LocationHelper, coroutineScope: CoroutineScope, forceUpdate: Boolean = false) {
        if (hasLoadedLocation && !forceUpdate) return

        locationHelper.loadLastLocation { latitude, longitude ->
            if (latitude != 0.0 && longitude != 0.0) {
                // Если локация существует, загружаем данные
                Log.d("ASD","DA")
                coroutineScope.launch {
                    getData("$latitude,$longitude")

                }
            } else {
                Log.d("ASD","NET")
                // Если локации нет, запрашиваем разрешение
                locationHelper.checkLocationPermission()
            }
            // Устанавливаем флаг после выполнения
            setLocationLoaded()
        }

    }

    private val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponce<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponce<WeatherModel>> = _weatherResult


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
                    _weatherResult.value =
                        NetworkResponce.Error("Ошибка сервера: ${response.code()}")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponce.Error("Ошибка загрузки данных")
            } catch (e: IOException) {
                _weatherResult.value = NetworkResponce.Error("Ошибка сети")
            }

        }

    }

    fun getForecastDay(dayIndex: Int): Forecastday? {
        val forecastDays =
            (_weatherResult.value as? NetworkResponce.Success)?.data?.forecast?.forecastday
        return forecastDays?.getOrNull(dayIndex)
    }

    fun getHoursForDay(dayIndex: Int): List<Hour>? {
        val forecastDays = (_weatherResult.value as? NetworkResponce.Success)?.data?.forecast?.forecastday
        return forecastDays?.getOrNull(dayIndex)?.hour
    }

    fun getHour(dayIndex: Int, hourIndex: Int): Hour? {
        return getHoursForDay(dayIndex)?.getOrNull(hourIndex)
    }

}


