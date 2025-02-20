package com.example.stickerapp

import android.R.attr.text
import android.R.attr.value
import android.graphics.Color
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LogInScreen(){
    var text by remember {mutableStateOf("")}

    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Box(modifier = Modifier
                .padding(20.dp,0.dp, 20.dp, 0.dp)) {
                Text(
                    text = stringResource(R.string.info_text),
                    fontSize = 19.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Label") }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {}
            ){
                Text("Start")
            }
        }
    }
}

@Preview(backgroundColor = 0x000000)
@Composable
fun LogInPreview(){
    LogInScreen()
}