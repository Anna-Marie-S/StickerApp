package com.example.stickerapp.Stylus

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

data class StylusState(
    var pressure: Float = 0f,
    var orientation: Float = 0F,
    var tilt: Float = 0F,
    var color: Color = Color.Black,
    var strokeWidth: Float = 5F,
    var path: Path = Path()
)