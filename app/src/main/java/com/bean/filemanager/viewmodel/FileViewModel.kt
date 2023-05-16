package com.bean.filemanager.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import com.bean.filemanager.decimal
import com.bean.filemanager.ui.FileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(): ViewModel() {
    private val _uiState = MutableStateFlow(FileUiState())
    val uiState: StateFlow<FileUiState> = _uiState.asStateFlow()

    init {
        getFileList(FileUiState().file)
    }

    fun getFileList(file: File) {
        _uiState.value = FileUiState(file = file, list = file.listFiles()?.asList())
    }

    fun getFileIcon(file: File, context: Context): Drawable? {
        Log.d(javaClass.name, "getFileList: ${file.name}")
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
        context.packageManager.queryIntentActivities(
            Intent(Intent.ACTION_VIEW).apply {setDataAndType(Uri.fromFile(file), mimeType) },
            PackageManager.MATCH_DEFAULT_ONLY
        ).run {
            Log.d(javaClass.name, "isNotEmpty: ${isNotEmpty()}")
            if (isNotEmpty()){
                return get(0).activityInfo.loadIcon(context.packageManager)
            } else {
                return null
            }
        }
    }

    fun getFileSize(file: File) : String{
        if (file.isFile) {
            file.length().let {
                if (it > 1024) {
                    val kbSize = it/1024.0
                    return if (kbSize > 1024) {
                        val mbSize = kbSize/1024.0
                        "${mbSize.decimal(2)} Mb"
                    } else {
                        "${kbSize.decimal(2)} Kb"
                    }
                } else {
                    "$it byte"
                }
            }
        } else if (file.isDirectory) {
            return "${file.listFiles()?.size.toString()} 項目"

        }
        return ""
    }

}