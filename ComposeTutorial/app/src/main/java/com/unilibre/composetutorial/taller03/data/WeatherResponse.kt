package com.unilibre.composetutorial.taller03.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("name") val city: String,
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<WeatherDesc>,
    @SerializedName("wind") val wind: Wind
)

data class Main(
    @SerializedName("temp") val temp: Double,
    @SerializedName("humidity") val humidity: Int
)

data class WeatherDesc(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String
)

data class Wind(
    @SerializedName("speed") val speed: Double
)