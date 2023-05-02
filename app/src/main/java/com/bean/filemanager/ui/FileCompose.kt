package com.bean.filemanager.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bean.filemanager.viewmodel.FileViewModel

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Column {
        TabTitle()
        FileList()
    }
}

@Composable
fun FileView() {
    Column {
        TabTitle()
        FileList()
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
                onClick = { tabIndex = index },
                text = { Text(text = text) },
                icon = { Icon(tabIcon[index], text) }
            )
        }
    }
}

@Composable
fun FileList(fileViewModel: FileViewModel = viewModel()) {
    val uiState by fileViewModel.uiState.collectAsState()
    Column {
        uiState.list.forEach { file ->
            Row {
                Image(painter = file, contentDescription = )

            Text(text = file.name,
                fontSize = 20.sp,
                modifier = Modifier
                    .heightIn(30.dp)
                    .clickable {
                        fileViewModel.getFileList(file)
                    }
            )
            }
        }
    }
}