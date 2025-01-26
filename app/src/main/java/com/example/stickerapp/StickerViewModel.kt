package com.example.stickerapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

class StickerViewModel: ViewModel() {

    var items by mutableStateOf(emptyList<StickerWrapper>())
        private set

    init {
        items = listOf(
            StickerWrapper(R.drawable.icons8_taco_64, "Taco", 0.dp, 0.dp, 100.dp),
            StickerWrapper(R.drawable.icons8_castle_48, "Castle", 0.dp, 0.dp, 100.dp)
        )
    }
}