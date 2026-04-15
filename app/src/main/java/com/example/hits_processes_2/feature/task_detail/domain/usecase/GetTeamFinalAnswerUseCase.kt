package com.example.hits_processes_2.feature.task_detail.domain.usecase

import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class GetTeamFinalAnswerUseCase(
    private val repository: TaskDetailRepository,
) {
    suspend operator fun invoke(taskId: String, teamId: String) = repository.getTeamFinalAnswer(taskId, teamId)
}

