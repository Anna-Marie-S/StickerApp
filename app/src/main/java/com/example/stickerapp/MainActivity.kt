package com.example.stickerapp

import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StickerAppTheme {
               DrawingScreen()
            }
        }
    }
}

@Composable
fun DrawingScreen(){
    val stickerController = rememberStickerController()




    Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Gray)
                    .height(500.dp)
                    .width(500.dp)
            ){


                stickerController.imageList.forEach { sticker ->
                    DragRotateBox(
                        stickerController = stickerController,
                        resource = sticker
                    )
                }
            }
        ControlBar(stickerController)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Added Stickers",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                stickerController.imageList.forEach { sticker ->
                    Text(
                        text = sticker.name
                    )
                }
                 val size = stickerController.imageList.size
                Text(
                    text = "You have added $size Stickers"
                )
            }
        }

        }
    }

@Composable
fun DragRotateBox(stickerController: StickerController, resource: StickerWrapper) {


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var rotation by remember { mutableStateOf(0f) }
        var position by remember { mutableStateOf(Offset.Zero) } // offset
        var scale by remember { mutableStateOf(1f) }
        var centroid by remember { mutableStateOf(Offset.Zero) }




        val boxSize = 100.dp
        val handleSize = 20.dp

        var initialTouch = Offset.Zero

        val boxSizePx = with(LocalDensity.current) { boxSize.toPx() }

        val center = Offset(boxSizePx, boxSizePx)


        // Main Box
        Box(
            modifier = Modifier
                .graphicsLayer(
                    rotationZ = rotation,
                    translationX = position.x,
                    translationY = position.y,
                    scaleX = scale,
                    scaleY = scale
                )
                .border(1.dp, Color.Yellow, shape = RoundedCornerShape(15.dp))
                .size(boxSize)
                .pointerInput(Unit) {
                    detectTransformGestures (
                        onGesture = { gestureCentroid, gesturePan, gestureZoom, _ ->
                        position += gesturePan.rotateBy(rotation)*scale
                        scale *=gestureZoom
                        centroid = gestureCentroid
                    }
                    )
                }

        )
        {
            Image(
                painter = painterResource(resource.res),
                contentDescription = resource.toString(),
                modifier = Modifier.fillMaxSize()
            )
            //Delete Handler
            Box(
                modifier = Modifier
                    .size(handleSize)
                    .background(Color.Black)
                    .align(Alignment.BottomStart)
                    .pointerInput(Unit) {
                        detectTapGestures (
                            onTap = { stickerController.removeSticker(resource) }
                        )
                    }
            )
            // Rotation handler
            Box(
                modifier = Modifier
                    .size(handleSize)
                    .background(Color.Red)
                    .align(Alignment.TopCenter)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                initialTouch = offset
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val angle = calculateRotationAngle(center, initialTouch, change.position)
                                rotation += angle
                            }
                        )
                    }
            )

            //Zoom Handler
            Box(
                modifier = Modifier
                    .size(handleSize)
                    .background(Color.Green)
                    .align(Alignment.BottomEnd)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                               scale = (scale * dragAmount.x.toFloat()).coerceIn(1f, 5f)
                            }
                        )
                    }
            )

        }
    }
}

// Generated by ChatGPT! from StackOverflow
fun calculateRotationAngle(pivot: Offset, initialTouch: Offset, currentTouch: Offset): Float {
    val initialVector = initialTouch - pivot
    val currentVector = currentTouch - pivot

    val initialAngle = atan2(initialVector.y, initialVector.x)
    val currentAngle = atan2(currentVector.y, currentVector.x)

    return Math.toDegrees((currentAngle - initialAngle).toDouble()).toFloat()
}

/**
 * Rotates the given offset around the origin by the given angle in degrees.
 *
 * A positive angle indicates a counterclockwise rotation around the right-handed 2D Cartesian
 * coordinate system.
 *
 * See: [Rotation matrix](https://en.wikipedia.org/wiki/Rotation_matrix)
 */
fun Offset.rotateBy(
    angle: Float
): Offset {
    val angleInRadians = ROTATION_CONST * angle
    val newX = x * cos(angleInRadians) - y * sin(angleInRadians)
    val newY = x * sin(angleInRadians) + y * cos(angleInRadians)
    return Offset(newX, newY)
}

internal const val ROTATION_CONST = (Math.PI / 180f).toFloat()