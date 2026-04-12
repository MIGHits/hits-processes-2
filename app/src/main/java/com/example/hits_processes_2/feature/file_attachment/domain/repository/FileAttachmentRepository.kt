package com.example.hits_processes_2.feature.file_attachment.domain.repository

import com.example.hits_processes_2.feature.file_attachment.domain.model.DownloadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import java.io.File

interface FileAttachmentRepository {

    suspend fun uploadFile(
        file: FileAttachmentUpload,
        onProgress: (Int) -> Unit = {},
    ): Result<UploadedFileAttachment>

    suspend fun downloadFile(
        fileId: String,
        destinationDir: File,
        onProgress: (Int) -> Unit = {},
    ): Result<DownloadedFileAttachment>
}
