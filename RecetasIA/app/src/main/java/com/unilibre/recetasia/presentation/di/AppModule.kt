package com.unilibre.recetasia.presentation.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.unilibre.recetasia.data.local.AppDatabase
import com.unilibre.recetasia.data.local.RecetaDao
import com.unilibre.recetasia.data.remote.GroqApi
import com.unilibre.recetasia.data.repository.RecetaRepositoryImpl
import com.unilibre.recetasia.domain.repository.RecetaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttp(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.groq.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideGroqApi(retrofit: Retrofit): GroqApi =
        retrofit.create(GroqApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "recetas_db").build()

    @Provides
    fun provideDao(db: AppDatabase): RecetaDao = db.recetaDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(impl: RecetaRepositoryImpl): RecetaRepository
}