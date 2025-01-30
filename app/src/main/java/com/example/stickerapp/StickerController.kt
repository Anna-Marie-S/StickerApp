package com.example.stickerapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.Image
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class StickerController(){

    val imageList = mutableStateListOf<StickerWrapper>()
    var id_counter : Int = 0
    val addedSticker = mutableStateListOf<StickerWrapper>()




    fun addHouseSticker(){
        id_counter += 1
        val stickerWrapper = StickerWrapper(
            res = R.drawable.icons8_castle_48,
            id = id_counter,
            name = "House"
        )
        imageList.add(stickerWrapper)
        addedSticker.add(stickerWrapper)
    }

    fun addTacoSticker(){

        id_counter++
        val stickerWrapper = StickerWrapper(
            res = R.drawable.icons8_taco_64,
            id = id_counter,
            name = "Taco"
        )
        imageList.add(stickerWrapper)
        addedSticker.add(stickerWrapper)
    }

    fun clearStickers(){
        imageList.clear()
    }

    fun setPosition(res_id: Int, posX: Float, posY: Float){
        imageList.find { it.id == res_id }?.positionX = posX
        imageList.find { it.id == res_id }?.positionY = posY
    }

    fun removeSticker(res: StickerWrapper){
        imageList.remove(res)
    }

    fun deleteSticker(res_id: Int){

        val toDelete = imageList.find {it.id == res_id}
        if (toDelete != null){
            imageList.remove(toDelete)
        } else {}
    }

    fun undoSticker(){
        val last = imageList.last()
        imageList.remove(last)
    }

    fun returnResource(index: Int): Int{
        return imageList.get(index).res
    }




// not needed right now
    fun drawableToBitmap(context: Context, drawable: Int): ImageBitmap{
        val db = ContextCompat.getDrawable(context, drawable)

        //creating a bitmap and initialising it
        val bm = Bitmap.createBitmap(db!!.intrinsicWidth, db.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val img = bm.asImageBitmap()

        return img
    }

}

@Composable
fun rememberStickerController(): StickerController {
    return remember {StickerController()}
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun <StickerWrapper> DragTarget(
    modifier: Modifier = Modifier, // already initialised we do not need to deal with this anymore
    dataToDrop: StickerWrapper, //this is a sticker
    //viewModel: MainViewModel,
    content: @Composable (()->Unit) // the images or boxes or whatever we want to drag is filled in below
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current
    val currentSticker = dataToDrop

    Box(
        modifier = modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit){
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        //viewModel.startDragging()
                        currentState.dataToDrop = dataToDrop
                        currentState.isDragging = true
                        currentState.dragPosition = currentPosition + it
                        currentState.draggableComposable = content
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = {
                        //viewModel.stopDragging()
                        //currentState.dragOffset = Offset.Zero
                        //currentState.isDragging = false
                    },
                    onDragCancel = {
                        //viewModel.stopDragging()
                        //currentState.dragOffset = Offset.Zero
                        //currentState.isDragging = false
                    }
                )
            }
    ) {
        content()
    }
}


// a class which gives you the state of what is going on at the current moment
internal class DragTargetInfo{
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    // we want to copy the composable
    var draggableComposable by mutableStateOf<(@Composable () ->Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)


}