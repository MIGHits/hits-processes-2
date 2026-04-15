package com.example.hits_processes_2.feature.task_detail.domain.usecase

import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.task_detail.domain.repository.AttachAnswerResult
import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class AttachTaskAnswerUseCase(
    private val repository: TaskDetailRepository,
) {
    suspend operator fun invoke(
        taskId: String,
        files: List<UploadedFileAttachment>,
    ): Result<AttachAnswerResult> = repository.attachAnswer(taskId, files)
}
