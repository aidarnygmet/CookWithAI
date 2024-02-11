package com.example.cookwithai.composables

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.cookwithai.BuildConfig
import com.example.cookwithai.viewModel.GeminiViewModel
import com.example.cookwithai.viewModel.GeminiViewModelFactory
import com.google.ai.client.generativeai.GenerativeModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(){
    val generativeModel = GenerativeModel(
        // Use a model that's applicable for your use case (see "Implement basic use cases" below)
        modelName = "gemini-pro-vision",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.API_KEY
    )
    val geminiViewModel : GeminiViewModel = viewModel(factory = GeminiViewModelFactory(generativeModel))
    val response  = geminiViewModel.state.response
    var userInput by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            userInput = userInput+ listOf(uri)
            Log.d("MainScreen", "Uri: ${userInput[0]}")
        }
    }
    Log.d("MainScreen", "User Input: ${userInput.size}")
    Scaffold(
        floatingActionButton = {
            if(response != ""){
                Button(
                    onClick = {
                        // Launch intent to open the note-taking app
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, response)
                            type = "text/plain"
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Save")
                }
            }

        },
        content = {
            Column {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier =Modifier.size(16.dp))
                    Text(text="Choose images of your ingredients:", style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                    Row (verticalAlignment = Alignment.CenterVertically){
                        repeat(3){index->
                            Log.d("MainScreen", "Index: $index and size: ${userInput.size}")
                            if(index < userInput.size){
                                val imagePainter: Painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(userInput[index])
                                        .build()
                                )
                                Image(
                                    painter = imagePainter,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(8.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )

                            } else {
                                if((index) == userInput.size){
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(8.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                                            .clickable {
                                                launcher.launch("image/*")
                                            }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        tint = Color.Gray,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(8.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(Color.DarkGray)
                                    )
                                }
                            }
                        }
                    }
                    Row (verticalAlignment = Alignment.CenterVertically){
                        repeat(2){index->
                            Log.d("MainScreen", "Index: ${index+3}and size: ${userInput.size}")
                            if(index+3 < userInput.size){
                                val imagePainter: Painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(userInput[index+3])
                                        .build()
                                )
                                Image(
                                    painter = imagePainter,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(8.dp)
                                        .clip(MaterialTheme.shapes.medium),
                                    contentScale = ContentScale.Crop
                                )

                            } else {
                                if((index+3) == userInput.size){
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        tint = MaterialTheme.colorScheme.primary,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(8.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
                                            .clickable {
                                                launcher.launch("image/*")
                                            }
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        tint = Color.Gray,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(8.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                            .background(Color.DarkGray)
                                    )
                                }


                            }
                        }
                    }
                    Spacer(modifier =Modifier.size(16.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
//                        val uriList = userInput.map { it.toString() }
//                        val bitmapList = mutableListOf<Bitmap>()
//                        runBlocking {
//                            uriList.forEach { uri ->
//                                try {
//                                    val inputStream =
//                                        context.contentResolver.openInputStream(Uri.parse(uri))
//                                    val bitmap = BitmapFactory.decodeStream(inputStream)
//                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, inputStream)
//                                    bitmapList.add(bitmap)
//                                    inputStream?.close()
//                                } catch (e: IOException) {
//                                    Log.e("MainScreen", "Error: ${e.message}")
//                                }
//                            }
//                            val prompt = content {
//                                text("Give me a detailed recipe for these ingredients:")
//                                bitmapList.forEach {
//                                    image(it)
//                                }
//                            }
//                            geminiViewModel.onClick(userInput, context)
//                        }
                        geminiViewModel.onClick(userInput, context)
                    },
                        modifier = Modifier
                            .width(120.dp)

                    ) {
                        if(response != ""){
                            Text(text = "Regenerate")
                        } else if (geminiViewModel.state.isLoading) {
                            Text(text = "Loading...")
                        } else {
                            Text(text = "Generate")
                        }
                    }
                    Button(onClick = { userInput = emptyList()
                        geminiViewModel.state = geminiViewModel.state.copy(response = "")
                    }, modifier = Modifier.width(120.dp)
                    ) {
                        Text(text ="Clear")
                    }
                }
                LazyColumn(content =
                {
                    item {
                        if(response != ""){
                            Text("Response:")
                            Text(text = response)
                        }
                    }
                }, modifier = Modifier.fillMaxSize()
                    .padding(16.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                    .padding(8.dp)
                )
            }
        }
    )

}

