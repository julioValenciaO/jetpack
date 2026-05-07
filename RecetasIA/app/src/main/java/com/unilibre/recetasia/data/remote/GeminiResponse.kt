package com.unilibre.recetasia.data.remote

data class GroqResponse(
    val choices: List<Choice>?
) {
    data class Choice(val message: Message?)
    data class Message(val content: String?)
}