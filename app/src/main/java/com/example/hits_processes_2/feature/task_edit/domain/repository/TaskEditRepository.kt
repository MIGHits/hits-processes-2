package com.example.hits_processes_2.feature.task_edit.domain.repository

import com.example.hits_processes_2.feature.task_edit.domain.model.EditTaskDetails
import com.example.hits_processes_2.feature.task_edit.domain.model.UpdateTaskData

interface TaskEditRepository {

    suspend fun getTask(
        courseId: String,
        taskId: String,
    ): Result<EditTaskDetails>

    suspend fun editTask(
        courseId: String,
        taskId: String,
        data: UpdateTaskData,
    ): Result<EditTaskDetails>
}
