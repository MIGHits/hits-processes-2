package com.example.hits_processes_2.feature.file_attachment.domain.repository

import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment

interface FileAttachmentRepository {

    suspend fun uploadFile(file: FileAttachmentUpload): Result<UploadedFileAttachment>
}
