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
    private var _pointerSize = MutableStateFlow(5f)
    var pointerSize = _pointerSize.asStateFlow()

    fun setPointerSize(size: Float){
        _pointerSize.update { size}
        println("VM says paintMode is ${pointerSize.value}")
    }

}