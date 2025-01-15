package com.example.stickerapp

import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.stickerapp.ui.theme.StickerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StickerAppTheme {
                DisplayBox( modifier = Modifier
                    .fillMaxSize()
                    .padding(100.dp))
            }
        }
    }
}



@Composable
fun DisplayBox(modifier: Modifier){
   var res by remember  {mutableStateOf(R.drawable.icons8_taco_64) }
    var imageList  = remember {mutableStateListOf<Int>()}
    StickerSelection(res = res, onResChange = {imageList.add(it)})

    Row(modifier = Modifier
        .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center)
    {
        imageList.forEach { Int ->
            Image(
                painter = painterResource(Int),
                contentDescription = res.toString()
            )
        }
    }

}

@Composable
fun StickerSelection(res: Int, onResChange: (Int) -> Unit) {
    var imageList  = remember {mutableStateListOf<Int>()}

    Column(
        modifier = Modifier
            .padding(50.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier,
                onClick = { onResChange(R.drawable.icons8_castle_48) }
            ) {
                Text(text = "House")
            }
            Button(
                modifier = Modifier,
                onClick = { onResChange(R.drawable.icons8_ghost_64) }
            ) {
                Text(text = "Ghost")
            }
            Button(
                modifier = Modifier,
                onClick = { onResChange(R.drawable.icons8_taco_64) }
            ) {
                Text(text = "Taco")
            }
        }


    }
}
