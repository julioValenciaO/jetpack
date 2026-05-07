package com.unilibre.composetutorial.taller03.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String = "7492d41fc518ca22c13fdaf3ce676b60",
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): WeatherResponse
}