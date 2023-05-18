package com.bean.filemanager.status

import android.os.Environment
import java.io.File

data class FileUiState(
    val file: File = Environment.getExternalStorageDirectory(),
    val list: List<File>? = listOf(),
    val isRequirePermission: Boolean = false,
    var errorText: String? = null
)
