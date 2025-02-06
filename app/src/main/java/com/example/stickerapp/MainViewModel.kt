package com.example.stickerapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
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


    val active = mutableStateOf(false)
    val count = mutableStateOf(0)
    val selectedItems = mutableStateListOf<Sticker>()
    val itemList = mutableStateListOf<Sticker>().apply { addAll(stickers) }


    fun clearSelection(){
        selectedItems.clear()
        count.value = 0
    }

    fun addSticker(item: Sticker){
        itemList.add(item)
    }

    fun overridePosition(item: Sticker, x: Float, y: Float){
        val index = itemList.indexOf(item)
        itemList[index].posX = x
        itemList[index].posY = y
    }
    fun deleteSticker(item:Sticker){
        itemList.remove(item)
    }


    fun removeSelectedItems(){
        itemList.removeAll(selectedItems)
        clearSelection()
    }

    fun toggleSelection(item: Sticker){
        if (selectedItems.contains(item)){
            selectedItems.remove(item)
            count.value = 0
        } else {
            selectedItems.add(item)
            count.value += 1
        }
    }

}