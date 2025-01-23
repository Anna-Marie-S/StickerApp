package com.example.stickerapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class StickerController(){

    val imageList = mutableStateListOf<StickerWrapper>()


    fun addHouseSticker(){
        val stickerWrapper = StickerWrapper(
            res = R.drawable.icons8_castle_48,
            positionX = 50.dp,
            positionY = 50.dp,
        )


        imageList.add(stickerWrapper)
    }

    fun addTacoSticker(){
        val stickerWrapper = StickerWrapper(
            res = R.drawable.icons8_taco_64,
            positionX = 50.dp,
            positionY = 100.dp,

        )


        imageList.add(stickerWrapper)
    }

    fun drawableToBitmap(context: Context, drawable: Int): ImageBitmap{
        val db = ContextCompat.getDrawable(context, drawable)

        //creating a bitmap and initialising it
        val bm = Bitmap.createBitmap(db!!.intrinsicWidth, db.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val img = bm.asImageBitmap()

        return img
    }

}

@Composable
fun rememberStickerController(): StickerController {
    return remember {StickerController()}
}