package com.example.stickerapp

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.geometry.Offset

data class StickerWrapper(
    @DrawableRes val res: Int,
    val positionX: Int,
    val positionY: Int,
    val size: Float
)