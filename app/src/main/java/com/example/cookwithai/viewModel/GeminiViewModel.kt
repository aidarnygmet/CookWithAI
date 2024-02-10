package com.example.cookwithai.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.asTextOrNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.FileNotFoundException

class GeminiViewModel(generativeModel: GenerativeModel): ViewModel() {
    var state by mutableStateOf(GeminiState())
    private var model = generativeModel
    fun onClick(prompt: Content){
        if(state.isLoading){
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            var response: String = ""
            try {
                model.generateContentStream(prompt).collect{chunk->
                    Log.d("GeminiViewModel", "Chunk: ${chunk.text}")
                    response+=chunk.text
                    state.copy(response = response, isLoading = true)
                }
                Log.d("GeminiViewModel", "Response: $response")
            } catch (e: Exception) {
                Log.d("GeminiViewModel", "Error: ${e.message}")
                state.copy(e = e.message ?: "An error occurred", isLoading = false)
            }finally {
                    state = state.copy(response = response, isLoading = false)
            }
        }
    }
}
