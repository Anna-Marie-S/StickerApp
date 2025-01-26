package com.example.stickerapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
    fun ControlBar(
    stickerController: StickerController,
) {
    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Button(
            colors = ButtonDefaults.buttonColors(),
            onClick = { stickerController.addHouseSticker() },
            modifier = Modifier.padding(3.dp)
        )
        {
            Text(text = "Add House")
        }
        Button(
            colors = ButtonDefaults.buttonColors(),
            onClick = { stickerController.addTacoSticker() },
            modifier = Modifier.padding(3.dp)
        )
        {
            Text(text = "Add Taco")
        }
        Button(
            colors = ButtonDefaults.buttonColors(),
            onClick = { stickerController.clearStickers() },
            modifier = Modifier.padding(3.dp)
        )
        {
            Text(text = "Clear")
        }
        Button(
            colors = ButtonDefaults.buttonColors(),
            onClick = { stickerController.undoSticker()},
            modifier = Modifier.padding(3.dp)
        )
        {
            Text(text = "Undo Sticker")
        }



    }
}