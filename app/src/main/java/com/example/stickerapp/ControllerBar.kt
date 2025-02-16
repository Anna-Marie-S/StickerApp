package com.example.stickerapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ControllerBar(
    onDownloadClick: () -> Unit,
    onShowClick: () -> Unit
){
    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceAround){
        Button(
            onClick = {onDownloadClick()}
        ){
            Text("Save Bitmap")
        }
        Button(
            {onShowClick()}
        ){
            Text("Show Bitmap")
        }
    }

}