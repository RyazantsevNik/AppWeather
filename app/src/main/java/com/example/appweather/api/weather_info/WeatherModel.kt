package com.example.appweather.api.weather_info

data class WeatherModel(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)