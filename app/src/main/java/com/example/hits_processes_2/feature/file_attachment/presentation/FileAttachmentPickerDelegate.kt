package com.example.hits_processes_2.feature.file_attachment.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

@Composable
fun rememberFileAttachmentPicker(
    onFilesPicked: (List<SelectedFileAttachment>) -> Unit,
    onPermissionDenied: (String) -> Unit = {},
): FileAttachmentPickerState {
    val context = LocalContext.current
    var pendingAction by remember { mutableStateOf<PendingPickerAction?>(null) }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        onFilesPicked(uris.mapToSelectedFiles(context))
    }

    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris ->
        onFilesPicked(uris.mapToSelectedFiles(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        if (result.values.all { it }) {
            when (pendingAction) {
                PendingPickerAction.Gallery -> galleryPickerLauncher.launch(IMAGE_MIME_TYPE)
                null -> Unit
            }
        } else {
            onPermissionDenied(context.getString(R.string.file_attachment_permission_denied))
        }
        pendingAction = null
    }

    return remember {
        object : FileAttachmentPickerState {
            override fun launchDocuments() {
                documentPickerLauncher.launch(ALLOWED_DOCUMENT_MIME_TYPES)
            }

            override fun launchGallery() {
                if (requiredGalleryPermissions().all { permission ->
                        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    }
                ) {
                    galleryPickerLauncher.launch(IMAGE_MIME_TYPE)
                } else {
                    pendingAction = PendingPickerAction.Gallery
                    permissionLauncher.launch(requiredGalleryPermissions())
                }
            }
        }
    }
}

interface FileAttachmentPickerState {
    fun launchDocuments()
    fun launchGallery()
}

private fun List<Uri>.mapToSelectedFiles(context: Context): List<SelectedFileAttachment> {
    return distinctBy { it.toString() }
        .map { uri ->
            context.contentResolver.takePersistableReadPermissionIfAvailable(uri)
            SelectedFileAttachment(
                name = context.contentResolver.resolveFileName(uri)
                    ?: context.getString(R.string.file_attachment_default_name),
                uriString = uri.toString(),
            )
        }
}

private fun android.content.ContentResolver.resolveFileName(uri: Uri): String? {
    query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                return cursor.getString(nameIndex)
            }
        }
    }
    return null
}

private fun android.content.ContentResolver.takePersistableReadPermissionIfAvailable(uri: Uri) {
    runCatching {
        takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}

private fun requiredGalleryPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

private enum class PendingPickerAction {
    Gallery,
}

private val ALLOWED_DOCUMENT_MIME_TYPES = arrayOf(
    "application/pdf",
    "text/plain",
    "image/png",
    "image/jpeg",
    "image/webp",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
)

private const val IMAGE_MIME_TYPE = "image/*"
