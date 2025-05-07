package com.example.stickerapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawBox(
    viewModel: MainViewModel,
    captureController: CaptureController,
    modifier: Modifier = Modifier
) {

    /**
     * Canvas touch state. [MotionEvent.Idle] by default, [MotionEvent.Down] at first contact,
     * [MotionEvent.Move] while dragging and [MotionEvent.Up] when first pointer is up
     */
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

    /**
     * Current position of the pointer that is pressed or being moved
     */
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Previous motion event before next touch is saved into this current position.
     */
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

    /**
     * Draw mode, erase mode or touch mode to
     */
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }

    /**
     * Path that is being drawn between [MotionEvent.Down] and [MotionEvent.Up]. When
     * pointer is up this path is saved to **paths** and new instance is created
     */
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }

    val pathsVM = viewModel.paths
    // here the Canvas
    val dragMode by viewModel.dragMode.collectAsState()
    val list = viewModel.stickerList

    val scaleVM = viewModel.canvasScale.collectAsState()
    val rotationVM = viewModel.canvasRotation.collectAsState()
    val offsetVM = viewModel.canvasOffset.collectAsState()

    var stickerPosition by remember { mutableStateOf(Offset.Zero) }
    stickerPosition = Offset.Zero - offsetVM.value

    /*
    We collect the state with the CustomTouchPad Composable and call the vm functions to change it
     */
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        viewModel.changeScale(scaleVM.value * zoomChange)
        viewModel.changeOffset(offsetVM.value + offsetChange)
    }

    fun clearCanvas(){
        viewModel.clearSticker()
        viewModel.resetCanvas()
        viewModel.clearPathsUndone()
        viewModel.clearPaths()
    }


    Column(
        modifier = Modifier.padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ControlBar(
            modifier = Modifier,
            pathProperties = currentPathProperty,
            viewModel = viewModel,
            onUndoClick = {viewModel.unDoPath()},
            onRedoClick = {viewModel.reDoPath()},
            onResetClick = {clearCanvas()}

        )
        Box(
            Modifier.clipToBounds()


        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .capturable(captureController)
                    .graphicsLayer(
                        scaleX = scaleVM.value,
                        scaleY = scaleVM.value,
                        rotationZ = rotationVM.value,
                        translationX = offsetVM.value.x,
                        translationY = offsetVM.value.y
                    )
            ) {
                Canvas(
                    modifier = modifier
                        .size(300.dp)
                        .requiredSize(10000.dp)
                        .background(Color.White)
                        .clipToBounds()
                        .dragMotionEvent(
                            onDragStart = { pointerInputChange ->
                                motionEvent = MotionEvent.Down
                                currentPosition = pointerInputChange.position
                                viewModel.setStickerPosition(pointerInputChange.position)
                                pointerInputChange.consume()

                            },
                            onDrag = { pointerInputChange ->
                                motionEvent = MotionEvent.Move
                                currentPosition = pointerInputChange.position
                                pointerInputChange.consume()

                            },
                            onDragEnd = { pointerInputChange ->
                                motionEvent = MotionEvent.Up
                                viewModel.setStickerPosition(pointerInputChange.position)
                                pointerInputChange.consume()
                            }
                        )
                ) {
                    when (motionEvent) {

                        MotionEvent.Down -> {
                                currentPath.moveTo(currentPosition.x, currentPosition.y)
                            previousPosition = currentPosition

                        }

                        MotionEvent.Move -> {

                                currentPath.quadraticTo(
                                    previousPosition.x,
                                    previousPosition.y,
                                    (previousPosition.x + currentPosition.x) / 2,
                                    (previousPosition.y + currentPosition.y) / 2
                                )


                            previousPosition = currentPosition
                        }

                        MotionEvent.Up -> {
                                currentPath.lineTo(currentPosition.x, currentPosition.y)
                                //paths.add(Pair(currentPath, currentPathProperty))
                            viewModel.addPath(currentPath, currentPathProperty)
                                currentPath = Path()

                                currentPathProperty = PathProperties(
                                    strokeWidth = currentPathProperty.strokeWidth,
                                    color = currentPathProperty.color,
                                    strokeCap = currentPathProperty.strokeCap,
                                    strokeJoin = currentPathProperty.strokeJoin,
                                )


                            // Since new path is drawn no need to store paths to undone
                            viewModel.clearPathsUndone()

                            // If we leave this state at MotionEvent.Up it causes current path to draw
                            // line from (0,0) if this composable recomposes when draw mode is changed
                            currentPosition = Offset.Unspecified
                            previousPosition = currentPosition
                            motionEvent = MotionEvent.Idle
                        }

                        else -> Unit
                    }

                    with(drawContext.canvas) {

                        pathsVM.forEach {

                            val path = it.first
                            val property = it.second

                                drawPath(
                                    color = property.color,
                                    path = path,
                                    style = Stroke(
                                        width = property.strokeWidth,
                                        cap = property.strokeCap,
                                        join = property.strokeJoin
                                    )
                                )

                        }

                        if (motionEvent != MotionEvent.Idle) {

                                drawPath(
                                    color = currentPathProperty.color,
                                    path = currentPath,
                                    style = Stroke(
                                        width = currentPathProperty.strokeWidth,
                                        cap = currentPathProperty.strokeCap,
                                        join = currentPathProperty.strokeJoin
                                    )
                                )

                        }
                    }
                }

                /*
                To show the sticker on the Canvas.
                 */
                list.forEach { sticker ->
                    key(
                        sticker.id
                    ) {
                        DragRotateBox(
                            resource = sticker,
                            onDelete = viewModel::deleteSticker,
                            viewModel,
                            stickerPos = stickerPosition,
                            modifier = Modifier
                        )
                    }
                }
            }

            CustomTouchPad(dragMode, state)
        }

    }
}

/*
Collects the state so that we can move and and scale the canvas.
 */

@Composable
fun CustomTouchPad(
    isVisible: Boolean,
    state: TransformableState
) {
    AnimatedVisibility(
        visible = isVisible
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = state)
                .border(2.dp, Color.Blue)
        ){
        }
    }
}
