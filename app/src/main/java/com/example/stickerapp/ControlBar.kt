package com.example.stickerapp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    viewModel: MainViewModel,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onResetClick: () -> Unit,

    ) {

        val mode = viewModel.dragMode.collectAsState()
        val properties by rememberUpdatedState(newValue = pathProperties)
        var showStrokeWidthMenu by remember { mutableStateOf(false) }
        var showColorMenu by remember { mutableStateOf(false) }
        var lastColor by remember { mutableStateOf(Color.Black) }
        var eraserMode by remember{ mutableStateOf(false) }
    val default_stroke_width = 7f

        Row(
            modifier = Modifier.padding(12.dp).background(MaterialTheme.colorScheme.background),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pen
            IconButton(
                onClick = {
                    properties.color = lastColor
                properties.strokeWidth = default_stroke_width
                eraserMode = false}
            ) {
                Icon(
                    painterResource(R.drawable.stylus_note_24px),
                    contentDescription = "Pen",
                    tint = if(!eraserMode)lastColor else Color.LightGray,
                    modifier = Modifier
                        .padding(2.dp)
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
                    tint = if(eraserMode)Color.Black else Color.LightGray,
                    modifier = Modifier
                        .padding(2.dp)

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
                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                    onDismiss = {showColorMenu = !showColorMenu},
                    onColorClick = {color: Color ->
                        showColorMenu = !showColorMenu
                        eraserMode = false
                        properties.color = color
                        properties.strokeWidth = default_stroke_width
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
            modifier = Modifier.padding(8.dp).background(MaterialTheme.colorScheme.background)
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
    modifier: Modifier,
    onDismiss: () -> Unit,
    onColorClick: (Color) -> Unit
){
    Dialog(onDismissRequest = onDismiss) {
        ElevatedCard(
            elevation =  CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(24.dp)
        ) {
                Row(modifier = Modifier.padding(12.dp))
                {
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
                text = { Text("Start")},
                leadingIcon = { Image(painterResource(R.drawable.start), contentDescription = "Start") },
                onClick = {
                    viewModel.addSticker("Start", "Organisation", R.drawable.start)
                    expanded = false}
            )
            HorizontalDivider()

            //Second Section - Natur
            DropdownMenuItem(
                text = { Text("Park")},
                leadingIcon = { Image(painterResource(R.drawable.park), contentDescription = "Park") },
                onClick = {
                    viewModel.addSticker("Park", "Nature", R.drawable.park)
                expanded = false}
            )

            DropdownMenuItem(
                text = { Text("Laubwald")},
                leadingIcon = { Image(painterResource(R.drawable.laubwald), contentDescription = "Laubwald") },
                onClick = {
                    viewModel.addSticker("Laubwald", "Natur", R.drawable.laubwald)
                    expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Wasser")},
                leadingIcon = { Image(painterResource(R.drawable.see), contentDescription = "Wasser") },
                onClick = {
                    viewModel.addSticker("Wasser", "Natur", R.drawable.see)
                    expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Sportplatz")},
                leadingIcon = { Image(painterResource(R.drawable.sport), contentDescription = "Sportplatz") },
                onClick = {
                    viewModel.addSticker("Sportplatz", "Natur", R.drawable.sport)
                    expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Spielplatz")},
                leadingIcon = { Image(painterResource(R.drawable.spielplatz), contentDescription = "Spielplatz") },
                onClick = {
                    viewModel.addSticker("Spielplatz", "Natur", R.drawable.spielplatz)
                    expanded = false}
            )
            HorizontalDivider()
            //Third Section - Gebäude
            DropdownMenuItem(
                text = { Text("Wohnhaus")},
                leadingIcon = { Image(painterResource(R.drawable.wohnhaus), contentDescription = "Wohnhaus") },
                onClick = {
                    viewModel.addSticker("Wohnhaus", "Gebäude", R.drawable.wohnhaus)
                    expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Büros")},
                leadingIcon = { Image(painterResource(R.drawable.bueros), contentDescription = "Büros") },
                onClick = {
                    viewModel.addSticker("Büros", "Gebäude", R.drawable.bueros)
                    expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Geschäfte")},
                leadingIcon = { Image(painterResource(R.drawable.geschaeft), contentDescription = "Geschäfte") },
                onClick = {
                    viewModel.addSticker("Geschäfte", "Gebäude", R.drawable.geschaeft)
                    expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Kultureinrichtung")},
                leadingIcon = { Image(painterResource(R.drawable.museum), contentDescription = "Museum") },
                onClick = {
                    viewModel.addSticker("Kultureinrichtung", "Gebäude", R.drawable.museum)
                    expanded = false}
            )
            DropdownMenuItem(
                text = { Text("Schule/Kindergarten")},
                leadingIcon = { Image(painterResource(R.drawable.schule), contentDescription = "Schule") },
                onClick = {
                    viewModel.addSticker("Schule", "Gebäude", R.drawable.schule)
                    expanded = false}
            )
            HorizontalDivider()
            // Fourth Section - Infrastruktur
            DropdownMenuItem(
                text = { Text("Haltestelle")},
                leadingIcon = { Image(painterResource(R.drawable.haltestellen), contentDescription = "Haltestelle") },
                onClick = {
                    viewModel.addSticker("Haltestelle", "Infrastruktur", R.drawable.haltestellen)
                    expanded = false}
            )
        }
    }
}

