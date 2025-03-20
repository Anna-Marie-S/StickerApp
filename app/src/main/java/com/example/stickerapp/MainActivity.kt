package com.example.stickerapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.stickerapp.ui.theme.StickerAppTheme
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            StickerAppTheme {
                val context = LocalContext.current
                (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
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

    val fileName = viewModel.fileName.collectAsState()
    val address = viewModel.address.collectAsState()
    val stickerList = viewModel.stickerList


    var showDialog by remember { mutableStateOf(false) }
    val showCameraDialog = viewModel.cameraDialogVisible.collectAsState()
    val inputBoxVisible = viewModel.inputVisible.collectAsState()
    val infoBoxVisible = viewModel.infoBoxVisible.collectAsState()
    val dragMode = viewModel.dragMode.collectAsState()

    val context = LocalContext.current

    fun resetState(){
        viewModel.setMode(false)
        viewModel.resetCanvas()
        viewModel.changeRotation(0f)
        viewModel.changeOffset(Offset.Zero)
    }

    if(!dragMode.value){
        viewModel.changeScale(1f)
    }
    fun downloadBitmap() {
        uiScope.launch {
            canvasBitmap = captureController.captureAsync().await()
            canvasBitmap?.asAndroidBitmap()?.let { saveBitmapMediaStore(context,it, fileName.value) }
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
    fun showInfoBox(){
        if(infoBoxVisible.value)
        {
            viewModel.setInfoBoxVisible(false)
        } else {
            viewModel.setInfoBoxVisible(true)
        }
    }

    if(inputBoxVisible.value) {
        DialogWithTextField(
            onDismissRequest = {viewModel.setInputVisibility(false)},
            onIDConfirmation = {
                viewModel.updateFileName(it)
            onStartClick()},
            onAddressConfirmation = {viewModel.updateAddress(it)},
            onStudyStartClick = {viewModel.setStudyState(StudyStates.STARTED)},
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
    }
    if(showCameraDialog.value){
        CameraDialog(
            onDismissRequest = {viewModel.setCameraDialogVisibility(false)},
            onOkayClick = {viewModel.setStudyState(StudyStates.DONE)}
        )
    }

    if(infoBoxVisible.value){
        InfoTextBox(
            onDismissRequest = {viewModel.setInfoBoxVisible(false)}
        )
    }

Box() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StudyControlBar(
            viewModel,
            onStartClick = { showInput() },
            onStopClick = {
                onPauseClick()
                saveFileMediaStore(context, stickerList, formattedTime,address.value, fileName.value)
                viewModel.setCameraDialogVisibility(true)
                viewModel.setMode(true)
            },
            onInfoClick = { showInfoBox() },
            onDownloadClick = { downloadBitmap()
            showBitmap()
            viewModel.setStudyState(StudyStates.ON_BACKGROUND)}
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
                modifier = Modifier
            )
        }
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
                        Text("Vielen Dank für Ihre Teilnahme!")
                        Spacer(Modifier.size(16.dp))
                        Image(
                            bitmap = bitmap,
                            contentDescription = "bitmap"
                        )
                        Text("Ihre Daten wurden in Dokumente/Downloads gespeichert.")
                        Text("Ihre Zeichnung wurde in Bilder gespeichert.")
                        Spacer(Modifier.size(4.dp))
                        Button(onClick = {
                            showDialog = false
                            canvasBitmap = null
                        }) {
                            Text("Fertig")
                        }
                    }
                }
            }

        }


    }

fun OutputStream.writeCsv(stickers: List<Sticker>, time: String, address: Array<String>) {
    val writer = bufferedWriter()
    writer.write(""" "Time in ms:"; ${time}""")
    writer.newLine()
    writer.write(""" "Adresse:"; ${address[0]}; ${address[1]}; ${address[2]}; ${address[3]} """)
    writer.newLine()
    writer.write("Used Stickers:")
    writer.newLine()
    writer.write("""Name:"; "Tag:""")
    writer.newLine()
    stickers.forEach {
        writer.write(""""${it.name}"; "${it.tag}"""")
        writer.newLine()
    }
    writer.flush()
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun saveFileMediaStore(context: Context, content: List<Sticker>, time: String, address: Array<String>, fileName: String){
    try {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
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
//                    outputStream.write(content.toByteArray())
                    outputStream.writeCsv(content, time, address)
                }

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING,0)
                resolver.update(myUri, contentValues, null, null)
            } catch (e: Exception){
                e.printStackTrace()
                resolver.delete(myUri, null, null)
            }
        }
        Toast.makeText(context, "Saved csv File to Downloads", Toast.LENGTH_SHORT).show()
    }catch (e: IOException){
        Toast.makeText(context, "Failed to save csv File", Toast.LENGTH_SHORT).show()
    }
}

@SuppressLint("InlinedApi")
private fun saveBitmapMediaStore(context: Context, bitmap: Bitmap, fileName: String){
    val resolver = context.contentResolver
    val imageCollection = MediaStore.Images.Media.getContentUri(
        MediaStore.VOLUME_EXTERNAL_PRIMARY
    )
    val timeInMillis = System.currentTimeMillis()
    try {
        val imageContentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${fileName}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            put(MediaStore.Images.Media.DATE_ADDED, timeInMillis)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }else {
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(directory, fileName)
                put(MediaStore.MediaColumns.DATA, file.absolutePath)
            }
        }
        val imageMediaStoreUri = resolver.insert(imageCollection, imageContentValues)
        imageMediaStoreUri?.let { myUri ->
            try{
                resolver.openOutputStream(myUri)?.use { outputStream ->
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG, 100, outputStream
                    )
                }

                imageContentValues.clear()
                imageContentValues.put(MediaStore.MediaColumns.IS_PENDING,0)
                resolver.update(myUri, imageContentValues, null, null)
            } catch (e: Exception){
                e.printStackTrace()
                resolver.delete(myUri, null, null)
            }
        }
        Toast.makeText(context, "Saved picture to Pictures", Toast.LENGTH_SHORT).show()
    }catch (e: IOException){
        Toast.makeText(context, "Failed to save bitmap", Toast.LENGTH_SHORT).show()
    }
}


