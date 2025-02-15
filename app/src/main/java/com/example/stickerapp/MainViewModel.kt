package com.example.stickerapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
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


    fun addHouseSticker(){
        counter.value++
        val item = Sticker(
            id = counter.value,
            name = "House",
            image = R.drawable.icons8_castle_48
        )
        _stickerList.add(item)
    }

    fun addTacoSticker(){
        counter.value++
        val item = Sticker(
            id = counter.value,
            name = "Taco",
            image = R.drawable.icons8_taco_64
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


}