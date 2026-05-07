package com.unilibre.composetutorial.taller03.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApiService::class.java)

    suspend fun getWeather(city: String): WeatherResponse {
        return api.getWeather(city)
    }
}