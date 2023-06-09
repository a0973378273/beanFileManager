package com.bean.filemanager

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import com.bean.filemanager.ui.compose.FileView


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FileView()
        }
        getFilePermission()
    }

    private fun getFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            }.run {
                if (!Environment.isExternalStorageManager()) {
                    launch(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
                }
            }

        } else {
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//
//            }.launch(Manifest.permission.)
        }
    }
}
