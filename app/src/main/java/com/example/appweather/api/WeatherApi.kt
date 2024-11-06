package com.example.appweather.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/v1/current.json")
    suspend fun getWeather(@Query("key") apikey: String,
                           @Query("q") city: String
    ) : retrofit2.Response<WeatherModel>
}