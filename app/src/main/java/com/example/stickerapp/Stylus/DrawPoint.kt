package com.example.stickerapp.Stylus

import android.graphics.PointF

class DrawPoint(
    x: Float,
    y: Float,
    val type: DrawPointType
): PointF(x,y)

enum class DrawPointType {
    START, LINE
}