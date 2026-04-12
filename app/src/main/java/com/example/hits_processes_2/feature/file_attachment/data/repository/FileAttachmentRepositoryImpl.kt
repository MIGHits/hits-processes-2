package com.example.hits_processes_2.feature.file_attachment.data.repository

import android.content.Context
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.file_attachment.data.remote.FileAttachmentApi
import com.example.hits_processes_2.feature.file_attachment.data.remote.FileMultipartFactory
import com.example.hits_processes_2.feature.file_attachment.data.remote.toDomain
import com.example.hits_processes_2.feature.file_attachment.domain.model.DownloadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.domain.repository.FileAttachmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLDecoder
import java.util.regex.Pattern

class FileAttachmentRepositoryImpl(
    private val context: Context,
    private val api: FileAttachmentApi,
    private val multipartFactory: FileMultipartFactory,
) : FileAttachmentRepository {

    override suspend fun uploadFile(
        file: FileAttachmentUpload,
        onProgress: (Int) -> Unit,
    ): Result<UploadedFileAttachment> = withContext(Dispatchers.IO) {
        runCatching { multipartFactory.create(file, onProgress) }
            .fold(
                onSuccess = { part ->
                    safeApiCall(
                        apiCall = { api.uploadFile(part) },
                        converter = { it.toDomain() },
                    )
                },
                onFailure = {
                    Result.failure(
                        IllegalStateException(context.getString(R.string.file_attachment_error_prepare)),
                    )
                },
            )
            .mapErrorMessage(context.getString(R.string.file_attachment_error_upload))
    }

    override suspend fun downloadFile(
        fileId: String,
        destinationDir: File,
        onProgress: (Int) -> Unit,
    ): Result<DownloadedFileAttachment> = withContext(Dispatchers.IO) {
        runCatching {
            val response = api.downloadFile(fileId)
            if (!response.isSuccessful || response.body() == null) {
                throw IllegalStateException(context.getString(R.string.file_attachment_error_download))
            }

            val body = response.body()!!
            val contentType = response.headers()[CONTENT_TYPE_HEADER].orEmpty()
            val fileName = parseFileName(
                contentDisposition = response.headers()[CONTENT_DISPOSITION_HEADER],
                contentType = contentType,
                fallback = fileId,
            )
            val outputFile = File(destinationDir, fileName)
            val totalBytes = body.contentLength()
            var writtenBytes = 0L

            body.byteStream().use { input ->
                outputFile.outputStream().use { output ->
                    val buffer = ByteArray(BUFFER_SIZE)
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        writtenBytes += read
                        if (totalBytes > 0L) {
                            onProgress(((writtenBytes * 100) / totalBytes).toInt().coerceIn(0, 100))
                        }
                    }
                }
            }

            DownloadedFileAttachment(
                file = outputFile,
                mimeType = contentType.ifBlank { DEFAULT_CONTENT_TYPE },
            )
        }.mapErrorMessage(context.getString(R.string.file_attachment_error_download))
    }

    private fun parseFileName(
        contentDisposition: String?,
        contentType: String,
        fallback: String,
    ): String {
        if (!contentDisposition.isNullOrBlank()) {
            val filenameStarRegex = Pattern.compile(
                "filename\\*\\s*=\\s*(?:[^'\\s]*'')([^;\\s]+)",
                Pattern.CASE_INSENSITIVE,
            )
            val filenameStarMatcher = filenameStarRegex.matcher(contentDisposition)
            if (filenameStarMatcher.find()) {
                return runCatching {
                    URLDecoder.decode(filenameStarMatcher.group(1), Charsets.UTF_8.name())
                }.getOrElse {
                    fallbackWithExtension(fallback, contentType)
                }.sanitizeFileName()
            }

            val filenameRegex = Pattern.compile(
                "filename\\s*=\\s*\"([^\"]*)\"",
                Pattern.CASE_INSENSITIVE,
            )
            val filenameMatcher = filenameRegex.matcher(contentDisposition)
            if (filenameMatcher.find()) {
                val fileName = filenameMatcher.group(1)?.trim().orEmpty()
                if (fileName.isNotBlank()) {
                    return fileName.sanitizeFileName()
                }
            }
        }

        return fallbackWithExtension(fallback, contentType).sanitizeFileName()
    }

    private fun fallbackWithExtension(
        fallback: String,
        contentType: String,
    ): String {
        val extension = when {
            contentType.contains("pdf", ignoreCase = true) -> ".pdf"
            contentType.contains("image/jpeg", ignoreCase = true) -> ".jpg"
            contentType.contains("image/png", ignoreCase = true) -> ".png"
            contentType.contains("image/webp", ignoreCase = true) -> ".webp"
            contentType.contains("wordprocessingml", ignoreCase = true) -> ".docx"
            contentType.contains("msword", ignoreCase = true) -> ".doc"
            contentType.contains("text/plain", ignoreCase = true) -> ".txt"
            else -> ""
        }

        return if (fallback.contains('.')) fallback else "$fallback$extension"
    }
}

private fun <T> Result<T>.mapErrorMessage(defaultMessage: String): Result<T> {
    return recoverCatching { exception ->
        throw IllegalStateException(exception.message ?: defaultMessage, exception)
    }
}

private fun String.sanitizeFileName(): String {
    return replace(Regex("[\\\\/:*?\"<>|]"), "_")
        .takeIf { it.isNotBlank() }
        ?: "download"
}

private const val CONTENT_DISPOSITION_HEADER = "Content-Disposition"
private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val DEFAULT_CONTENT_TYPE = "application/octet-stream"
private const val BUFFER_SIZE = 8192
