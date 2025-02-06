package com.example.stickerapp

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

internal fun Activity.checkAndAskPermission(continueNext: () -> Unit) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P && ContextCompat.checkSelfPermission(this,
            permissions[0]) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(this,
            permissions,
            PERMISSION_CODE)
        return
    }
    continueNext()
}

internal fun activityChooser(uri: Uri?) = Intent.createChooser(Intent().apply {
    setDataAndType(uri, "image/*")
    action = Intent.ACTION_VIEW
}, "Select Gallery App")



//writing files to storage via scope and normal manner acc. to Api level
//this is from askshay

internal fun Context.saveImage(bitmap: Bitmap): Uri? {
    val uri: Uri? = null
    val fileName = System.nanoTime().toString() + ".png"
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // where the image is stored (think that is the camera folder)
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
            // flagging that the image is being uploaded, put to 0 when done
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        } else {
            val directory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val file = File(directory, fileName)
            put(MediaStore.MediaColumns.DATA, file.absolutePath)
        }
    }

    val imageMediaStoreUri =
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

    imageMediaStoreUri?.let { myUri ->
        try {
            contentResolver.openOutputStream(myUri)?.let { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            values.clear()
            values.put(
                MediaStore.MediaColumns.IS_PENDING, 0
            )

            contentResolver.update(myUri, values, null, null)

        } catch (e: Exception) {
            e.printStackTrace()
            contentResolver.delete(myUri, null, null)
        }
    }
    return uri
}