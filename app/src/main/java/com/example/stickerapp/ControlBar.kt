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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
    fun ControlBar(
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
                onClick = {
                    drawController.saveBitmap()}
            )  {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Download"
                )
            }
            StickerMenu(viewModel)


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

@Composable
fun StickerMenu(
    viewModel: MainViewModel
){
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        IconButton(onClick = {expanded = !expanded}) {
            Icon(Icons.Default.MoreVert, contentDescription = "Stickermenu")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            //First Section
            DropdownMenuItem(
                text = { Text("House")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_castle_48), contentDescription = null) },
                onClick = {viewModel.addHouseSticker()}
            )
            DropdownMenuItem(
                text = { Text("Ghost")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_ghost_64), contentDescription = null) },
                onClick = {viewModel.addTacoSticker()}
            )
            HorizontalDivider()

            //Second Section
            DropdownMenuItem(
                text = { Text("Taco")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_taco_64), contentDescription = null) },
                onClick = {viewModel.addTacoSticker()}
            )
            DropdownMenuItem(
                text = { Text("House")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_taco_64), contentDescription = null) },
                onClick = {}
            )
        }
    }
}