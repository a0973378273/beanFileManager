package com.bean.filemanager.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files.delete
import java.nio.file.Files.exists
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor() : ViewModel() {
    private val intent = Channel<FileIntent>()
    private val _uiState = MutableStateFlow(FileUiState())
    val uiState: StateFlow<FileUiState> = _uiState.asStateFlow()

    init {
        setFileList(File(getInternalFilePath()))
        handleIntent()
    }

    fun sendIntent(fileIntent: FileIntent) = viewModelScope.launch {
        intent.send(fileIntent)
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is FileIntent.CopyFile -> {

                    }
                    is FileIntent.CreateFile -> {
                        createFile(it.folder, "123.txt")
                    }
                    is FileIntent.CreateFolder -> {
                        createFolder(it.folder)
                    }
                    is FileIntent.DeleteFile -> {
                        delete(it.file)
                    }
                    is FileIntent.FileInfo -> {}
                    is FileIntent.MoveFile -> {}
                    is FileIntent.RenameFile -> rename(it.newFile, it.oldFile)
                    is FileIntent.SelectFile -> setFileList(it.file)
                    is FileIntent.SelectExternalStorageFile -> setFileList(
                        File(
                            getExternalFilePath(
                                context = it.context
                            ) ?: ""
                        )
                    )
                    is FileIntent.SelectInternalStorageFile -> setFileList(File(getInternalFilePath()))
                    is FileIntent.SelectList -> setFileList(
                        file = it.file.parentFile,
                        selectFile = it.file
                    )
                }
            }
        }
    }

    private fun setFileList(file: File, selectFile: File? = null, error: String? = null) {
        file.listFiles()?.let {
            arrayListOf<FileUiState.FileData>().apply {
                it.asList().forEach {
                    add(FileUiState.FileData(it, getFileSize(it)))
                }
                _uiState.value = FileUiState(
                    file = file,
                    list = this,
                    listSelected = selectFile,
                    errorText = error
                )
            }
        } ?: run {
            _uiState.value = FileUiState(file = file, isRequirePermission = true)
        }
    }

    private fun getFileSize(file: File): String {
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

    private fun createFile(file: File, fileName: String) {
        Log.d(javaClass.name, "createFile")
        Log.d(javaClass.name, "file: ${file.path}")
        val createdFile = File(file.path, fileName)
        if (file.isDirectory) {
            if (!createdFile.exists())
                runCatching {
                    file.createNewFile()
                    Log.e(javaClass.name, "createNewFile")
                }.onSuccess {
                    setFileList(file)
                }.onFailure {
                    Log.e(javaClass.name, it.stackTraceToString())
                    _uiState.value = FileUiState(file, errorText = "Create file failed")
                }
        } else {
            Log.e(javaClass.name, "not directory")
            _uiState.value = FileUiState(file, errorText = "not File")
        }
    }

    private fun createFolder(folder: File) {
        Log.d(javaClass.name, "createFolder")
        if (folder.isDirectory) {
            runCatching {
                folder.mkdir()
            }.onSuccess {
                setFileList(folder.parentFile)
            }.onFailure {
                _uiState.value = FileUiState(folder, errorText = "Create folder failed")
            }
        } else {
            _uiState.value = FileUiState(folder, errorText = "not folder")
        }
    }

    private fun rename(newFile: File, oldFile: File) {
        oldFile.apply {
            if (exists()) {
                if (renameTo(newFile)) {
                    setFileList(_uiState.value.file)
                } else {
                    setFileList(
                        _uiState.value.file,
                        error = "rename file failed, because it is not renamed"
                    )
                }
            } else {
                setFileList(
                    _uiState.value.file,
                    error = "rename file failed, because it is not exist"
                )
            }
        }
    }

    private fun delete(file: File) {
        if (file.exists()) {
            if (file.delete()) {
                setFileList(_uiState.value.file)
            } else {
                setFileList(
                    _uiState.value.file,
                    error = "delete file failed, because it is not deleted"
                )
            }
        } else {
            setFileList(_uiState.value.file, error = "delete file failed, because it is not exist")
        }
    }
}