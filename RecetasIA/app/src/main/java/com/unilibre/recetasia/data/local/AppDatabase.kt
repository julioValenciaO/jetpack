package com.unilibre.recetasia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecetaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
}