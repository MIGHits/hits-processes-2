package com.example.hits_processes_2.feature.task_detail.domain.model

import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment

data class TeamFinalAnswer(
    val id: String,
    val score: Int = 0,
    val submittedAtIso: String? = null,
    val status: String? = null,
    val files: List<UploadedFileAttachment> = emptyList(),
)

