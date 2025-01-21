package com.example.stickerapp

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
    fun ControlBar(
    stickerController: StickerController
){
    Button(
        colors = ButtonDefaults.buttonColors(Color.Black),
        onClick = {stickerController.addHouseSticker()},
        modifier = Modifier.padding(3.dp)
            .width(40.dp)
    )
    {
        //black colour
    }
}