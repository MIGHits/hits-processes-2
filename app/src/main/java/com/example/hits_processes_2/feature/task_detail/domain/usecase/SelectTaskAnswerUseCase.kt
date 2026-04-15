package com.example.hits_processes_2.feature.task_detail.domain.usecase

import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class SelectTaskAnswerUseCase(
    private val repository: TaskDetailRepository,
) {
    suspend operator fun invoke(taskId: String, answerId: String) =
        repository.selectAnswer(taskId, answerId)
}
