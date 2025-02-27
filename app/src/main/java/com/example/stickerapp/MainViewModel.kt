package com.example.stickerapp

import android.os.Build
import android.view.MotionEvent
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class MainViewModel : ViewModel() {

    private var _inputVisible = MutableStateFlow(false)
    var inputVisible = _inputVisible.asStateFlow()

    fun setInputVisibility(boolean: Boolean){
        _inputVisible.update { boolean }
    }

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

    private var _canvasAddOn = MutableStateFlow(100.dp)
    var canvasAddOn = _canvasAddOn.asStateFlow()

    fun setMode(boolean: Boolean){
        _dragMode.update { boolean }
    }

    fun setPointerSize(size: Float){
        _pointerSize.update { size}
        println("VM says paintMode is ${pointerSize.value}")
    }

    fun increaseCanvasSize(){
        var oldSize = _canvasAddOn.value
        _canvasAddOn.update { oldSize + 100.dp }
    }

    fun resetCanvas(){
        _canvasScale.update { calculateResetScale() }
    }

    private fun calculateResetScale(): Float{
        var startingSize = 300f
        var base = _canvasScale.value + startingSize
        var newSize = base.pow(2)
        var oldSize = startingSize.pow(2)
        var newScale = newSize / oldSize
        println("started with $startingSize, then went to $base now the scale is $newScale")
        return newScale
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

    /*
    Stylus Stuff
     */




    private var _penColor = MutableStateFlow(Color.Black)
    val penColor = _penColor.asStateFlow()


    fun changePenColor(color: Color){
        _penColor.update { color }
    }



}