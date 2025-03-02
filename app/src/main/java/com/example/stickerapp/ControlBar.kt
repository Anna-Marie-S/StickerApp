package com.example.stickerapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
    fun ControlBar(
    modifier: Modifier,
    pathProperties: PathProperties,
    drawMode: DrawMode,
    viewModel: MainViewModel,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onResetClick: () -> Unit
    ) {

        val mode = viewModel.dragMode.collectAsState()
        val properties by rememberUpdatedState(newValue = pathProperties)
        var showStrokeWidthMenu by remember { mutableStateOf(false) }
        var showColorMenu by remember { mutableStateOf(false) }
        var lastColor by remember { mutableStateOf(Color.Black) }
        var eraserMode by remember{ mutableStateOf(false) }

        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pen
            IconButton(
                onClick = {
                    properties.color = lastColor
                properties.strokeWidth = 10f
                eraserMode = false}
            ) {
                Icon(
                    painterResource(R.drawable.stylus_note_24px),
                    contentDescription = "Pen",
                    tint = lastColor,
                    modifier = Modifier
                        .padding(2.dp)
                        .border(if(eraserMode)0.dp else 2.dp,Color.Black, shape = RoundedCornerShape(3.dp))
                    )
            }
            // Eraser Button
            IconButton(
                onClick = {
                    lastColor = if(properties.color != Color.White) properties.color else lastColor
                    properties.color = Color.White
                properties.strokeWidth = 50f
                eraserMode = true}
            )  {
                Icon(
                    painterResource(R.drawable.ink_eraser_24px),
                    contentDescription = "Eraser",
                    tint = Color.Black,
                    modifier = Modifier
                        .padding(2.dp)
                        .border(if(eraserMode)2.dp else 0.dp,Color.Black, shape = RoundedCornerShape(3.dp))
                )
            }

           // Show Color Menu
            Button(
                onClick = {showColorMenu = true},
                colors = ButtonDefaults.buttonColors(lastColor),
                modifier = Modifier.padding(3.dp).width(30.dp).height(30.dp)
            ) { }

            // Undo Button
            IconButton(
                onClick = {onUndoClick()}
            )  {
                Icon(
                    painterResource(R.drawable.undo_24px),
                    contentDescription = "Undo",
                    tint = Color.Black
                )
            }
            // ReDo Button
            IconButton(
                onClick = {onRedoClick()}
            )  {
                Icon(
                    painterResource(R.drawable.redo_24px),
                    contentDescription = "Redo",
                    tint = Color.Black
                )
            }
            //Reset Button
            IconButton(
                onClick = {onResetClick()}
            )  {
                Icon(
                    Icons.Default.Delete,
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

            if(showStrokeWidthMenu){
                StrokeWidthMenu(
                    pathOption = properties,
                    onDismiss = {showStrokeWidthMenu = !showStrokeWidthMenu}
                )
            }
            if(showColorMenu){
                ColorSelectionMenu(
                    onDismiss = {showColorMenu = !showColorMenu},
                    onColorClick = {color: Color ->
                        showColorMenu = !showColorMenu
                        properties.color = color
                        lastColor = color
                    }
                )
            }
        }
}

@Composable
fun StrokeWidthMenu(pathOption: PathProperties, onDismiss: () -> Unit){
    var strokeWidth by remember { mutableStateOf(pathOption.strokeWidth) }

    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            elevation =  CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Canvas(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    val path = Path()
                    path.moveTo(0f, size.height / 2)
                    path.lineTo(size.width, size.height / 2)

                    drawPath(
                        color = pathOption.color,
                        path = path,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                Text(
                    text = "Stroke Width ${strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        pathOption.strokeWidth = strokeWidth
                    },
                    valueRange = 1f..100f,
                    onValueChangeFinished = {}
                )
            }
        }
    }
}
@Composable
fun ColorSelectionMenu(
    onDismiss: () -> Unit,
    onColorClick: (Color) -> Unit
){
    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            elevation =  CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(24.dp)
        ) {
                Row(){
                    Button(
                        onClick = {onColorClick(Color.Black)},
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier.padding(3.dp).width(40.dp)
                    ) { }
                    Button(
                        onClick = {onColorClick(Color.Red)},
                        colors = ButtonDefaults.buttonColors(Color.Red),
                        modifier = Modifier.padding(3.dp).width(40.dp)
                    ) {}
                    Button(
                        onClick = {onColorClick(Color.Green)},
                        colors = ButtonDefaults.buttonColors(Color.Green),
                        modifier = Modifier.padding(3.dp).width(40.dp)
                    ) { }
                    Button(
                        onClick = {onColorClick(Color.Blue)},
                        colors = ButtonDefaults.buttonColors(Color.Blue),
                        modifier = Modifier.padding(3.dp).width(40.dp)
                    ) { }
                    Button(
                        onClick = {onColorClick(Color.Yellow)},
                        colors = ButtonDefaults.buttonColors(Color.Yellow),
                        modifier = Modifier.padding(3.dp).width(40.dp)
                    ) { }
                }
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
                    viewModel.addSticker("House", "Building", R.drawable.icons8_castle_48)
                expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Ghost")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_ghost_64), contentDescription = null) },
                onClick = {
                    viewModel.addSticker("Ghost", "Nature", R.drawable.icons8_ghost_64)
                expanded = false}
            )
            HorizontalDivider()

            //Second Section
            DropdownMenuItem(
                text = { Text("Taco")},
                leadingIcon = { Image(painterResource(R.drawable.icons8_taco_64), contentDescription = null) },
                onClick = {
                    viewModel.addSticker("Taco", "Nature", R.drawable.icons8_taco_64)
                expanded = false}
            )
        }
    }
}

