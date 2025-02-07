package com.example.stickerapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
    fun ControlBar(
    stickerController: StickerController,
    drawController: DrawController,
    viewModel: MainViewModel
) {
    var checked by remember { mutableStateOf(false) }
    var sliderVisible by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(Color.Black),
                onClick = { drawController.changeColor(Color.Black) },
                modifier = Modifier.padding(3.dp).width(30.dp)
            )
            {
                //Black Pen
            }
            Button(
                colors = ButtonDefaults.buttonColors(Color.White),
                onClick = { drawController.changeColor(Color.White) },
                modifier = Modifier.padding(3.dp).width(30.dp)
            )
            {
                //Eraser
            }
            Button(
                colors = ButtonDefaults.buttonColors(Color.Green),
                onClick = { sliderVisible = !sliderVisible },
                modifier = Modifier.padding(3.dp).width(30.dp)
            )
            {
                //Slider
            }
            Button(
                colors = ButtonDefaults.buttonColors(),
                onClick = { drawController.unDo() },
                modifier = Modifier.padding(3.dp)
            )
            {
                Text(text = "UnDo")
            }
            Button(
                colors = ButtonDefaults.buttonColors(),
                onClick = { drawController.reDo() },
                modifier = Modifier.padding(3.dp)
            )
            {
                Text(text = "ReDo")
            }
            Button(
                colors = ButtonDefaults.buttonColors(),
                onClick = { drawController.reset() },
                modifier = Modifier.padding(3.dp)
            )
            {
                Text(text = "Reset Canvas")
            }
            Button(
                colors = ButtonDefaults.buttonColors(),
                onClick = { drawController.saveBitmap() },
                modifier = Modifier.padding(3.dp)
            )
            {
                Text(text = "Download")
            }
            Button(
                colors = ButtonDefaults.buttonColors(),
                onClick = {viewModel.addHouseSticker() },
                modifier = Modifier.padding(3.dp)
            )
            {
                Text(text = "House")
            }
            Button(
                colors = ButtonDefaults.buttonColors(),
                onClick = { viewModel.addTacoSticker() },
                modifier = Modifier.padding(3.dp)
            )
            {
                Text(text = "Taco")
            }

            // For changing to TransformMode
            Switch(
                checked = checked, // the initial state of the switch
                onCheckedChange = {
                    viewModel.setMode(it)
                    checked = it
                },
                thumbContent = if (checked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                } else {
                    null
                }
            )
        }
        //Animated Visibility Items
        CustomSlider(sliderVisible, drawController)
    }
}

@Composable
fun CustomSlider(
    isVisible: Boolean,
    drawController: DrawController

){
    AnimatedVisibility(
        visible = isVisible
    ) {
        var sliderPosition by remember { mutableStateOf(0f) }
        Column(
            modifier = Modifier
                .height(100.dp)
                .width(500.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Stroke Width",
                modifier = Modifier.padding(12.dp, 0.dp, 0.dp, 0.dp)
            )
            Slider(
                value = drawController.penSize,
                onValueChange = {sliderPosition = it},
                onValueChangeFinished = {drawController.changeStrokeWidth(sliderPosition)},
                valueRange = 3f..10f
            )

        }
    }
}