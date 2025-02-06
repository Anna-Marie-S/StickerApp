package com.example.stickerapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private var _paintMode = MutableStateFlow(false)
    var paintMode = _paintMode.asStateFlow()

    fun setMode(boolean: Boolean){
        _paintMode.update { boolean }
        println("VM says paintMode is ${paintMode.value}")
    }
}