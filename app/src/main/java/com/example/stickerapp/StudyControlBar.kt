package com.example.stickerapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

enum class StudyStates {
    STARTED, DONE, ON_BACKGROUND
}
@Composable
fun StudyControlBar(
    viewModel: MainViewModel,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onInfoClick: () -> Unit,
    onDownloadClick: () -> Unit
){
    val studyState = viewModel.studyState.collectAsState()
    Row(
        modifier = Modifier.padding(12.dp),
        horizontalArrangement = Arrangement.Center
    ){
        // Start Study: Clear Canvas, Start Stopwatch, Save Filename
        IconButton(
            onClick = {
                when(studyState.value){
                    StudyStates.ON_BACKGROUND ->{
                        onStartClick() }
                    StudyStates.STARTED -> {
                        onStopClick() }
                    StudyStates.DONE -> {
                        onDownloadClick() }
                }
                }
        ){
            when(studyState.value){
                StudyStates.ON_BACKGROUND ->{
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.Green
                    )
                }
                StudyStates.STARTED -> {
                    Icon(
                        painterResource(R.drawable.stop_24px),
                        contentDescription = "Stop",
                        tint = Color.Red
                    )
                }
                StudyStates.DONE -> {
                    Icon(
                        painterResource(R.drawable.photo_camera_24px),
                        contentDescription = "Download",
                        tint = Color.Black
                    )
                }
            }


        }
        // Stop Study: Stop Stopwatch, Open Download Screen, open Drag Mode
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
    }
}

@Composable
fun DialogWithTextField(
    onDismissRequest: () -> Unit,
    onIDConfirmation: (String) -> Unit,
    onAdressConfirmation: (String) -> Unit,
    onStudyStartClick:() -> Unit,
    modifier: Modifier
) {

        var textID by remember { mutableStateOf("") }
        var textAdresse by remember { mutableStateOf("") }
        var textEnabled by remember { mutableStateOf(true) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        ElevatedCard(
            elevation =  CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                   .width(700.dp)
                    .background(Color.White)
                    .weight(1f)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ){
            Text(
                text = stringResource(R.string.info_text)
            )
        }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    value = textID,
                    onValueChange = { textID = it },
                    label = { Text("Id") },
                    enabled = textEnabled
                )
                OutlinedTextField(
                    value = textAdresse,
                    onValueChange = { textAdresse = it },
                    label = { Text("Adresse") },
                    enabled = textEnabled
                )
                Button(
                    onClick = {
                        if (textID.isNotEmpty() && textAdresse.isNotEmpty()) {
                            onIDConfirmation(textID)
                            onAdressConfirmation(textAdresse)
                            textEnabled = false
                            onStudyStartClick()
                            onDismissRequest()
                        }
                    }
                ) {
                    Text("Start")
                }

            }
        }
        }
    }

                }


@Composable
fun CameraDialog(
    onDismissRequest: () -> Unit
){
    Dialog(onDismissRequest = {}) {
        ElevatedCard(
            elevation =  CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.camer_text),
                modifier = Modifier.padding(12.dp)
            )
                TextButton(
                    {onDismissRequest()}
                ) {
                    Text("Okay")
                }

        }
    }
}

@Composable
fun InfoTextBox(
    onDismissRequest: () -> Unit
){
    Dialog(onDismissRequest = {}) {
    ElevatedCard(
        elevation =  CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(24.dp)
    ) {
        Column {
            IconButton(
                onClick = {onDismissRequest()}
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
            Text(
                text = stringResource(R.string.info_text),
                modifier = Modifier.padding(12.dp)
            )
        }

    }
}
}
