package com.example.hits_processes_2.feature.file_attachment.data.remote

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.IOException

class FileMultipartFactory(
    private val context: Context,
) {

    fun create(file: FileAttachmentUpload): MultipartBody.Part {
        val uri = Uri.parse(file.uriString)
        val mimeType = context.contentResolver.getType(uri)
            ?.toMediaTypeOrNull()
            ?: DEFAULT_MEDIA_TYPE

        return MultipartBody.Part.createFormData(
            name = FILE_PART_NAME,
            filename = file.fileName,
            body = ContentUriRequestBody(
                contentResolver = context.contentResolver,
                uri = uri,
                contentType = mimeType,
            ),
        )
    }

    private class ContentUriRequestBody(
        private val contentResolver: ContentResolver,
        private val uri: Uri,
        private val contentType: MediaType,
    ) : RequestBody() {

        override fun contentType(): MediaType = contentType

        override fun contentLength(): Long {
            return contentResolver.openAssetFileDescriptor(uri, "r")
                ?.use { descriptor -> descriptor.length.coerceAtLeast(-1L) }
                ?: -1L
        }

        override fun writeTo(sink: BufferedSink) {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                sink.writeAll(inputStream.source())
            } ?: throw IOException()
        }
    }

    private companion object {
        const val FILE_PART_NAME = "file"
        val DEFAULT_MEDIA_TYPE = "application/octet-stream".toMediaType()
    }
}
