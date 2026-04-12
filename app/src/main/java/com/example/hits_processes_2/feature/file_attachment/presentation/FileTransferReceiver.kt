package com.example.hits_processes_2.feature.file_attachment.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.service.FileTransferService

@Composable
fun RememberFileTransferReceiver(
    onUploadCompleted: (List<UploadedFileAttachment>) -> Unit = {},
    onUploadFailed: (String) -> Unit = {},
    onUploadProgress: (progress: Int, currentFileIndex: Int, totalFiles: Int) -> Unit = { _, _, _ -> },
    onDownloadCompleted: (Uri, String) -> Unit = { _, _ -> },
    onDownloadFailed: (String) -> Unit = {},
    onDownloadProgress: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val currentOnUploadCompleted = rememberUpdatedState(onUploadCompleted)
    val currentOnUploadFailed = rememberUpdatedState(onUploadFailed)
    val currentOnUploadProgress = rememberUpdatedState(onUploadProgress)
    val currentOnDownloadCompleted = rememberUpdatedState(onDownloadCompleted)
    val currentOnDownloadFailed = rememberUpdatedState(onDownloadFailed)
    val currentOnDownloadProgress = rememberUpdatedState(onDownloadProgress)

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context, intent: Intent) {
                when (intent.action) {
                    FileTransferService.ACTION_UPLOAD_PROGRESS -> {
                        currentOnUploadProgress.value(
                            intent.getIntExtra(FileTransferService.EXTRA_PROGRESS, 0),
                            intent.getIntExtra(FileTransferService.EXTRA_CURRENT_FILE_INDEX, 0),
                            intent.getIntExtra(FileTransferService.EXTRA_TOTAL_FILES, 0),
                        )
                    }

                    FileTransferService.ACTION_UPLOAD_COMPLETED -> {
                        val ids = intent.getStringArrayListExtra(FileTransferService.EXTRA_UPLOADED_FILE_IDS).orEmpty()
                        val names = intent.getStringArrayListExtra(FileTransferService.EXTRA_UPLOADED_FILE_NAMES).orEmpty()
                        currentOnUploadCompleted.value(
                            ids.mapIndexed { index, id ->
                                UploadedFileAttachment(
                                    id = id,
                                    fileName = names.getOrElse(index) { id },
                                )
                            },
                        )
                    }

                    FileTransferService.ACTION_UPLOAD_FAILED -> {
                        currentOnUploadFailed.value(
                            intent.getStringExtra(FileTransferService.EXTRA_ERROR_MESSAGE).orEmpty(),
                        )
                    }

                    FileTransferService.ACTION_DOWNLOAD_PROGRESS -> {
                        currentOnDownloadProgress.value(
                            intent.getIntExtra(FileTransferService.EXTRA_PROGRESS, 0),
                        )
                    }

                    FileTransferService.ACTION_DOWNLOAD_COMPLETED -> {
                        val uri = intent.getStringExtra(FileTransferService.EXTRA_DOWNLOADED_FILE_URI)
                            ?.let(Uri::parse)
                            ?: return
                        val mimeType = intent.getStringExtra(FileTransferService.EXTRA_DOWNLOADED_FILE_MIME)
                            ?: "application/octet-stream"
                        currentOnDownloadCompleted.value(uri, mimeType)
                    }

                    FileTransferService.ACTION_DOWNLOAD_FAILED -> {
                        currentOnDownloadFailed.value(
                            intent.getStringExtra(FileTransferService.EXTRA_ERROR_MESSAGE).orEmpty(),
                        )
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(FileTransferService.ACTION_UPLOAD_PROGRESS)
            addAction(FileTransferService.ACTION_UPLOAD_COMPLETED)
            addAction(FileTransferService.ACTION_UPLOAD_FAILED)
            addAction(FileTransferService.ACTION_DOWNLOAD_PROGRESS)
            addAction(FileTransferService.ACTION_DOWNLOAD_COMPLETED)
            addAction(FileTransferService.ACTION_DOWNLOAD_FAILED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            context.registerReceiver(receiver, filter)
        }

        onDispose {
            runCatching { context.unregisterReceiver(receiver) }
        }
    }
}
