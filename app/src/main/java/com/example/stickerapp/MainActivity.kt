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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.stickerapp.ui.theme.StickerAppTheme
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


                list.forEach { sticker ->
                    key(
                        sticker.id
                    ) {
                        DragRotateBox(
                            resource = sticker,
                            onDelete = viewModel::deleteSticker
                        )
                    }
                }

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



