package com.example.hits_processes_2.feature.task_detail.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.TaskDetailDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TaskDetailApi {

    @GET("api/course/{courseId}/task/{taskId}")
    suspend fun getTask(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
    ): Response<ApiResponseDto<TaskDetailDto>>
}
