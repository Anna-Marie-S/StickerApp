package com.example.stickerapp

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun DrawBox(
    drawController: DrawController,
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier
            .background(Color.White)
            .fillMaxSize()
            .pointerInput(Unit) {

    detectDragGestures(
        onDragStart = { offset ->
            drawController.insertNewPath(offset)
        }
    ) { change, _ ->
        val newPoint = change.position
        drawController.updateLatestPath(newPoint)
    }
}) {
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
}