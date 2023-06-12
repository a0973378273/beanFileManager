package com.bean.filemanager.status

import android.os.Environment
import java.io.File

data class FileUiState(
    val file: File = Environment.getExternalStorageDirectory(),
    val list: List<FileData>? = listOf(),
    val listSelected: File? = null,
    val isRequirePermission: Boolean = false,
    var errorText: String? = null
) {
    data class FileData(val file: File, val item: String)
}
