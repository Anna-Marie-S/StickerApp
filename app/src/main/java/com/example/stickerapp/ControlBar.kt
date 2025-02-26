package com.example.stickerapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
    fun ControlBar(
    drawController: DrawController,
    viewModel: MainViewModel
    ) {

        val mode = viewModel.dragMode.collectAsState()

        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            //Black Pen
            IconButton(
                onClick = {
                    viewModel.changePenColor(Color.Green)}
            )  {
                Icon(
                    painterResource(R.drawable.stylus_note_24px),
                    contentDescription = "Pen",
                    tint = Color.Green
                )
            }//Red Pen
            IconButton(
                onClick = {
                    viewModel.changePenColor(Color.Red)}
            )  {
                Icon(
                    painterResource(R.drawable.stylus_note_24px),
                    contentDescription = "Pen",
                    tint = Color.Red
                )
            }

            //Eraser makes Size Choices
            EraserMenu(drawController)
            // Undo Button
            IconButton(
                onClick = {drawController.unDo()}
            )  {
                Icon(
                    painterResource(R.drawable.undo_24px),
                    contentDescription = "Undo",
                    tint = Color.Black
                )
            }
            // ReDo Button
            IconButton(
                onClick = {drawController.reDo()}
            )  {
                Icon(
                    painterResource(R.drawable.redo_24px),
                    contentDescription = "Redo",
                    tint = Color.Black
                )
            }
            //Reset Button
            IconButton(
                onClick = {drawController.reset()}
            )  {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Clear Canvas",
                    tint = Color.Black
                )
            }

            //Sticker DropDownMenu
            StickerMenu(viewModel)

            // For changing to TransformMode
            Switch(
                checked = mode.value, // the initial state of the switch
                onCheckedChange = {
                    viewModel.setMode(it)
                },
                thumbContent = if (mode.value) {
                    {
                        Icon(
                            painterResource(R.drawable.drag_pan_24px),
                            tint = Color.Gray,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                } else {
                    {
                        Icon(
                            painterResource(R.drawable.stylus_note_24px),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                }
            )
        }
}



@Composable
fun EraserMenu(
    drawController: DrawController
){
    var expanded by remember { mutableStateOf(false) }

    Box{
        TextButton(onClick = {expanded = !expanded
        drawController.changeColor(Color.White)}) {
            Row {
                Icon(painterResource(R.drawable.ink_eraser_24px), tint = MaterialTheme.colorScheme.primary, contentDescription = "Eraser")
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Sticker menu")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            //Three Choices of Stroke Width
            DropdownMenuItem(
                text = { Text("Small")},
                leadingIcon = { Icon(painterResource(R.drawable.eraser_size_2_24px), tint = Color.White, contentDescription = null) },
                onClick = {
                    drawController.changeStrokeWidth(10f)
                expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Medium")},
                leadingIcon = { Icon(painterResource(R.drawable.eraser_size_4_24px),tint = Color.White, contentDescription = null) },
                onClick = {
                    drawController.changeStrokeWidth(30f)
                expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Large")},
                leadingIcon = { Icon(painterResource(R.drawable.eraser_size_5_24px),tint = Color.White, contentDescription = null) },
                onClick = {
                    drawController.changeStrokeWidth(50f)
                expanded = false}
            )
        }
    }
}


@Composable
fun StickerMenu(
    viewModel: MainViewModel
){
    var expanded by remember { mutableStateOf(false) }

    Box{
        TextButton(onClick = {expanded = !expanded}) {
            Row {
                Text("Stickers")
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Sticker menu")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            //First Section
            DropdownMenuItem(
                text = { Text("House")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_castle_48), contentDescription = null) },
                onClick = {
                    viewModel.addSticker(R.drawable.icons8_castle_48)
                expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Ghost")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_ghost_64), contentDescription = null) },
                onClick = {
                    viewModel.addSticker(R.drawable.icons8_ghost_64)
                expanded = false}
            )
            HorizontalDivider()

            //Second Section
            DropdownMenuItem(
                text = { Text("Taco")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_taco_64), contentDescription = null) },
                onClick = {
                    viewModel.addSticker(R.drawable.icons8_taco_64)
                expanded = false}
            )
            DropdownMenuItem(
                text = { Text("House")},
                leadingIcon = { Image(painterResource(R.drawable.drag_pan_24px), contentDescription = null) },
                onClick = {}
            )
        }
    }
}

