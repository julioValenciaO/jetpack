package com.unilibre.recetasia.data.remote

data class GroqRequest(
    val model: String = "llama-3.1-8b-instant",
    val messages: List<Message>,
    val temperature: Double = 0.3,
    val max_tokens: Int = 1024
) {
    data class Message(val role: String, val content: String)
}