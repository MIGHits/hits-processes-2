package com.example.hits_processes_2.feature.task_detail.data.repository

import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.task_detail.data.remote.TaskDetailApi
import com.example.hits_processes_2.feature.task_detail.data.remote.toDomain
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import com.example.hits_processes_2.feature.task_detail.domain.repository.TaskDetailRepository

class TaskDetailRepositoryImpl(
    private val api: TaskDetailApi,
) : TaskDetailRepository {

    override suspend fun getTaskDetail(
        courseId: String,
        taskId: String,
    ): Result<TaskDetail> {
        return safeApiCall(
            apiCall = { api.getTask(courseId, taskId) },
            converter = { it.toDomain() },
        )
    }
}
