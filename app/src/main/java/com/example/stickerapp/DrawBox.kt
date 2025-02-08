package com.example.stickerapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawBox(
    drawController: DrawController,
    viewModel: MainViewModel,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    modifier: Modifier = Modifier
) = AndroidView(
    factory = {
        ComposeView(it).apply {
            setContent {
                LaunchedEffect(drawController) {
                    drawController.trackBitmaps(this@apply, this, bitmapCallback)
                    drawController.changeBgColor(Color.White)
                }

    // here the Canvas
    val paintMode by viewModel.paintMode.collectAsState()
    val list = viewModel.stickerList

    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }

    Box() {
        Box(modifier = modifier
            .fillMaxSize()
            .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            rotationZ = rotation,
            translationX = offset.x,
            translationY = offset.y
            )) {
            Canvas(
                modifier = modifier
//                    .graphicsLayer(
//                        scaleX = scale,
//                        scaleY = scale,
//                        rotationZ = rotation,
//                        translationX = offset.x,
//                        translationY = offset.y
//                    )
                    .fillMaxSize()
                    .background(if(!paintMode){Color.White}else{Color.Yellow}) // better the other way around
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

        CustomTouchPad(paintMode, state)
    }

            }
        }
    },
    modifier = modifier
)

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
        )
    }
}
