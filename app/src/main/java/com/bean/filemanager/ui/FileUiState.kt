package com.bean.filemanager.ui

import android.os.Environment
import java.io.File

data class FileUiState(
    val file: File = Environment.getExternalStorageDirectory(),
    val list: List<File>? = listOf()
)
