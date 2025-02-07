package com.example.stickerapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    /*
    Canvas Things
     */
    private var _paintMode = MutableStateFlow(false)
    var paintMode = _paintMode.asStateFlow()

    private var _pointerSize = MutableStateFlow(5f)
    var pointerSize = _pointerSize.asStateFlow()

    fun setMode(boolean: Boolean){
        _paintMode.update { boolean }
        println("VM says paintMode is ${paintMode.value}")
    }

    fun setPointerSize(size: Float){
        _pointerSize.update { size}
        println("VM says paintMode is ${pointerSize.value}")
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