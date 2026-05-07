package com.unilibre.recetasia.domain.model

data class Receta(
    val id: String = java.util.UUID.randomUUID().toString(),
    val nombre: String,
    val tiempoMinutos: Int,
    val dificultad: String,
    val pasos: List<String>,
    val calorias: Int,
    val ingredientes: List<String>,
    val esFavorita: Boolean = false
)