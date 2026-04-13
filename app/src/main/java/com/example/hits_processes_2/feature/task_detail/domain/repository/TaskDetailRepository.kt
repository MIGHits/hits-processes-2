package com.example.hits_processes_2.feature.task_detail.domain.repository

import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail

interface TaskDetailRepository {

    suspend fun getTaskDetail(
        courseId: String,
        taskId: String,
    ): Result<TaskDetail>
}
