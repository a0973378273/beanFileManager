package com.bean.filemanager.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import com.bean.filemanager.ui.FileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(@ApplicationContext val context: Context): ViewModel() {
    private val _uiState = MutableStateFlow(FileUiState())
    val uiState: StateFlow<FileUiState> = _uiState.asStateFlow()

    init {
        getFileList(FileUiState().file)
    }

    fun getFileList(file: File) {
        _uiState.value = FileUiState(file = file, list = file.listFiles().asList())
    }

    fun getFileIcon(file: File) {
        Log.d(javaClass.name, "getFileList")
        val resolveInfoFlags = PackageManager.ResolveInfoFlags.of(PackageManager.)
        context.packageManager.queryIntentActivities(Intent(Intent.ACTION_VIEW), )
        _uiState.value = FileUiState(file = file, list = file.listFiles().asList())
    }
}