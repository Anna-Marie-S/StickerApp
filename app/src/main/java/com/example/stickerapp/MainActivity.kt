package com.example.stickerapp

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stickerapp.ui.theme.StickerAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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

@Composable
fun DragBox(
    stickerController: StickerController,
    sticker: StickerWrapper
){
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var orgPosX by remember { mutableStateOf(sticker.positionX) }
    var orgPosY by remember { mutableStateOf(sticker.positionY) }

    var position by remember {
        mutableStateOf(
            Offset(
                sticker.positionX,
                sticker.positionY
            )
        )
    }


    Box(
        modifier = Modifier
            //.offset((positionX/d.density).dp, (positionY/d.density).dp)
            .graphicsLayer(
                translationX = position.x,
                translationY =position.y
            )
            .size(100.dp)
            .clickable(
                enabled = true,
                onClick = {stickerController.removeSticker(sticker)}
            )
            .pointerInput(Unit) {
                detectDragGestures (
                    onDrag = {change, dragAmount ->
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {stickerController.setPosition(sticker.id, orgPosX + offsetX, orgPosY + offsetY)}
                )
            }


    )
    {
        Image(
            painter = painterResource(sticker.res),
            contentDescription = sticker.name,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = sticker.id.toString()
        )
    }
}

