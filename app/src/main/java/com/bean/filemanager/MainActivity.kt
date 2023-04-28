package com.bean.filemanager

import android.os.Bundle
import android.widget.TableRow
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key.Companion.Tab
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Tab
import androidx.compose.ui.tooling.preview.Preview
import com.bean.filemanager.ui.theme.FileManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileManagerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TabTitle()
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FileManagerTheme {
        Greeting("Android")
    }
}

@Composable
fun TabTitle(){
    val context = LocalContext.current
    Tab(text = { "123" },
        icon = { Icons.Default.Home },
        selected = true,
        onClick = { Toast.makeText(context, "123", Toast.LENGTH_LONG).show() }
    )
}