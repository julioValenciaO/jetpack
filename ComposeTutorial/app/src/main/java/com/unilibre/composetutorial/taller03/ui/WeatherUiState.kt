package com.unilibre.composetutorial.taller03.ui

import com.unilibre.composetutorial.taller03.data.WeatherResponse

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}