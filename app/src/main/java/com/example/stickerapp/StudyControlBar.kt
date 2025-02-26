package com.example.stickerapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun StudyControlBar(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onInfoClick: () -> Unit,
    onDownloadClick: () -> Unit,
){
    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.Center
    ){
        // Start Study: Clear Canvas, Start Stopwatch, Save Filename
        IconButton(
            onClick = {onStartClick()}
        ){
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Start",
                tint = Color.Black
            )
        }
        // Stop Study: Stop Stopwatch, Open Download Screen, open Drag Mode
        IconButton(
            onClick = {onStopClick()}
        ){
            Icon(
                Icons.Default.Done,
                contentDescription = "Stop",
                tint = Color.Black
            )
        }
        //Info: Display an Info Box with the study's instruction
        IconButton(
            onClick = {onInfoClick()}
        ){
            Icon(
                Icons.Default.Info,
                contentDescription = "Info",
                tint = Color.Black
            )
        }
        // Download
        IconButton(
            onClick = {onDownloadClick()}
        ){
            Icon(
                painterResource(R.drawable.download_24px),
                contentDescription = "Download",
                tint = Color.Black
            )
        }
    }
}