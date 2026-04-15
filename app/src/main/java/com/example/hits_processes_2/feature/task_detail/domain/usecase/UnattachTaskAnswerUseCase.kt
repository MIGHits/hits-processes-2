package com.example.hits_processes_2.feature.task_detail.domain.usecase

import com.example.hits_processes_2.feature.task_detail.domain.model.TeamFinalAnswer
import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class UnattachTaskAnswerUseCase(
    private val repository: TaskDetailRepository,
) {
    suspend operator fun invoke(taskId: String, taskAnswerId: String): Result<TeamFinalAnswer?> {
        return repository.unattachAnswer(taskId, taskAnswerId)
    }
}

