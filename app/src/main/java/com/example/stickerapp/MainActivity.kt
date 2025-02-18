package com.example.stickerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.stickerapp.ui.theme.StickerAppTheme
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StickerAppTheme {
                DrawingScreen(viewModel)
//                val stopwatch = remember {Stopwatch()}
//                StopwatchDisplay(
//                    formattedTime = stopwatch.formattedTime,
//                    onStartClick = stopwatch::start,
//                    onPauseClick = stopwatch::pause,
//                    onResetClick = stopwatch::reset,
//                )
            }
        }
    }
}


@Composable
fun StopwatchDisplay(
    formattedTime: String,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formattedTime,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.Black
        )
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(onStartClick){
                Text("Start")
            }
            Button(onPauseClick){
                Text("Pause")
            }
            Button(onResetClick){
                Text("Reset")
            }

        }
    }

}
@Composable
fun DrawingScreen(viewModel: MainViewModel) {

    val drawController = rememberDrawController()
    val captureController = rememberCaptureController()

    val uiScope = rememberCoroutineScope()
    var canvasBitmap: ImageBitmap? by remember { mutableStateOf(null) }

    var showDialog by remember { mutableStateOf(false) }


    fun downloadBitmap() {
        uiScope.launch {
            canvasBitmap = captureController.captureAsync().await()
        }
    }

    fun showBitmap() {
        showDialog = true
    }

// a column with a box and the Controlbar
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControlBar(drawController, viewModel)
        ControllerBar(
            onDownloadClick = {
                downloadBitmap() },
            onShowClick = { showBitmap() }
        )

        //with the DrawBox and the StickerList
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f, fill = false)
                .background(LightGray)
        ) {
            DrawBox(
                drawController = drawController,
                viewModel = viewModel,
                captureController = captureController
            )
        }
    }

        if (showDialog) {
            canvasBitmap?.let { bitmap ->
                Dialog(onDismissRequest = { }) {
                    Column(
                        modifier = Modifier
                            .background(LightGray)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Preview of Ticket image \uD83D\uDC47")
                        Spacer(Modifier.size(16.dp))
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Preview of ticket"
                        )
                        Spacer(Modifier.size(4.dp))
                        Button(onClick = {
                            showDialog = false
                            canvasBitmap = null
                        }) {
                            Text("Close Preview")
                        }
                    }
                }
            }

        }
    }




