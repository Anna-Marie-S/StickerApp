package com.example.stickerapp

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.pow

class MainViewModel : ViewModel() {

    /*
    Study things
     */

    private var _inputVisible = MutableStateFlow(false)
    var inputVisible = _inputVisible.asStateFlow()

    fun setInputVisibility(boolean: Boolean){
        _inputVisible.update { boolean }
    }

    private var _cameraDialogVisible = MutableStateFlow(false)
    var cameraDialogVisible = _cameraDialogVisible.asStateFlow()

    fun setCameraDialogVisibility(boolean: Boolean){
        _cameraDialogVisible.update { boolean }
    }

    private var _infoBoxVisible = MutableStateFlow(false)
    var infoBoxVisible = _infoBoxVisible.asStateFlow()

    fun setInfoBoxVisible(boolean: Boolean){
        _infoBoxVisible.update { boolean }
    }

    private var _studyState = MutableStateFlow(StudyStates.ON_BACKGROUND)
    var studyState = _studyState.asStateFlow()

    fun setStudyState(state: StudyStates){
        _studyState.update { state }
    }

    /*
    Canvas Things
     */
    private var _dragMode = MutableStateFlow(false)
    var dragMode = _dragMode.asStateFlow()

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


    fun setMode(boolean: Boolean){
        _dragMode.update { boolean }
    }

    fun resetCanvas(){
        _canvasScale.update {1f }
        _canvasOffset.update { Offset.Zero }
    }

    /*
    Sticker things
     */

    private val _stickerList = mutableStateListOf<Sticker>()
    val stickerList = _stickerList

    private val _stickerPosition = mutableStateOf(Offset.Zero)
    val stickerPosition = _stickerPosition

    fun setStickerPosition(offset: Offset){
        _stickerPosition.value = offset
    }

    val counter = mutableStateOf(0)


    fun addSticker(name: String, tag: String, image:Int){
        counter.value++
        val item = Sticker(
            id = counter.value,
            name = name,
            tag = tag,
            image = image
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

    private var _address = MutableStateFlow(arrayOf("", "", "", ""))
    var address = _address.asStateFlow()
        private set

    fun updateAddress(input: Array<String>){
        _address.update { input }
    }


}