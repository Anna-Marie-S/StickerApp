package com.example.stickerapp

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class StickerWrapper(
    @DrawableRes val res: Int,
    var name: String,
    var id: Int,
    var positionX: Float = 400f,
    var positionY: Float = 400f,
    val size: Dp = 100.dp
) {
    @Composable
    fun DraggedImage(
        modifier: Modifier = Modifier.fillMaxSize()
    ) {
        var rotation by remember { mutableStateOf(0f) }
        var position by remember { mutableStateOf(Offset.Zero) } // offset
        var scale by remember { mutableStateOf(1f) }
        var centroid by remember { mutableStateOf(Offset.Zero) }
        val stickerController = rememberStickerController()


        val boxSize = 100.dp
        val handleSize = 20.dp

        var initialTouch = Offset.Zero

        val boxSizePx = with(LocalDensity.current) { boxSize.toPx() }

        val center = Offset(boxSizePx, boxSizePx)

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
                painter = painterResource(res),
                contentDescription = res.toString(),
                modifier = Modifier.fillMaxSize()
            )

            //Delete Handler
            Box(
                modifier = Modifier
                    .size(handleSize)
                    .background(Color.Red)
                    .align(Alignment.BottomStart)
                    .pointerInput(Unit) {
                        detectTapGestures (
                            onTap = { stickerController.removeSticker(this@StickerWrapper)
                            println(id.toString())}
                        )
                    }
            )
        }
    }
}