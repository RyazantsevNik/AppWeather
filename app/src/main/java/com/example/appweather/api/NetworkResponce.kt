package com.example.appweather.api

//T refers to WeatherModel
sealed class NetworkResponce<out T> {
    data class Success<out T>(val data : T): NetworkResponce<T>()
    data class Error(val message : String) : NetworkResponce<Nothing>()
    object Loading: NetworkResponce<Nothing>()
}