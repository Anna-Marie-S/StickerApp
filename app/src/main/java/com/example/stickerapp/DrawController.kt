package com.example.stickerapp

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

class DrawController {
    private val _redoPathList = mutableStateListOf<PathWrapper>()
    private val _undoPathList = mutableStateListOf<PathWrapper>()
    internal val pathList: SnapshotStateList<PathWrapper> = _undoPathList

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
}

@Composable
fun rememberDrawController(): DrawController {
    return remember {DrawController()}
}