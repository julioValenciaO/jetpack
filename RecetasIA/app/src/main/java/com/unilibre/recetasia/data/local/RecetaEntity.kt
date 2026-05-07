package com.unilibre.recetasia.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class RecetaEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val tiempoMinutos: Int,
    val dificultad: String,
    val pasos: String,       // JSON string de la lista
    val calorias: Int,
    val ingredientes: String, // JSON string de la lista
    val esFavorita: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)