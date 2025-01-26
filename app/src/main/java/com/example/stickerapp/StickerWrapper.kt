package com.example.stickerapp

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class StickerWrapper(
    @DrawableRes val res: Int,
    var name: String,
    var positionX: Dp = 0.dp,
    var positionY: Dp = 0.dp,
    val size: Dp = 100.dp
){
    val setPositionX = StickerWrapper::positionX::set
    val setPositionY = StickerWrapper::positionY::set
}