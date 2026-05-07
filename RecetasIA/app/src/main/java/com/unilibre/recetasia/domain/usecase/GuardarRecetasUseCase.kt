package com.unilibre.recetasia.domain.usecase

import com.unilibre.recetasia.domain.model.Receta
import com.unilibre.recetasia.domain.repository.RecetaRepository
import javax.inject.Inject

class GuardarRecetaUseCase @Inject constructor(
    private val repository: RecetaRepository
) {
    suspend operator fun invoke(receta: Receta) = repository.guardarReceta(receta)
}