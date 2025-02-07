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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
            horizontalArrangement = Arrangement.Center
        ) {
            //Black Pen
            IconButton(
                onClick = {drawController.changeColor(Color.Black)}
            )  {
                Icon(
                    Icons.Rounded.Create,
                    contentDescription = "Pen"
                )
            }
            //Eraser makes Slider visible
            IconButton(
                onClick = {drawController.changeColor(Color.White)
                sliderVisible = !sliderVisible}
            )  {
                Icon(
                    Icons.Rounded.Place,
                    contentDescription = "Eraser"
                )
            }
            // Undo Button
            IconButton(
                onClick = {drawController.unDo()}
            )  {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Undo"
                )
            }
            // ReDo Button
            IconButton(
                onClick = {drawController.reDo()}
            )  {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Redo"
                )
            }
            //Reset Button
            IconButton(
                onClick = {drawController.reset()}
            )  {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Clear Canvas"
                )
            }
            //Download Button
            IconButton(
                onClick = {drawController.saveBitmap()}
            )  {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Download"
                )
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