package com.unilibre.recetasia.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApi {

    @POST("openai/v1/chat/completions")
    suspend fun generarContenido(
        @Header("Authorization") authorization: String,
        @Body request: GroqRequest
    ): GroqResponse
}