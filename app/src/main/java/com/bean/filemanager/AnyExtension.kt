package com.bean.filemanager

import android.content.Context
import android.os.Environment
import java.io.File

fun Any.getInternalFilePath(): String = Environment.getExternalStorageDirectory().absolutePath
fun Any.getExternalFilePath(context: Context): String? {
    context.getExternalFilesDirs(null)?.let {
        if (it.size > 2)
            return it[1].path
    }
    return null
}
