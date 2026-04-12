package com.example.hits_processes_2.feature.task_creation.data.repository

import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.common.resources.StringResourceProvider
import com.example.hits_processes_2.feature.task_creation.data.remote.TaskCreationApi
import com.example.hits_processes_2.feature.task_creation.data.remote.toDomain
import com.example.hits_processes_2.feature.task_creation.data.remote.toDto
import com.example.hits_processes_2.feature.task_creation.domain.model.CreateTaskData
import com.example.hits_processes_2.feature.task_creation.domain.model.CreatedTask
import com.example.hits_processes_2.feature.task_creation.domain.repository.TaskCreationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskCreationRepositoryImpl(
    private val api: TaskCreationApi,
    private val strings: StringResourceProvider,
) : TaskCreationRepository {

    override suspend fun createTask(
        courseId: String,
        data: CreateTaskData,
    ): Result<CreatedTask> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.createTask(courseId, data.toDto()) },
            converter = { it.toDomain() },
        ).recoverCatching { exception ->
            throw IllegalStateException(
                exception.message ?: strings.getString(R.string.task_creation_error_create_failed),
                exception,
            )
        }
    }
}
