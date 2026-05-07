package com.unilibre.recetasia.domain.usecase

import com.unilibre.recetasia.domain.model.Receta
import com.unilibre.recetasia.domain.repository.RecetaRepository
import javax.inject.Inject

class GenerarRecetasUseCase @Inject constructor(
    private val repository: RecetaRepository
) {
    suspend operator fun invoke(ingredientes: List<String>): Result<List<Receta>> {
        if (ingredientes.isEmpty()) return Result.failure(Exception("No hay ingredientes detectados"))
        return runCatching { repository.generarRecetas(ingredientes) }
    }
}