package com.example.hits_processes_2.feature.file_attachment.data.remote

import com.example.hits_processes_2.feature.file_attachment.data.remote.dto.UploadedFileDto
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment

fun UploadedFileDto.toDomain(): UploadedFileAttachment = UploadedFileAttachment(
    id = id,
    fileName = fileName,
)
