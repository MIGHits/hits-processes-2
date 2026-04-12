package com.example.hits_processes_2.feature.file_attachment.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.OpenableColumns
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.FileProvider
import com.example.hits_processes_2.MainActivity
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.domain.repository.FileAttachmentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FileTransferService : Service() {

    private val repository: FileAttachmentRepository by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPLOAD -> {
                val uris = intent.extractFileUris()
                if (uris.isNullOrEmpty()) {
                    stopSelfResult(startId)
                    return START_NOT_STICKY
                }

                startForegroundWithNotification(
                    notificationId = UPLOAD_NOTIFICATION_ID,
                    notification = buildUploadProgressNotification(
                        progress = 0,
                        currentFileIndex = 0,
                        totalFiles = uris.size,
                    ),
                )
                serviceScope.launch { uploadFiles(uris, startId) }
            }

            ACTION_DOWNLOAD -> {
                val fileId = intent.getStringExtra(EXTRA_FILE_ID)
                if (fileId.isNullOrBlank()) {
                    stopSelfResult(startId)
                    return START_NOT_STICKY
                }

                startForegroundWithNotification(
                    notificationId = DOWNLOAD_NOTIFICATION_ID,
                    notification = buildDownloadProgressNotification(0),
                )
                serviceScope.launch { downloadFile(fileId, startId) }
            }

            else -> stopSelfResult(startId)
        }

        return START_NOT_STICKY
    }

    private suspend fun uploadFiles(
        uris: List<Uri>,
        startId: Int,
    ) {
        val uploadedFiles = mutableListOf<UploadedFileAttachment>()
        var lastNotificationUpdate = 0L

        uris.forEachIndexed { index, uri ->
            val file = FileAttachmentUpload(
                fileName = resolveFileName(uri),
                uriString = uri.toString(),
            )
            val result = repository.uploadFile(file) { progress ->
                val now = System.currentTimeMillis()
                if (now - lastNotificationUpdate >= NOTIFICATION_THROTTLE_MS) {
                    lastNotificationUpdate = now
                    notificationManager.notify(
                        UPLOAD_NOTIFICATION_ID,
                        buildUploadProgressNotification(progress, index, uris.size),
                    )
                    sendBroadcast(
                        Intent(ACTION_UPLOAD_PROGRESS).apply {
                            putExtra(EXTRA_PROGRESS, progress)
                            putExtra(EXTRA_CURRENT_FILE_INDEX, index)
                            putExtra(EXTRA_TOTAL_FILES, uris.size)
                            setPackage(packageName)
                        },
                    )
                }
            }

            result.onSuccess(uploadedFiles::add).onFailure { throwable ->
                notificationManager.notify(
                    UPLOAD_RESULT_NOTIFICATION_ID,
                    buildUploadErrorNotification(throwable.message.orEmpty()),
                )
                sendBroadcast(
                    Intent(ACTION_UPLOAD_FAILED).apply {
                        putExtra(
                            EXTRA_ERROR_MESSAGE,
                            throwable.message ?: getString(R.string.file_attachment_error_upload),
                        )
                        setPackage(packageName)
                    },
                )
                stopSelfResult(startId)
                return
            }
        }

        notificationManager.notify(
            UPLOAD_RESULT_NOTIFICATION_ID,
            buildUploadCompleteNotification(uploadedFiles.size),
        )
        sendBroadcast(
            Intent(ACTION_UPLOAD_COMPLETED).apply {
                putStringArrayListExtra(
                    EXTRA_UPLOADED_FILE_IDS,
                    ArrayList(uploadedFiles.map { it.id }),
                )
                putStringArrayListExtra(
                    EXTRA_UPLOADED_FILE_NAMES,
                    ArrayList(uploadedFiles.map { it.fileName }),
                )
                setPackage(packageName)
            },
        )
        stopSelfResult(startId)
    }

    private suspend fun downloadFile(
        fileId: String,
        startId: Int,
    ) {
        var lastNotificationUpdate = 0L

        val result = repository.downloadFile(fileId, cacheDir) { progress ->
            val now = System.currentTimeMillis()
            if (now - lastNotificationUpdate >= NOTIFICATION_THROTTLE_MS) {
                lastNotificationUpdate = now
                notificationManager.notify(
                    DOWNLOAD_NOTIFICATION_ID,
                    buildDownloadProgressNotification(progress),
                )
                sendBroadcast(
                    Intent(ACTION_DOWNLOAD_PROGRESS).apply {
                        putExtra(EXTRA_PROGRESS, progress)
                        setPackage(packageName)
                    },
                )
            }
        }

        result.onSuccess { downloadedFile ->
            notificationManager.notify(
                DOWNLOAD_RESULT_NOTIFICATION_ID,
                buildDownloadCompleteNotification(downloadedFile.file, downloadedFile.mimeType),
            )
            val fileUri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                downloadedFile.file,
            )
            sendBroadcast(
                Intent(ACTION_DOWNLOAD_COMPLETED).apply {
                    putExtra(EXTRA_DOWNLOADED_FILE_URI, fileUri.toString())
                    putExtra(EXTRA_DOWNLOADED_FILE_MIME, downloadedFile.mimeType)
                    setPackage(packageName)
                },
            )
        }.onFailure { throwable ->
            notificationManager.notify(
                DOWNLOAD_RESULT_NOTIFICATION_ID,
                buildDownloadErrorNotification(throwable.message.orEmpty()),
            )
            sendBroadcast(
                Intent(ACTION_DOWNLOAD_FAILED).apply {
                    putExtra(
                        EXTRA_ERROR_MESSAGE,
                        throwable.message ?: getString(R.string.file_attachment_error_download),
                    )
                    setPackage(packageName)
                },
            )
        }

        stopSelfResult(startId)
    }

    private fun startForegroundWithNotification(
        notificationId: Int,
        notification: Notification,
    ) {
        ServiceCompat.startForeground(
            this,
            notificationId,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            },
        )
    }

    private fun Intent.extractFileUris(): List<Uri>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelableArrayListExtra(EXTRA_FILE_URIS, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            getParcelableArrayListExtra(EXTRA_FILE_URIS)
        }
    }

    private fun resolveFileName(uri: Uri): String {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    return cursor.getString(index)
                }
            }
        }
        return getString(R.string.file_attachment_default_name)
    }

    private fun buildUploadProgressNotification(
        progress: Int,
        currentFileIndex: Int,
        totalFiles: Int,
    ): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setContentTitle(getString(R.string.file_attachment_upload_notification_title))
            .setContentText(
                getString(
                    R.string.file_attachment_upload_notification_progress,
                    currentFileIndex + 1,
                    totalFiles,
                ),
            )
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun buildUploadCompleteNotification(uploadedFilesCount: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_upload_done)
            .setContentTitle(getString(R.string.file_attachment_upload_complete_title))
            .setContentText(
                resources.getQuantityString(
                    R.plurals.file_attachment_upload_complete_text,
                    uploadedFilesCount,
                    uploadedFilesCount,
                ),
            )
            .setAutoCancel(true)
            .build()
    }

    private fun buildUploadErrorNotification(errorMessage: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(getString(R.string.file_attachment_upload_error_title))
            .setContentText(errorMessage.ifBlank { getString(R.string.file_attachment_error_upload) })
            .setAutoCancel(true)
            .build()
    }

    private fun buildDownloadProgressNotification(progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(getString(R.string.file_attachment_download_notification_title))
            .setContentText(getString(R.string.file_attachment_download_notification_progress))
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun buildDownloadCompleteNotification(
        downloadedFile: java.io.File,
        mimeType: String,
    ): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(getString(R.string.file_attachment_download_complete_title))
            .setContentText(downloadedFile.name)
            .setContentIntent(createOpenDownloadedFileIntent(downloadedFile, mimeType))
            .setAutoCancel(true)
            .build()
    }

    private fun buildDownloadErrorNotification(errorMessage: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle(getString(R.string.file_attachment_download_error_title))
            .setContentText(errorMessage.ifBlank { getString(R.string.file_attachment_error_download) })
            .setAutoCancel(true)
            .build()
    }

    private fun createOpenDownloadedFileIntent(
        downloadedFile: java.io.File,
        mimeType: String,
    ): PendingIntent? {
        return runCatching {
            val fileUri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                downloadedFile,
            )
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Intent(this, MainActivity::class.java).apply {
                    action = MainActivity.ACTION_OPEN_DOWNLOADED_FILE
                    putExtra(MainActivity.EXTRA_OPEN_FILE_URI, fileUri.toString())
                    putExtra(MainActivity.EXTRA_OPEN_FILE_MIME, mimeType)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            } else {
                val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        clipData = ClipData.newRawUri("", fileUri)
                    }
                }
                Intent.createChooser(viewIntent, downloadedFile.name).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

            val flags = PendingIntent.FLAG_UPDATE_CURRENT or
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_MUTABLE
                } else {
                    PendingIntent.FLAG_IMMUTABLE
                }
            PendingIntent.getActivity(this, downloadedFile.absolutePath.hashCode(), intent, flags)
        }.getOrNull()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val CHANNEL_ID = "file_transfer_channel"

        const val ACTION_UPLOAD = "com.example.hits_processes_2.file_attachment.action.UPLOAD"
        const val ACTION_DOWNLOAD = "com.example.hits_processes_2.file_attachment.action.DOWNLOAD"
        const val ACTION_UPLOAD_PROGRESS = "com.example.hits_processes_2.file_attachment.action.UPLOAD_PROGRESS"
        const val ACTION_UPLOAD_COMPLETED = "com.example.hits_processes_2.file_attachment.action.UPLOAD_COMPLETED"
        const val ACTION_UPLOAD_FAILED = "com.example.hits_processes_2.file_attachment.action.UPLOAD_FAILED"
        const val ACTION_DOWNLOAD_PROGRESS = "com.example.hits_processes_2.file_attachment.action.DOWNLOAD_PROGRESS"
        const val ACTION_DOWNLOAD_COMPLETED = "com.example.hits_processes_2.file_attachment.action.DOWNLOAD_COMPLETED"
        const val ACTION_DOWNLOAD_FAILED = "com.example.hits_processes_2.file_attachment.action.DOWNLOAD_FAILED"

        const val EXTRA_FILE_URIS = "extra_file_uris"
        const val EXTRA_FILE_ID = "extra_file_id"
        const val EXTRA_PROGRESS = "extra_progress"
        const val EXTRA_CURRENT_FILE_INDEX = "extra_current_file_index"
        const val EXTRA_TOTAL_FILES = "extra_total_files"
        const val EXTRA_ERROR_MESSAGE = "extra_error_message"
        const val EXTRA_UPLOADED_FILE_IDS = "extra_uploaded_file_ids"
        const val EXTRA_UPLOADED_FILE_NAMES = "extra_uploaded_file_names"
        const val EXTRA_DOWNLOADED_FILE_URI = "extra_downloaded_file_uri"
        const val EXTRA_DOWNLOADED_FILE_MIME = "extra_downloaded_file_mime"

        private const val UPLOAD_NOTIFICATION_ID = 1301
        private const val DOWNLOAD_NOTIFICATION_ID = 1302
        private const val UPLOAD_RESULT_NOTIFICATION_ID = 1303
        private const val DOWNLOAD_RESULT_NOTIFICATION_ID = 1304
        private const val NOTIFICATION_THROTTLE_MS = 350L
    }
}
