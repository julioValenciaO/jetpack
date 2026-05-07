package com.unilibre.recetasia.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Query("SELECT * FROM recetas WHERE esFavorita = 1 ORDER BY fecha DESC")
    fun getFavoritas(): Flow<List<RecetaEntity>>

    @Query("SELECT * FROM recetas ORDER BY fecha DESC")
    fun getAll(): Flow<List<RecetaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(r: RecetaEntity)

    @Query("UPDATE recetas SET esFavorita = :esFavorita WHERE id = :id")
    suspend fun actualizarFavorita(id: String, esFavorita: Boolean)

    @Delete
    suspend fun eliminar(r: RecetaEntity)
}