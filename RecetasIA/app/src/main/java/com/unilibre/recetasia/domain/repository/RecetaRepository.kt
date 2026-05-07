package com.unilibre.recetasia.domain.repository

import com.unilibre.recetasia.domain.model.Receta
import kotlinx.coroutines.flow.Flow

interface RecetaRepository {
    suspend fun generarRecetas(ingredientes: List<String>): List<Receta>
    fun getFavoritas(): Flow<List<Receta>>
    suspend fun guardarReceta(receta: Receta)
    suspend fun actualizarFavorita(id: String, esFavorita: Boolean)
}