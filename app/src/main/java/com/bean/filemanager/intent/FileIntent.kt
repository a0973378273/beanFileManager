package com.bean.filemanager.intent

sealed class FileIntent {
    object DeleteFile: FileIntent()
    object CreateFile: FileIntent()
    object CreateFolder: FileIntent()
    object RenameFile: FileIntent()
    object CopyFile: FileIntent()
    object MoveFile: FileIntent()
    object FileInfo: FileIntent()
}
