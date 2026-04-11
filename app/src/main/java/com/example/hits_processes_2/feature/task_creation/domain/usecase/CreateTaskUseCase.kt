package com.example.hits_processes_2.feature.task_creation.domain.usecase

import com.example.hits_processes_2.feature.task_creation.domain.model.CreateTaskData
import com.example.hits_processes_2.feature.task_creation.domain.repository.TaskCreationRepository

class CreateTaskUseCase(
    private val repository: TaskCreationRepository,
) {

    suspend operator fun invoke(
        courseId: String,
        data: CreateTaskData,
    ) = repository.createTask(courseId, data)
}
