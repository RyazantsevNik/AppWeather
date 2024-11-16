package com.example.appweather.api.models

data class Forecastday(
    val astro: Astro,
    val date: String,
    val date_epoch: String,
    val day: Day,
    val hour: List<Hour>
)