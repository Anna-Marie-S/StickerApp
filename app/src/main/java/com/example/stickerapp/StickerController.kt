package com.example.stickerapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp

class StickerController(){

    val imageList = mutableStateListOf<StickerWrapper>()


    fun addHouseSticker(){
        val stickerWrapper = StickerWrapper(
            res = R.drawable.icons8_castle_48,
            positionX = 50,
            positionY = 50,
            size = 300f
        )

        imageList.add(stickerWrapper)
    }
}

@Composable
fun rememberStickerController(): StickerController {
    return remember {StickerController()}
}