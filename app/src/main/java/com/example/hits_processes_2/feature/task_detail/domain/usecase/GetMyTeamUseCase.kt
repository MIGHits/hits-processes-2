package com.example.hits_processes_2.feature.task_detail.domain.usecase

import com.example.hits_processes_2.feature.task_detail.domain.model.MyTeam
import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class GetMyTeamUseCase(
    private val repository: TaskDetailRepository,
) {
    suspend operator fun invoke(courseId: String, taskId: String): Result<MyTeam> =
        repository.getMyTeam(courseId, taskId)
}
