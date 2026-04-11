package com.example.hits_processes_2.feature.file_attachment.domain.usecase

import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.repository.FileAttachmentRepository

class UploadFileAttachmentUseCase(
    private val repository: FileAttachmentRepository,
) {

    suspend operator fun invoke(file: FileAttachmentUpload) = repository.uploadFile(file)
}
