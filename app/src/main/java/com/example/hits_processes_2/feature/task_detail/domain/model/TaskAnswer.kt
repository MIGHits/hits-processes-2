package com.example.hits_processes_2.feature.task_detail.domain.model

import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment

data class TaskAnswer(
    val id: String,
    val files: List<UploadedFileAttachment>,
    val uploadedAtIso: String? = null,
    val finalDecision: Boolean = false,
)

