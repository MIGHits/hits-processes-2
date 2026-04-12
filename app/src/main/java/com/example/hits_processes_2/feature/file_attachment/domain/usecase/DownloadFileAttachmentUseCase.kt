package com.example.hits_processes_2.feature.file_attachment.domain.usecase

import com.example.hits_processes_2.feature.file_attachment.domain.repository.FileAttachmentRepository
import java.io.File

class DownloadFileAttachmentUseCase(
    private val repository: FileAttachmentRepository,
) {

    suspend operator fun invoke(
        fileId: String,
        destinationDir: File,
        onProgress: (Int) -> Unit = {},
    ) = repository.downloadFile(fileId, destinationDir, onProgress)
}
