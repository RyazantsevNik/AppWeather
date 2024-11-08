package com.example.appweather.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/v1/forecast.json")
    suspend fun getWeather(@Query("key") apikey: String,
                           @Query("q") city: String,
                           @Query("days") days: Int
    ) : retrofit2.Response<WeatherModel>
}