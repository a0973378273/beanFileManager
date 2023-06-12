package com.bean.filemanager.ui.compose

import android.content.Intent
import android.provider.Settings.ACTION_SETTINGS
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.TextField
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bean.filemanager.R
import com.bean.filemanager.extension.getExternalFilePath
import com.bean.filemanager.extension.getInternalFilePath
import com.bean.filemanager.intent.FileIntent
import com.bean.filemanager.viewmodel.FileViewModel
import kotlinx.coroutines.launch
import java.io.File

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Scaffold(bottomBar = { BottomBar() }) {
        Column {
            FileActionBar()
            TabTitle()
            FilePath()
            FileList()
        }
        selectDialog() {}
    }
}

@Composable
fun FileView() {
    Scaffold(bottomBar = { BottomBar() }) {
        Column {
            FileActionBar()
            TabTitle()
            FilePath()
            FileList()
        }
        selectDialog() {}
    }
}

@Composable
fun FileActionBar(fileViewModel: FileViewModel = viewModel()) {
    val menuItem = arrayOf(R.string.create_file, R.string.create_folder)
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
                menuItem.forEach { textRes ->
                    DropdownMenuItem(text = { Text(stringResource(textRes)) }, onClick = {
                        when (textRes) {
                            R.string.create_file -> fileViewModel.sendIntent(
                                FileIntent.CreateFile(
                                    uiState.file,
                                    "123.txt"
                                )
                            )
                            R.string.create_folder -> fileViewModel.sendIntent(
                                FileIntent.CreateFolder(
                                    uiState.file
                                )
                            )
                        }
                        uiState.errorText?.let {
                            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                        }
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
    val tabTitleRes = listOf(
        R.string.internal,
        R.string.external,
    )

    val tabIcon = listOf(
        Icons.Filled.Home,
        Icons.Filled.ShoppingCart,
    )

    TabRow(selectedTabIndex = tabIndex, Modifier.background(Color.Red)) {
        tabTitleRes.forEachIndexed { index, text ->
            Tab(selected = tabIndex == index,
                onClick = {
                    tabIndex = index
                    when (text) {
                        R.string.internal -> fileViewModel.sendIntent(FileIntent.SelectInternalStorageFile)
                        R.string.external -> fileViewModel.sendIntent(
                            FileIntent.SelectExternalStorageFile(
                                context
                            )
                        )
                    }
                },
                text = { Text(text = stringResource(text)) },
                icon = { Icon(tabIcon[index], stringResource(text)) }
            )
        }
    }
}

@Composable
fun FilePath(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
    if (uiState.file.path.isNotEmpty()) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            Text(text = uiState.file.path, fontSize = 20.sp)
        }
    }
}

@Composable
fun FileList(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
    var selectItemIndex by remember { mutableStateOf(-1) }
    Box(Modifier.fillMaxSize()) {
        uiState.list.let { fileList ->
            if (fileList.isNullOrEmpty()) {
                Box(Modifier.align(Center)) {
                    Text(text = "無項目", fontSize = 20.sp)
                }
            } else {
                LazyColumn {
                    itemsIndexed(fileList) { index, item ->
                        Row(
                            Modifier.background(if (index == selectItemIndex) Color.Gray else Color.White)
                        ) {
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
                                .pointerInput(MutableInteractionSource()) {
                                    detectTapGestures(
                                        onLongPress = {
                                            Log.d(javaClass.name, "onLongPress")
                                            fileViewModel.sendIntent(
                                                FileIntent.SelectList(
                                                    fileList[index].file
                                                )
                                            )
                                            selectItemIndex = index
                                        },
                                        onTap = {
                                            Log.d(javaClass.name, "onTap")
                                            selectItemIndex = -1
                                            fileViewModel.sendIntent(
                                                FileIntent.SelectFile(
                                                    item.file
                                                )
                                            )
                                        })
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
            }
        }
    }
}

@Composable
fun BottomBar(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
    var selectedItem by remember { mutableStateOf(0) }
    val items =
        listOf(R.string.copy, R.string.move, R.string.rename, R.string.delete, R.string.info)
    Log.d("test", "BottomBar: ${uiState.listSelected}")
    uiState.listSelected?.let {
        NavigationBar {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        when (item) {
                            R.string.info -> Icon(
                                Icons.Filled.Info,
                                contentDescription = stringResource(item)
                            )
                            R.string.delete -> Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(item)
                            )
                            R.string.copy -> Icon(
                                Icons.Filled.ContentCopy,
                                contentDescription = stringResource(item)
                            )
                            R.string.move -> Icon(
                                Icons.Filled.ContentCut,
                                contentDescription = stringResource(item)
                            )
                            R.string.rename -> Icon(
                                Icons.Filled.FormatItalic,
                                contentDescription = stringResource(item)
                            )
                        }
                    },
                    label = { Text(stringResource(item)) },
                    selected = selectedItem == index,
                    onClick = {
                        selectedItem = index
                        //TODO view action
                        when (item) {
                            R.string.info -> fileViewModel.sendIntent(FileIntent.FileInfo(File("")))
                            R.string.delete -> uiState.listSelected?.let {
                                fileViewModel.sendIntent(
                                    FileIntent.DeleteFile(it)
                                )
                            }
                            R.string.copy -> {}
//                                fileViewModel.sendIntent(
//                                FileIntent.CopyFile(
//                                    File(""),
//                                    File("")
//                                )
//                            )
                            R.string.move -> fileViewModel.sendIntent(
                                FileIntent.MoveFile(
                                    File(""),
                                    File("")
                                )
                            )
                            R.string.rename -> uiState.listSelected?.let {
                                fileViewModel.sendIntent(
                                    FileIntent.RenameFile(
                                        it,
                                        File("${uiState.file.absoluteFile}/123")
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun selectDialog(content: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        Dialog(onDismissRequest = { openDialog.value = false }) {
            Column (Modifier.background(Color.White)){
                Text(text = "輸入新名稱")
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                )
            }
        }
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun BottomSheet() {
//    val scope = rememberCoroutineScope()
//    val scaffoldState = rememberBottomSheetScaffoldState()
//    BottomSheetScaffold(
//        scaffoldState = scaffoldState,
//        sheetPeekHeight = 128.dp,
//        sheetContent = {
//            Box(
//                Modifier
//                    .fillMaxWidth()
//                    .height(128.dp),
//                contentAlignment = Center
//            ) {
//                Text("Swipe up to expand sheet")
//            }
//            Column(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(64.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Sheet content")
//                Spacer(Modifier.height(20.dp))
//                Button(
//                    onClick = {
//                        scope.launch { scaffoldState.bottomSheetState.partialExpand() }
//                    }
//                ) {
//                    Text("Click to collapse sheet")
//                }
//            }
//        }) {
//
//    }
//}