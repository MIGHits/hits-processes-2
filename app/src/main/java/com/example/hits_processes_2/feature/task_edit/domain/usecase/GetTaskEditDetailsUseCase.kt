package com.example.hits_processes_2.feature.task_edit.domain.usecase

import com.example.hits_processes_2.feature.task_edit.domain.repository.TaskEditRepository

class GetTaskEditDetailsUseCase(
    private val repository: TaskEditRepository,
) {

    suspend operator fun invoke(
        courseId: String,
        taskId: String,
    ) = repository.getTask(courseId, taskId)
}
