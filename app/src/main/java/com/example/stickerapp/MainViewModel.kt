package com.example.stickerapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    /*
    Canvas Things
     */
    private var _dragMode = MutableStateFlow(false)
    var dragMode = _dragMode.asStateFlow()

    private var _pointerSize = MutableStateFlow(5f)
    var pointerSize = _pointerSize.asStateFlow()

    private var _canvasScale = MutableStateFlow(1f)
    var canvasScale = _canvasScale.asStateFlow()

    fun changeScale(scale: Float)
    {
        _canvasScale.update { scale }
    }

    private var _canvasRotation = MutableStateFlow(0f)
    var canvasRotation = _canvasRotation.asStateFlow()

    fun changeRotation(rot: Float)
    {
        _canvasRotation.update { rot }
    }

    private var _canvasOffset = MutableStateFlow(Offset.Zero)
    var canvasOffset = _canvasOffset.asStateFlow()

    fun changeOffset(pan : Offset)
    {
        _canvasOffset.update { pan }
    }

    private var _canvasSize = MutableStateFlow(500.dp)
    var canvasSize = _canvasSize.asStateFlow()

    fun setMode(boolean: Boolean){
        _dragMode.update { boolean }
    }

    fun setPointerSize(size: Float){
        _pointerSize.update { size}
        println("VM says paintMode is ${pointerSize.value}")
    }

    fun increaseCanvasSize(){
        var oldSize = _canvasSize.value
        _canvasSize.update { oldSize + 300.dp }
    }



    /*
    Sticker things
     */

    private val _stickerList = mutableStateListOf<Sticker>()
    val stickerList = _stickerList

    val counter = mutableStateOf(0)


    fun addSticker(resource: Int){
        counter.value++
        val item = Sticker(
            id = counter.value,
            name = "House",
            image = resource
        )
        _stickerList.add(item)
    }


    fun deleteSticker(item:Sticker){
        _stickerList.remove(item)
    }

    fun clearSticker(){
        _stickerList.clear()
        counter.value = 0
    }

    /*
    Filename
     */
    private var _fileName = MutableStateFlow("")
    var fileName = _fileName.asStateFlow()
    private set

    fun updateFileName(input: String){
        _fileName.update { input }
    }


}