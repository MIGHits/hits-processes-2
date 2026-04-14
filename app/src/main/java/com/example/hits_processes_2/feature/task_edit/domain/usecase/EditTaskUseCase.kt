package com.example.hits_processes_2.feature.task_edit.domain.usecase

import com.example.hits_processes_2.feature.task_edit.domain.model.UpdateTaskData
import com.example.hits_processes_2.feature.task_edit.domain.repository.TaskEditRepository

class EditTaskUseCase(
    private val repository: TaskEditRepository,
) {

    suspend operator fun invoke(
        courseId: String,
        taskId: String,
        data: UpdateTaskData,
    ) = repository.editTask(courseId, taskId, data)
}
