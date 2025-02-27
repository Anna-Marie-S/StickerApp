package com.example.stickerapp

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
                val stopwatch = remember { Stopwatch() }
                DrawingScreen(
                    viewModel = viewModel,
                    formattedTime = stopwatch.formattedTime,
                    onStartClick = stopwatch::start,
                    onPauseClick = stopwatch::pause,
                ) {
                    checkAndAskPermission {
                        CoroutineScope(Dispatchers.IO).launch {
                            val uri = saveImage(it)
                            withContext(Dispatchers.Main) {
                                startActivity(activityChooser(uri))
                            }
                        }
                    }
                }
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

@SuppressLint("NewApi")
@Composable
fun DrawingScreen(
    viewModel: MainViewModel,
    formattedTime: String,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    save: (Bitmap) -> Unit,
    ) {

    val drawController = rememberDrawController()
    val captureController = rememberCaptureController()

    val uiScope = rememberCoroutineScope()
    var canvasBitmap: ImageBitmap? by remember { mutableStateOf(null) }

    var fileName = viewModel.fileName.collectAsState()


    var showDialog by remember { mutableStateOf(false) }
    var inputBoxVisible = viewModel.inputVisible.collectAsState()

    val context = LocalContext.current

    fun resetState(){
        viewModel.setMode(false)
        viewModel.resetCanvas()
        viewModel.changeRotation(0f)
        viewModel.changeOffset(Offset.Zero)
    }
    fun downloadBitmap() {
        uiScope.launch {
            canvasBitmap = captureController.captureAsync().await()
            canvasBitmap?.asAndroidBitmap()?.let { save(it) }
        }

    }

    fun showBitmap() {
        showDialog = true
    }
    fun showInput() {
        if (inputBoxVisible.value)
        {
            viewModel.setInputVisibility(false)
        } else {
            viewModel.setInputVisibility(true)
        }
    }
    fun showInfoBox(){}

    if(inputBoxVisible.value) {
        DialogWithTextField(
            onDismissRequest = {viewModel.setInputVisibility(false)},
            onConfirmation = {viewModel.updateFileName(it)
            onStartClick()}
        )
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
                captureController = captureController,
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
                        Text("Your Painting")
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
@Composable
fun DialogWithTextField(
    onDismissRequest: () -> Unit,
    onConfirmation: (String) -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest()}) {
        var text by remember { mutableStateOf("") }
        var textEnabled by remember { mutableStateOf(true) }
            Column(
                modifier = Modifier
                    .size(300.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.info_text),
                    modifier = Modifier.padding(16.dp),
                )

                Row(modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Filename") },
                        enabled = textEnabled
                    )
                    TextButton(
                        onClick = {
                            if (text.isNotEmpty()) {
                                onConfirmation(text)
                                textEnabled = false
                                onDismissRequest()
                            }
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }

            }
    }
}



@RequiresApi(Build.VERSION_CODES.Q)
private fun saveFileMediaStore(context: Context, content: String, fileName: String){
    try {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }else {
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(directory, fileName)
                put(MediaStore.MediaColumns.DATA, file.absolutePath)
            }
        }
        val resolver = context.contentResolver
        val fileMediaStoreUri = resolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )
        fileMediaStoreUri?.let { myUri ->
            try{
                resolver.openOutputStream(myUri)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                }

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING,0)
                resolver.update(myUri, contentValues, null, null)
            } catch (e: Exception){
                e.printStackTrace()
                resolver.delete(myUri, null, null)
            }
        }
    }catch (e: IOException){
        Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
    }
}


