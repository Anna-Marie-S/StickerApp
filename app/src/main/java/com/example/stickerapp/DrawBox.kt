package com.example.stickerapp

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawBox(
    drawController: DrawController,
    modifier: Modifier = Modifier
) {
    var paintMode by remember { mutableStateOf(true) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    Column(modifier = modifier.fillMaxHeight()) {
        Row() {
            Box(
                modifier = modifier
                    .background(Color.Yellow)
                    .size(200.dp)
                    .transformable(state = state)
            )
            Column() {
                Box(
                    modifier = modifier.background(Color.Black)
                ) {
                    Switch(
                        checked = paintMode,
                        onCheckedChange = { paintMode = it }
                    )
                }
                Text(paintMode.toString())
            }
        }
        Box(modifier = modifier.background(Color.White)){
        Canvas(
            modifier = modifier
                .background(Color.Green)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = rotation,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .fillMaxSize()
//            .pointerInteropFilter {
//                when(it.getToolType(0)) {
//                    MotionEvent.TOOL_TYPE_STYLUS -> {
//                        when(it.actionMasked) {
//                            MotionEvent.ACTION_DOWN -> {
//                                drawController.insertNewPath(Offset(it.x,it.y))
//                            }
//                            MotionEvent.ACTION_MOVE -> {
//                                drawController.updateLatestPath(Offset(it.x, it.y))
//                            }
//                        }
//                    }
//                    MotionEvent.TOOL_TYPE_FINGER-> {println("It finger")}
//                    MotionEvent.TOOL_TYPE_UNKNOWN -> {println("Unkown")}
//                    else -> false
//                }
//                true
//            }
                .pointerInput(drawController.color) {

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
                        width = 5f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }}
    }
}
