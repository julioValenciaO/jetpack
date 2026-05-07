package com.unilibre.recetasia.domain.usecase

import com.unilibre.recetasia.domain.repository.RecetaRepository
import javax.inject.Inject

class ObtenerFavoritosUseCase @Inject constructor(
    private val repository: RecetaRepository
) {
    operator fun invoke() = repository.getFavoritas()
}