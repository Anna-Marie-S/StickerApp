package com.example.stickerapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import com.example.stickerapp.Stylus.StylusState
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.CaptureController


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawBox(
    drawController: DrawController,
    viewModel: MainViewModel,
    captureController: CaptureController,
    stylusState: StylusState,
    modifier: Modifier = Modifier
){

    // here the Canvas
    val dragMode by viewModel.dragMode.collectAsState()
    val list = viewModel.stickerList

    val scaleVM = viewModel.canvasScale.collectAsState()
    val rotationVM = viewModel.canvasRotation.collectAsState()
    val offsetVM = viewModel.canvasOffset.collectAsState()
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        viewModel.changeScale(scaleVM.value * zoomChange)
        viewModel.changeOffset(offsetVM.value + offsetChange)
    }
    val addOn = viewModel.canvasAddOn.collectAsState()

    Box(
        Modifier.clipToBounds()
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .capturable(captureController)
            .graphicsLayer(
            scaleX = scaleVM.value,
            scaleY = scaleVM.value,
            rotationZ = rotationVM.value,
            translationX = offsetVM.value.x,
            translationY = offsetVM.value.y
            )) {
            Canvas(
                modifier = modifier
                    .size(300.dp)
                    .requiredWidth(300.dp + addOn.value)
                    .background(if(dragMode){Color.Yellow}else{Color.White}) // better the other way around
                    .clipToBounds()
                    .pointerInteropFilter {
                        viewModel.processMotionEvent(it)
                    }
//                    .pointerInput(Unit) {
//                        detectDragGestures(
//                            onDragStart = { offset ->
//                                drawController.insertNewPath(offset)
//                            }
//                        ) { change, _ ->
//                            val newPoint = change.position
//                            drawController.updateLatestPath(newPoint)
//                        }
//                    }
            ) {
                with(stylusState) {
                    drawPath(
                        path = this.path,
                        color = Color.Gray,
                        style = Stroke(10F)
                    )
                }
//                    drawController.pathList.forEach { pw ->
//                        drawPath(
//                            createPath(pw.points),
//                            color = pw.strokeColor,
//                            alpha = 1f,
//                            style = Stroke(
//                                width = pw.strokeWidth,
//                                cap = StrokeCap.Round,
//                                join = StrokeJoin.Round
//                            )
//                        )
//                    }

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
