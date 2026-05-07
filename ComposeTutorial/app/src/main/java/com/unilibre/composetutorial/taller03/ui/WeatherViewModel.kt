package com.unilibre.composetutorial.taller03.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unilibre.composetutorial.taller03.data.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeather(city: String) {
        if (city.isBlank()) return
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            try {
                val data = repository.getWeather(city)
                _uiState.value = WeatherUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
