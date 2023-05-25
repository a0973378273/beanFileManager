package com.bean.filemanager.ui.compose

import android.content.Intent
import android.provider.Settings.ACTION_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bean.filemanager.extension.getExternalFilePath
import com.bean.filemanager.extension.getInternalFilePath
import com.bean.filemanager.intent.FileIntent
import com.bean.filemanager.viewmodel.FileViewModel

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Column {
        FileActionBar()
        TabTitle()
        FilePath()
        FileList()
    }
}

@Composable
fun FileView() {
    Column {
        FileActionBar()
        TabTitle()
        FilePath()
        FileList()
    }
}

@Composable
fun FileActionBar(fileViewModel: FileViewModel = viewModel()) {
    val menuItem = arrayOf("新增檔案","新增資料夾")
    val uiState by fileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var expande by remember { mutableStateOf(false) }
    Row {
        Box(Modifier.weight(1f)) {
            IconButton(onClick = {
                uiState.file.run {
                    if (path.equals(getInternalFilePath())
                        or path.equals(getExternalFilePath(context))
                        or (parentFile == null)
                    ) {
                        Log.w(javaClass.name, "root folder when click back button")
                    } else {
                        Log.d(javaClass.name, "parent file path: ${parentFile.absoluteFile}")
                        fileViewModel.sendIntent(FileIntent.SelectFile(parentFile))
                    }
                }
            })
            {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Box {
            IconButton(onClick = { expande = true }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
            }
            DropdownMenu(expanded = expande, onDismissRequest = { expande = false }) {
                menuItem.forEach { text ->
                    DropdownMenuItem(text = { Text(text) }, onClick = {
                        when (text) {
                            "新增檔案" -> fileViewModel.sendIntent(FileIntent.CreateFile(uiState.file))
                            "新增資料夾" -> fileViewModel.sendIntent(FileIntent.CreateFolder(uiState.file))
                        }
                        uiState.errorText?.let { Toast.makeText(context,it,Toast.LENGTH_LONG).show() }
                        expande = false
                    })
                }
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
    val context = LocalContext.current
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
                    when (index) {
                        0 -> fileViewModel.sendIntent(FileIntent.SelectInternalStorageFile)
                        1 -> fileViewModel.sendIntent(FileIntent.SelectExternalStorageFile(context))
                    }
                },
                text = { Text(text = text) },
                icon = { Icon(tabIcon[index], text) }
            )
        }
    }
}

@Composable
fun FilePath(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
    uiState.file.absoluteFile
    //不顯示 root path
}

@Composable
fun FileList(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
//    var showDialog by remember { mutableStateOf(false) }
    val longClickMenuItem = arrayOf("修改名稱")
    var expande by remember { mutableStateOf(false) }
    uiState.list?.let {
        LazyColumn {
            items(it) { item ->
                Row {
                    Icon(
                        imageVector = if (item.file.isDirectory) Icons.Filled.Folder else Icons.Filled.Description,
                        contentDescription = "file",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(start = 20.dp)
                            .align(CenterVertically)
                    )
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
//                                    showDialog = true
                                              },
                                onTap = { fileViewModel.sendIntent(FileIntent.SelectFile(item.file)) })
                        }
                    ) {
                        Text(
                            text = item.file.name,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = item.item,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Divider()
            }
        }
    } ?: run {
        Box(Modifier.fillMaxSize(), Center) {
            Text(text = "無項目", fontSize = 20.sp)
        }
    }
}

@Composable
fun ShowAddMenu() {

}

@Composable
fun ShowLongClickMenu(fileViewModel: FileViewModel = viewModel()) {
    Log.d("test", "ShowLongClickMenu")
    val uiState by fileViewModel.uiState.collectAsState()
    val longClickMenuItem = arrayOf("修改名稱")
    var expande by remember { mutableStateOf(true) }
    DropdownMenu(expanded = expande, onDismissRequest = { expande = false }) {
        longClickMenuItem.forEach { text ->
            DropdownMenuItem(text = { Text(text) }, onClick = {
                when (text) {
                    "修改名稱" -> {}
                }
                expande = false
            })
        }
    }
}