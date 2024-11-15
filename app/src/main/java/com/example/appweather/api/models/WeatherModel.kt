package com.example.appweather.api.models

data class WeatherModel(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)