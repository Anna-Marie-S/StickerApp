package com.example.stickerapp.Stylus

import androidx.compose.ui.graphics.Path

data class StylusState(
    var pressure: Float = 0f,
    var orientation: Float = 0F,
    var tilt: Float = 0F,
    var path: Path = Path()
)