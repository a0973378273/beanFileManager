package com.bean.filemanager.ui

import android.content.Intent
import android.provider.Settings.ACTION_SETTINGS
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bean.filemanager.viewmodel.FileViewModel
import java.io.File

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Column {
        FileActionBar()
        TabTitle()
        FileList()
    }
}

@Composable
fun FileView() {
    Column {
        FileActionBar()
        TabTitle()
        FileList()
    }
}

@Composable
fun FileActionBar(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    Row {
        Box(Modifier.weight(1f)) {
            IconButton(onClick = {
                uiState.file.parentFile?.let {
                    //TODO 防止在root做返回
                    Log.d("test", "FileActionBar: ${it.absoluteFile}")
                    fileViewModel.getFileList(uiState.file.parentFile)
                }
            })
            {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Box {
            IconButton(
                onClick = { context.startActivity(Intent(ACTION_SETTINGS)) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                )
            }
        }
    }
}

@Composable
fun TabTitle(fileViewModel: FileViewModel = viewModel()) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabTitle = listOf(
        "內部儲存空間",
        "SDCard",
    )

    val tabIcon = listOf(
        Icons.Filled.Home,
        Icons.Filled.ShoppingCart,
    )

    TabRow(selectedTabIndex = tabIndex, Modifier.background(Color.Red)) {
        tabTitle.forEachIndexed { index, text ->
            Tab(selected = tabIndex == index,
                onClick = {
                    tabIndex = index
//                    fileViewModel.getFileList()
                    
                          },
                text = { Text(text = text) },
                icon = { Icon(tabIcon[index], text) }
            )
        }
    }
}

@Composable
fun FileList(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
    LazyColumn {
        uiState.list?.let {
            items(it) { item: File ->
                Row {
                    Icon(
                        imageVector = if (item.isDirectory) Icons.Filled.Folder else Icons.Filled.Description,
                        contentDescription = "file",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 20.dp)
                            .align(CenterVertically)
                    )
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp)
                        .clickable { fileViewModel.getFileList(item) }) {
                        Text(
                            text = item.name,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = fileViewModel.getFileSize(item),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                    Divider()
            }
        } ?: run {

        }
    }
}