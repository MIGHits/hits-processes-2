package com.example.hits_processes_2.feature.task_creation.domain.repository

import com.example.hits_processes_2.feature.task_creation.domain.model.CreateTaskData
import com.example.hits_processes_2.feature.task_creation.domain.model.CreatedTask

interface TaskCreationRepository {

    suspend fun createTask(
        courseId: String,
        data: CreateTaskData,
    ): Result<CreatedTask>
}
