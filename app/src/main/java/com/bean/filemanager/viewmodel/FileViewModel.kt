package com.bean.filemanager.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bean.filemanager.extension.decimal
import com.bean.filemanager.extension.getExternalFilePath
import com.bean.filemanager.extension.getInternalFilePath
import com.bean.filemanager.intent.FileIntent
import com.bean.filemanager.status.FileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor() : ViewModel() {
    val intentChannel = Channel<FileIntent>()
    private val _uiState = MutableStateFlow(FileUiState())
    val uiState: StateFlow<FileUiState> = _uiState.asStateFlow()

    init {
        setInternalFileList()
    }

    private fun handleIntent() {

     intentChannel.consumeAsFlow().collect{
         when (it) {
             FileIntent.CopyFile -> TODO()
             FileIntent.CreateFile -> TODO()
             FileIntent.CreateFolder -> TODO()
             FileIntent.DeleteFile -> TODO()
             FileIntent.FileInfo -> TODO()
             FileIntent.MoveFile -> TODO()
             FileIntent.RenameFile -> TODO()
         }
     }
    }

    fun setFileList(file: File) {
        file.listFiles()?.let {
            _uiState.value = FileUiState(file = file, list = it.asList())
        } ?: run {
            _uiState.value = FileUiState(file = file, isRequirePermission = true)
        }
    }

    fun getFileSize(file: File): String {
        if (file.isFile) {
            file.length().let {
                if (it > 1024) {
                    val kbSize = it / 1024.0
                    return if (kbSize > 1024) {
                        val mbSize = kbSize / 1024.0
                        "${mbSize.decimal(2)} Mb"
                    } else {
                        "${kbSize.decimal(2)} Kb"
                    }
                } else {
                    "$it byte"
                }
            }
        } else if (file.isDirectory) {
            file.listFiles()?.let {
                return "${it.size} 項目"
            } ?: run {
                return "- 項目"
            }
        }
        return ""
    }

    fun setInternalFileList() {
        getInternalFilePath().let { path ->
            File(path).apply {
                if (exists())
                    _uiState.value = FileUiState(file = this, list = this.listFiles()?.asList())
                else
                    _uiState.value = FileUiState(file = this, list = null)
            }
        }
    }

    fun setExternalFileList(context: Context) {
        getExternalFilePath(context)?.let { path ->
            File(path).apply {
                if (exists())
                    _uiState.value = FileUiState(file = this, list = this.listFiles()?.asList())
                else
                    _uiState.value = FileUiState(file = this, list = null)
            }
        } ?: run {
            _uiState.value = FileUiState(file = File(""), list = null)
        }
    }

    fun createFile(file: File) {
        Log.d(javaClass.name, "createFile")
        if (file.isDirectory) {
            file.createNewFile()
            _uiState.value = FileUiState(file = file, list = file.listFiles()?.asList())
        } else
            _uiState.value = FileUiState(file = file, list = file.listFiles()?.asList(), errorText = "創建檔案失敗")
        //TODO error handler
    }

    fun createFolder(file: File) {
        Log.d(javaClass.name, "createFolder")
        if (file.isDirectory) {
            file.mkdir()
            _uiState.value = FileUiState(file = file, list = file.listFiles()?.asList())
        } else {
            _uiState.value = FileUiState(file = file, list = file.listFiles()?.asList(), errorText = "創建資料夾失敗")
        }
        //TODO error handler
    }

    fun rename(file: File, newName: String) {
        val newFile = File("${file.parentFile}/$newName")
        file.apply {
            if (exists())
                renameTo(newFile)
        }
    }

    fun delete(file: File) {
        if (file.exists())
            file.delete()
    }
}