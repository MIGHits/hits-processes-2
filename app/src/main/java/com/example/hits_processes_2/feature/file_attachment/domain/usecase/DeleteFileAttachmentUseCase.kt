package com.example.hits_processes_2.feature.file_attachment.domain.usecase

import com.example.hits_processes_2.feature.file_attachment.domain.repository.FileAttachmentRepository

class DeleteFileAttachmentUseCase(
    private val repository: FileAttachmentRepository,
) {
    suspend operator fun invoke(fileId: String) = repository.deleteFile(fileId)
}

