package com.unilibre.recetasia.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unilibre.recetasia.domain.model.Receta
import com.unilibre.recetasia.domain.usecase.GenerarRecetasUseCase
import com.unilibre.recetasia.domain.usecase.GuardarRecetaUseCase
import com.unilibre.recetasia.domain.usecase.ObtenerFavoritosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecetasUiState {
    object Idle : RecetasUiState()
    object Cargando : RecetasUiState()
    data class Exito(val recetas: List<Receta>) : RecetasUiState()
    data class Error(val mensaje: String) : RecetasUiState()
}

@HiltViewModel
class RecetasViewModel @Inject constructor(
    private val generarRecetasUseCase: GenerarRecetasUseCase,
    private val guardarRecetaUseCase: GuardarRecetaUseCase,
    private val obtenerFavoritosUseCase: ObtenerFavoritosUseCase
) : ViewModel() {

    val favoritas: StateFlow<List<Receta>> = obtenerFavoritosUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<RecetasUiState>(RecetasUiState.Idle)
    val uiState: StateFlow<RecetasUiState> = _uiState.asStateFlow()

    private val _ingredientes = MutableStateFlow<List<String>>(emptyList())
    val ingredientes: StateFlow<List<String>> = _ingredientes.asStateFlow()

    private val _recetaSeleccionada = MutableStateFlow<Receta?>(null)
    val recetaSeleccionada: StateFlow<Receta?> = _recetaSeleccionada.asStateFlow()

    fun setIngredientes(lista: List<String>) { _ingredientes.value = lista }

    fun agregarIngrediente(ingrediente: String) {
        if (ingrediente.isNotBlank())
            _ingredientes.value = _ingredientes.value + ingrediente
    }

    fun eliminarIngrediente(ingrediente: String) {
        _ingredientes.value = _ingredientes.value - ingrediente
    }

    fun generarRecetas() {
        viewModelScope.launch {
            _uiState.value = RecetasUiState.Cargando
            generarRecetasUseCase(_ingredientes.value)
                .onSuccess { _uiState.value = RecetasUiState.Exito(it) }
                .onFailure { _uiState.value = RecetasUiState.Error(it.message ?: "Error desconocido") }
        }
    }

    fun seleccionarReceta(receta: Receta) { _recetaSeleccionada.value = receta }

    fun guardarFavorita(receta: Receta) {
        viewModelScope.launch { guardarRecetaUseCase(receta.copy(esFavorita = true)) }
    }

    fun resetState() { _uiState.value = RecetasUiState.Idle }
}