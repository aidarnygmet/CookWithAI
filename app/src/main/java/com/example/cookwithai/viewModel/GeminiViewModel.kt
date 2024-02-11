package com.example.cookwithai.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asImageOrNull
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

class GeminiViewModel(generativeModel: GenerativeModel): ViewModel() {
    var state by mutableStateOf(GeminiState())
    private var model = generativeModel
    fun onClick(uri: List<Uri>, context: Context){
        if(state.isLoading){
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            state = state.copy(isLoading = true)
            runBlocking {
//                val bitmapList = mutableListOf<Bitmap>()
                val bitmapList = compressImages(uri, context)
//                uri.forEach {
//                    compressImage(it, 600, 800, context)?.let { bitmap -> bitmapList.add(bitmap) }
//                }
                val prompt = content {
                    text("Give me a detailed recipe for these ingredients:")
                    bitmapList.forEach {
                        if (it != null) {
                            image(it)
                        }
                    }
                }
                Log.d("GeminiViewModel", "Prompt: ${prompt.parts[1].asImageOrNull()?.byteCount}")
                var response = ""
                try {
                    model.generateContentStream(prompt).collect{chunk->
                        Log.d("GeminiViewModel", "Chunk: ${chunk.text}")
                        response+=chunk.text
                        state = state.copy(response = response, isLoading = true)
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
}
private fun calculateSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 4
    Log.d("GeminiViewModel", "Height: $height, Width: $width")
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }
    Log.d("GeminiViewModel", "Sample Size: $inSampleSize")
    return inSampleSize
}
fun compressImage(uri: Uri, maxWidth: Int, maxHeight: Int, context: Context): Bitmap? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val options = BitmapFactory.Options()
    options.inSampleSize = calculateSampleSize(options, maxWidth, maxHeight)
    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
    val compressedBytes = byteArrayOutputStream.toByteArray()
    val ans = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
    Log.d("GeminiViewModel", "Compressed: ${ans.byteCount}")
    inputStream?.close()
    return ans
//    inputStream?.use { stream ->
//        Log.d("GeminiViewModel", "Stream: ${uri}")
//        // Decode image bounds to calculate the sample size
//        val options = BitmapFactory.Options().apply {
//            inJustDecodeBounds = true
//            BitmapFactory.decodeStream(stream, null, this)
//
//        }
//        options.inSampleSize = calculateSampleSize(options, maxWidth, maxHeight)
//
//
//        options.inJustDecodeBounds = false
//        bitmap = BitmapFactory.decodeStream(stream, null, options)
//        Log.d("GeminiViewModel", "Bitmap1: ${bitmap?.byteCount}")
//        stream.close()
//    }
//    Log.d("GeminiViewModel", "Bitmap2: ${bitmap?.byteCount}")
//    return bitmap
}
suspend fun compressImages(uris: List<Uri>, context: Context): List<Bitmap?> = coroutineScope {
val bitmaps = uris.map { uri->
    async { compressImage(uri, 600, 800, context) }

}
    bitmaps.awaitAll()
}
//fun compressAndConvertToBitmap(context: Context, uri: Uri, compressFormat: Bitmap.CompressFormat): Bitmap? {
//    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
//    val options = BitmapFactory.Options()
//    options.inSampleSize = getSampleSize(options, 600, 800) // Resize if needed
//    val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
//    val byteArrayOutputStream = ByteArrayOutputStream()
//    bitmap.compress(compressFormat, quality, byteArrayOutputStream)
//    val compressedBytes = byteArrayOutputStream.toByteArray()
//    return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
//}
