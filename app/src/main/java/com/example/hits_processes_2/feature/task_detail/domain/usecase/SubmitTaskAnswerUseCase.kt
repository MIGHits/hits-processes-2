package com.example.hits_processes_2.feature.task_detail.domain.usecase

import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class SubmitTaskAnswerUseCase(
    private val repository: TaskDetailRepository,
) {
    suspend operator fun invoke(taskId: String): Result<Unit> =
        repository.submitAnswer(taskId)
}
