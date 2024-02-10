package com.example.cookwithai.viewModel

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse

class GeminiState {
    var isLoading: Boolean = false
    var response: String = ""
    var userInput: Content? = null
    var error: String = ""
    fun copy(isLoading: Boolean = false, userInput: Content? = null, response: String = "", e: String = ""): GeminiState {
        val newState = GeminiState()
        newState.isLoading = isLoading
        newState.userInput = userInput
        newState.response = response
        newState.error = e
        return newState
    }
}
