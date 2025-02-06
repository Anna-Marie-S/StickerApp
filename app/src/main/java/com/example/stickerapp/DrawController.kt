package com.example.stickerapp

import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.view.drawToBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class DrawController {
    private val _redoPathList = mutableStateListOf<PathWrapper>()
    private val _undoPathList = mutableStateListOf<PathWrapper>()
    internal val pathList: SnapshotStateList<PathWrapper> = _undoPathList

    private val _bitmapGenerators = MutableSharedFlow<Bitmap.Config>(extraBufferCapacity = 1)
    private val bitmapGenerators = _bitmapGenerators.asSharedFlow()

    fun saveBitmap(config: Bitmap.Config = Bitmap.Config.ARGB_8888){
        _bitmapGenerators.tryEmit(config)
    }

   fun trackBitmaps(
       it: View,
       coroutineScope: CoroutineScope,
       onCaptured: (ImageBitmap?, Throwable?) -> Unit
   ) = bitmapGenerators
       .mapNotNull { config -> it.drawBitmapFromView(it.context, config)}
       .onEach { bitmap -> onCaptured(bitmap.asImageBitmap(), null) }
       .catch { error -> onCaptured(null, error) }
       .launchIn(coroutineScope)

    var color by mutableStateOf(Color.Black)
        private set

    fun changeColor(value: Color) {
        color = value
    }

    fun updateLatestPath( newPoint: Offset){
        val index = _undoPathList.lastIndex
        _undoPathList[index].points.add(newPoint)
    }

    fun insertNewPath(newPoint: Offset) {
        val pathWrapper = PathWrapper(
            points = mutableStateListOf(newPoint),
            strokeColor = color
        )
        _undoPathList.add(pathWrapper)
    }

    fun unDo(){
        if(_undoPathList.isNotEmpty()) {
            val last = _undoPathList.last()
            _redoPathList.add(last)
            _undoPathList.remove(last)
        }
    }

    fun reDo(){
        if (_redoPathList.isNotEmpty()){
            val last = _redoPathList.last()
            _undoPathList.add(last)
            _redoPathList.remove(last)
        }
    }

    fun reset(){
        _redoPathList.clear()
        _undoPathList.clear()
    }
}

@Composable
fun rememberDrawController(): DrawController {
    return remember {DrawController()}
}