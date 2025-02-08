package com.example.stickerapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun CapturableDrawBox(
    drawController: DrawController,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
) {
    LaunchedEffect(drawController, backgroundColor) {
        drawController.changeBgColor(backgroundColor)
        //drawController.trackHistory(this, trackHistory)
    }
    Canvas(
        modifier = modifier
            .background(drawController.bgColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        drawController.insertNewPath(offset)
                        drawController.updateLatestPath(offset)
                        drawController.pathList
                    }
                )
            }
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
                    width = pw.strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

    }
}