package com.example.stickerapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/*
The Draggable Composable has a main box that takes in an image and also takes in
other boxes as handlers

 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DragRotateBox(
        resource: Sticker,
        onDelete: (Sticker) -> Unit,
        viewModel: MainViewModel,
        stickerPos: Offset,
        modifier: Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val boxSize = 100.dp
        val handleSize = 20.dp
        var rotation by remember { mutableStateOf(0f) }
        var scale by remember { mutableStateOf(1f) }
        var centroid by remember { mutableStateOf(Offset.Zero) }
        val boxSizePx = with(LocalDensity.current) { boxSize.toPx() }
        val center = Offset(boxSizePx, boxSizePx)
        val startPos = viewModel.stickerPosition.value


        var position by remember { mutableStateOf(stickerPos) }

        var selected by remember { mutableStateOf(false) }

        // Main Border Box
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            var rotation by remember { mutableStateOf(0f) }
            var scale by remember { mutableStateOf(1f) }
            var centroid by remember { mutableStateOf(Offset.Zero) }
            var position by remember { mutableStateOf(Offset(300f, 300f)) }


            val boxSize = 100.dp
            val handleSize = 20.dp

            var selected by remember { mutableStateOf(false) }


            // Main Border Box
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(resource.image),
                    contentDescription = resource.name,
                    modifier = Modifier.fillMaxSize()
                )

            }


            AnimatedVisibility(
                visible = selected,
                enter = scaleIn(),
                exit = scaleOut()
            ) {Box(
                modifier = Modifier.fillMaxSize()
            ) {
                //Delete Handler
                Box(
                    modifier = Modifier
                        .size(handleSize)
                        .clip(CircleShape)
                        .background(Color.Red)
                        .align(Alignment.TopStart)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { onDelete(resource) }
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete"
                    )
                }

                // To unselect
                Box(
                    modifier = Modifier
                        .size(handleSize)
                        .clip(CircleShape)
                        .background(Color.Cyan)
                        .align(Alignment.TopEnd)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { selected = false }
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close Menu"
                    )

                }
            }
            }
        }
    }
}
                        // Rotation handler
//                        Box(
//                            modifier = Modifier
//                                .size(handleSize)
//                                .clip(CircleShape)
//                                .background(Color.Yellow)
//                                .align(Alignment.BottomEnd)
//                                .pointerInput(Unit) {
//                                    detectDragGestures(
//                                        onDragStart = { offset ->
//                                            initialTouch = offset
//                                        },
//                                        onDrag = { change, _ ->
//                                            change.consume()
//                                            val angle = calculateRotationAngle(
//                                                center,
//                                                initialTouch,
//                                                change.position
//                                            )
//                                            rotation += angle
//                                        }
//                                    )
//                                }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Filled.Refresh,
//                                contentDescription = "Rotate"
//                            )
//                        }

                        // Zoom Handler
//                        Box(
//                            modifier = Modifier
//                                .size(handleSize)
//                                .clip(CircleShape)
//                                .background(Color.Green)
//                                .align(Alignment.BottomStart)
//                                .pointerInput(Unit) {
//                                    detectDragGestures(
//                                        onDrag = { change, dragAmount ->
//                                            change.consume()
//                                            scale =
//                                                (scale * dragAmount.x).coerceAtMost(5f)
//                                        }
//                                    )
//                                }
//                        )
//                        {
//                            Icon(
//                                imageVector = Icons.Filled.Settings,
//                                contentDescription = "Zoom",
//                            )
//                        }







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

    internal val ROTATION_CONST = (Math.PI / 180f).toFloat()

