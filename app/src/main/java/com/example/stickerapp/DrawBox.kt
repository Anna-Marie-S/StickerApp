package com.example.stickerapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawBox(
    drawController: DrawController,
    viewModel: MainViewModel,
    captureController: CaptureController,
    //bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    modifier: Modifier = Modifier
){

    // here the Canvas
    val dragMode by viewModel.dragMode.collectAsState()
    val list = viewModel.stickerList


    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
                var size by remember { mutableStateOf(10000.dp) }
                fun resetState(){
                    scale = 1f
                    rotation = 0f
                    offset = Offset.Zero
                }
                fun makeBigger(){
                    size += 300.dp
                }

    Box(
        Modifier.clipToBounds()
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .capturable(captureController)
            .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            rotationZ = rotation,
            translationX = offset.x,
            translationY = offset.y
            )) {
            Canvas(
                modifier = modifier
                    .size(size)
                    .background(if(dragMode){Color.Yellow}else{Color.White}) // better the other way around
                    .clipToBounds()
                    .pointerInput(Unit) {

                        detectDragGestures(
                            onDragStart = { offset ->
                                drawController.insertNewPath(offset)
                            }
                        ) { change, _ ->
                            val newPoint = change.position
                            drawController.updateLatestPath(newPoint)
                        }
                    }
            ) {
                    drawController.pathList.forEach { pw ->
                        drawPath(
                            createPath(pw.points),
                            color = pw.strokeColor,
                            alpha = 1f,
                            style = Stroke(
                                width = pw.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                }

            }
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
        Row(modifier = Modifier.padding(12.dp)) {
            Button(onClick = { resetState() }) { Text("Reset Canvas") }
            Button(onClick = { }) { Text("Save") }
        }

        CustomTouchPad(dragMode, state)
    }

            }



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
