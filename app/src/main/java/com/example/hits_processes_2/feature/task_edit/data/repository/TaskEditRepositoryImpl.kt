package com.example.hits_processes_2.feature.task_edit.data.repository

import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.task_edit.data.remote.TaskEditApi
import com.example.hits_processes_2.feature.task_edit.data.remote.toDomain
import com.example.hits_processes_2.feature.task_edit.data.remote.toDto
import com.example.hits_processes_2.feature.task_edit.domain.model.EditTaskDetails
import com.example.hits_processes_2.feature.task_edit.domain.model.UpdateTaskData
import com.example.hits_processes_2.feature.task_edit.domain.repository.TaskEditRepository

class TaskEditRepositoryImpl(
    private val api: TaskEditApi,
) : TaskEditRepository {

    override suspend fun getTask(
        courseId: String,
        taskId: String,
    ): Result<EditTaskDetails> {
        return safeApiCall(
            apiCall = { api.getTask(courseId, taskId) },
            converter = { it.toDomain() },
        )
    }

    override suspend fun editTask(
        courseId: String,
        taskId: String,
        data: UpdateTaskData,
    ): Result<EditTaskDetails> {
        return safeApiCall(
            apiCall = { api.editTask(courseId, taskId, data.toDto()) },
            converter = { it.toDomain() },
        )
    }
}
