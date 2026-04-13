package com.example.hits_processes_2.feature.task_detail.domain.usecase

import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class GetTaskDetailUseCase(
    private val repository: TaskDetailRepository,
) {

    suspend operator fun invoke(
        courseId: String,
        taskId: String,
    ) = repository.getTaskDetail(courseId, taskId)
}
