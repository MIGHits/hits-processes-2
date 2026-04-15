package com.example.hits_processes_2.feature.task_detail.data.remote

import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.TaskAnswerDto
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskAnswer
import com.example.hits_processes_2.feature.task_detail.domain.model.TeamFinalAnswer
import com.example.hits_processes_2.feature.teams.data.remote.dto.FinalTaskAnswerDto

fun TaskAnswerDto.toDomain(): TaskAnswer = TaskAnswer(
    id = id,
    files = files.orEmpty().map { UploadedFileAttachment(id = it.id, fileName = it.fileName.orEmpty()) },
    uploadedAtIso = uploadedAt,
    finalDecision = finalDecision,
)

fun FinalTaskAnswerDto.toDomain(): TeamFinalAnswer = TeamFinalAnswer(
    id = id.orEmpty(),
    score = score ?: 0,
    submittedAtIso = submittedAt,
    status = status,
    files = taskAnswer?.files.orEmpty()
        .mapNotNull { file ->
            val fileId = file.id ?: return@mapNotNull null
            UploadedFileAttachment(id = fileId, fileName = file.fileName.orEmpty())
        },
)

