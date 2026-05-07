package com.unilibre.recetasia.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unilibre.recetasia.data.local.RecetaDao
import com.unilibre.recetasia.data.local.RecetaEntity
import com.unilibre.recetasia.data.remote.GroqApi
import com.unilibre.recetasia.data.remote.GroqRequest
import com.unilibre.recetasia.domain.model.Receta
import com.unilibre.recetasia.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ⚠️ Reemplaza con tu key de console.groq.com
private const val GROQ_API_KEY = "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"

class RecetaRepositoryImpl @Inject constructor(
    private val api: GroqApi,
    private val dao: RecetaDao,
    private val gson: Gson
) : RecetaRepository {

    override suspend fun generarRecetas(ingredientes: List<String>): List<Receta> {
        val ingredientesTexto = ingredientes.joinToString(", ")

        /**
         * IA: Me ayudó a estructurar este "System Prompt" y "User Prompt" para optimizar 
         * la respuesta de la IA (Groq). Gracias a esto, la IA devuelve un formato JSON 
         * estricto que mi aplicación puede procesar sin errores.
         */
        val systemMessage = GroqRequest.Message(
            role = "system",
            content = "Eres un chef experto. Siempre respondes SOLO con JSON válido, sin texto adicional, sin markdown, sin explicaciones."
        )

        val userMessage = GroqRequest.Message(
            role = "user",
            content = "Ingredientes disponibles: $ingredientesTexto. Dame exactamente 3 recetas en este formato JSON: [{\"nombre\":\"Nombre\",\"tiempo_minutos\":20,\"dificultad\":\"Facil\",\"pasos\":[\"paso 1\",\"paso 2\"],\"calorias\":300,\"ingredientes\":[\"ing1\",\"ing2\"]}]"
        )

        val request = GroqRequest(
            messages = listOf(systemMessage, userMessage)
        )

        return try {
            val response = api.generarContenido(
                authorization = "Bearer $GROQ_API_KEY",
                request = request
            )

            val texto = response.choices
                ?.firstOrNull()
                ?.message
                ?.content
                ?: return emptyList()

            /**
             * IA: Me proporcionó este bloque de código para "limpiar" la respuesta de la API.
             * A veces la IA incluye etiquetas como ```json, y este código asegura que 
             * solo extraigamos el contenido entre los corchetes [] para evitar errores al parsear.
             */
            val textoLimpio = texto
                .replace("```json", "")
                .replace("```", "")
                .trim()

            // Encontrar el JSON array
            val inicio = textoLimpio.indexOf('[')
            val fin = textoLimpio.lastIndexOf(']')

            if (inicio == -1 || fin == -1) return emptyList()

            val jsonLimpio = textoLimpio.substring(inicio, fin + 1)

            val tipo = object : TypeToken<List<RecetaJson>>() {}.type
            val recetasJson: List<RecetaJson> = gson.fromJson(jsonLimpio, tipo)
            recetasJson.map { it.toDomain() }

        } catch (e: Exception) {
            throw Exception("Error al generar recetas: ${e.message}")
        }
    }

    override fun getFavoritas(): Flow<List<Receta>> =
        dao.getFavoritas().map { lista -> lista.map { it.toDomain(gson) } }

    override suspend fun guardarReceta(receta: Receta) =
        dao.insertar(receta.toEntity(gson))

    override suspend fun actualizarFavorita(id: String, esFavorita: Boolean) =
        dao.actualizarFavorita(id, esFavorita)
}

/**
 * IA: Sugirió el uso de esta clase intermedia (Data Transfer Object) y funciones de extensión 
 * para separar los datos que vienen de la API/Base de Datos del modelo que usa la interfaz de usuario.
 */
private data class RecetaJson(
    val nombre: String = "",
    val tiempo_minutos: Int = 0,
    val dificultad: String = "",
    val pasos: List<String> = emptyList(),
    val calorias: Int = 0,
    val ingredientes: List<String> = emptyList()
) {
    fun toDomain() = Receta(
        nombre = nombre,
        tiempoMinutos = tiempo_minutos,
        dificultad = dificultad,
        pasos = pasos,
        calorias = calorias,
        ingredientes = ingredientes
    )
}

private fun RecetaEntity.toDomain(gson: Gson) = Receta(
    id = id,
    nombre = nombre,
    tiempoMinutos = tiempoMinutos,
    dificultad = dificultad,
    pasos = gson.fromJson(pasos, object : TypeToken<List<String>>() {}.type),
    calorias = calorias,
    ingredientes = gson.fromJson(ingredientes, object : TypeToken<List<String>>() {}.type),
    esFavorita = esFavorita
)

private fun Receta.toEntity(gson: Gson) = RecetaEntity(
    id = id,
    nombre = nombre,
    tiempoMinutos = tiempoMinutos,
    dificultad = dificultad,
    pasos = gson.toJson(pasos),
    calorias = calorias,
    ingredientes = gson.toJson(ingredientes),
    esFavorita = esFavorita
)