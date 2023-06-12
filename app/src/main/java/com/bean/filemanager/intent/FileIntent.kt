package com.bean.filemanager.intent

import android.content.Context
import java.io.File

sealed class FileIntent {
    class SelectFile(val file: File): FileIntent()
    class SelectList(val file: File): FileIntent()
    object SelectInternalStorageFile : FileIntent()
    class SelectExternalStorageFile(val context: Context): FileIntent()
    class DeleteFile(val file: File): FileIntent()
    class CreateFile(val folder: File, fileName: String): FileIntent()
    class CreateFolder(val folder: File): FileIntent()
    class RenameFile(val oldFile: File, val newFile: File): FileIntent()
    class CopyFile(val file: File, val targetFolder: File): FileIntent()
    class MoveFile(val file: File, val targetFolder: File): FileIntent()
    class FileInfo(val file: File): FileIntent()
}
