package com.bean.filemanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bean.filemanager.intent.FileIntent
import com.bean.filemanager.status.FileUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<intent,state> : ViewModel() {
//    private val intent = Channel<intent>()
//    private val _uiState = MutableStateFlow(state())
//    val uiState: StateFlow<FileUiState> = _uiState.asStateFlow()
//    init {
//        handleIntent()
//    }
//    abstract fun handleIntent() = viewModelScope.launch {
//        intent.consumeAsFlow().collect{
//
//        }
//    }
}