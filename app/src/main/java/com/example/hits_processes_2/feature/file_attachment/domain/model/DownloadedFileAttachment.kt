package com.example.hits_processes_2.feature.file_attachment.domain.model

import java.io.File

data class DownloadedFileAttachment(
    val file: File,
    val mimeType: String,
)
