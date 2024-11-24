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
import com.example.appweather.api.models.Forecastday
import com.example.appweather.api.NetworkResponse
import com.example.appweather.api.RetrofitInstance
import com.example.appweather.api.models.Hour
import com.example.appweather.api.models.WeatherModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult


    fun getData(city: String) {
        // Загружаем данные в фоновом потоке
        _weatherResult.postValue(NetworkResponse.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = weatherApi.getWeather(Constant.apiKey, city, 7)
                if (response.isSuccessful) {
                    val weatherData = response.body()
                    if (weatherData != null) {
                        // Обновляем LiveData в главном потоке
                        withContext(Dispatchers.Main) {
                            _weatherResult.postValue(NetworkResponse.Success(weatherData)) // Используем postValue
                        }
                    } else {
                        // Обновляем LiveData с ошибкой в главном потоке
                        withContext(Dispatchers.Main) {
                            _weatherResult.postValue(NetworkResponse.Error("Нет данных"))
                        }
                    }
                } else {
                    // Обновляем LiveData с ошибкой в главном потоке
                    withContext(Dispatchers.Main) {
                        _weatherResult.postValue(NetworkResponse.Error("Некорректный город"))
                    }
                }
            } catch (e: Exception) {
                // Обновляем LiveData с ошибкой в главном потоке
                withContext(Dispatchers.Main) {
                    _weatherResult.postValue(NetworkResponse.Error("Ошибка загрузки данных"))
                }
            } catch (e: IOException) {
                // Обновляем LiveData с ошибкой в главном потоке
                withContext(Dispatchers.Main) {
                    _weatherResult.postValue(NetworkResponse.Error("Ошибка сети"))
                }
            }
        }
    }

    fun getForecastDay(dayIndex: Int): Forecastday? {
        val forecastDays =
            (_weatherResult.value as? NetworkResponse.Success)?.data?.forecast?.forecastday
        return forecastDays?.getOrNull(dayIndex)
    }

    private fun getHoursForDay(dayIndex: Int): List<Hour>? {
        val forecastDays = (_weatherResult.value as? NetworkResponse.Success)?.data?.forecast?.forecastday
        return forecastDays?.getOrNull(dayIndex)?.hour
    }

    fun getHour(dayIndex: Int, hourIndex: Int): Hour? {
        return getHoursForDay(dayIndex)?.getOrNull(hourIndex)
    }

}