package com.example.hits_processes_2.feature.task_creation.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.task_creation.data.remote.dto.CreateTaskRequestDto
import com.example.hits_processes_2.feature.task_creation.data.remote.dto.TaskDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskCreationApi {

    @POST("api/course/{courseId}/task")
    suspend fun createTask(
        @Path("courseId") courseId: String,
        @Body body: CreateTaskRequestDto,
    ): Response<ApiResponseDto<TaskDto>>
}
