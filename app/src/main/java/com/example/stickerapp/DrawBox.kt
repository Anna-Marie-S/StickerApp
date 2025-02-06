package com.example.stickerapp

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.TransformableState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.math.roundToInt

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
                }
                // here the Canvas
                val paintMode by viewModel.paintMode.collectAsState()

                var scale by remember { mutableStateOf(1f) }
                var rotation by remember { mutableStateOf(0f) }
                var offset by remember { mutableStateOf(Offset.Zero) }
                val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
                    scale *= zoomChange
                    rotation += rotationChange
                    offset += offsetChange
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
                            .size(1000.dp)
                            .border(2.dp, Color.White)
                            .align(Alignment.Center)
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
                                    width = 5f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
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
){
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = isVisible,
//        enter = slideInVertically { with(density) { -40.dp.roundToPx()} }
//        + expandVertically (expandFrom = Alignment.Top)
//        + fadeIn(initialAlpha = 0.3f),
//        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = state)
                .drawWithContent {
                    drawContent()
                    drawCircle(
                        color = Color(0xFFFFFFFF),
                        center = Offset(x = size.width, y = size.height),
                        radius = 50f,
                        blendMode = BlendMode.DstOut
                    )
                }
                .border(2.dp, Color.Blue)
        )
    }
}