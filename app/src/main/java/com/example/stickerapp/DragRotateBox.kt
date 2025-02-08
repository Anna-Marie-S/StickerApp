package com.example.stickerapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/*
The Draggable Composable has a main box that takes in an image and also takes in
other boxes as handlers

 */
@Composable
fun DragRotateBox(
        resource: Sticker,
        onDelete: (Sticker) -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            var rotation by remember { mutableStateOf(0f) }
            var scale by remember { mutableStateOf(1f) }
            var centroid by remember { mutableStateOf(Offset.Zero) }

            var position by remember { mutableStateOf(Offset(300f, 300f)) }


            val boxSize = 100.dp
            val handleSize = 20.dp

            var initialTouch = Offset.Zero

            val boxSizePx = with(LocalDensity.current) { boxSize.toPx() }

            val center = Offset(boxSizePx, boxSizePx)
            val d = LocalDensity.current

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
                    .size(boxSize)
                    .clickable(
                        enabled = true,
                        onClick = {onDelete(resource)}
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures(
                            onGesture = { gestureCentroid, gesturePan, _, _ ->
                                position += gesturePan.rotateBy(rotation) * scale
                                //scale *=gestureZoom
                                centroid = gestureCentroid
                            }
                        )
                    }

            )
            {
                Image(
                    painter = painterResource(resource.image),
                    contentDescription = resource.name,
                    modifier = Modifier.fillMaxSize()
                )
                    Text(
                        text = resource.id.toString()
                    )


                //Delete Handler
                Box(
                    modifier = Modifier
                        .size(handleSize)
                        .background(Color.Red)
                        .align(Alignment.BottomStart)
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
                // Rotation handler
                Box(
                    modifier = Modifier
                        .size(handleSize)
                        .background(Color.Yellow)
                        .align(Alignment.TopCenter)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    initialTouch = offset
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val angle = calculateRotationAngle(
                                        center,
                                        initialTouch,
                                        change.position
                                    )
                                    rotation += angle
                                }
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Rotate"
                    )
                }

                // Zoom Handler
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
                {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Zoom",
                    )
                }

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

    internal val ROTATION_CONST = (Math.PI / 180f).toFloat()

