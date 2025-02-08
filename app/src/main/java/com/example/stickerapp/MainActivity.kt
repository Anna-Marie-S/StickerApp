package com.example.stickerapp

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import com.example.stickerapp.ui.theme.StickerAppTheme
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StickerAppTheme {
                DrawingScreen(viewModel) {
                    checkAndAskPermission {
                        CoroutineScope(Dispatchers.IO).launch {
                            val uri = saveImage(it)
                            withContext(Dispatchers.Main) {
                                startActivity(activityChooser(uri))

                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CapturableScreen(){

    val captureController = rememberCaptureController()
    val scope = rememberCoroutineScope()

    Column(){
        Button(
            onClick = {
                scope.launch {
                    val bitmapAsync = captureController.captureAsync()
                    try{
                        val bitmap = bitmapAsync.await()
                    } catch (e: Throwable){
                        e.printStackTrace()
                    }
                }
            }
        ) { Text("Download") }

        Box(
            modifier = Modifier
                .background(Color.Red)
                .size(300.dp)
                .capturable(captureController)
        )
    }
}
@Composable
fun DrawingScreen(viewModel: MainViewModel, save: (Bitmap) -> Unit) {

    val stickerController = rememberStickerController()
    val drawController = rememberDrawController()
    val list = viewModel.stickerList


// a column with a box and the Controlbar
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControlBar(stickerController, drawController, viewModel)

// a box with the DrawBox and the StickerList
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f, fill = false)
                .background(Color.LightGray)
        ) {
            DrawBox(
                drawController = drawController,
                viewModel = viewModel,
                bitmapCallback = { imageBitmap, error ->
                    imageBitmap?.let {
                        save(it.asAndroidBitmap())
                    }
                })

        }
    }
}


@Composable
fun StickerScreen(
    viewModel: MainViewModel
){

    val stickerController = rememberStickerController()
    val drawController = rememberDrawController()
    val list = viewModel.stickerList



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControlBar(stickerController,drawController, viewModel)

// a box with the DrawBox and the StickerList
Box() {
    list.forEach { item ->
        key(
            item.id
        ) {
            DragRotateBox(
                resource = item,
                onDelete = viewModel::deleteSticker
            )
        }
    }
}
    }

}



