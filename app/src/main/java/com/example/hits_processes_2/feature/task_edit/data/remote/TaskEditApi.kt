package com.example.hits_processes_2.feature.task_edit.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.task_edit.data.remote.dto.TaskEditDetailsDto
import com.example.hits_processes_2.feature.task_edit.data.remote.dto.TaskEditRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface TaskEditApi {

    @GET("api/course/{courseId}/task/{taskId}")
    suspend fun getTask(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
    ): Response<ApiResponseDto<TaskEditDetailsDto>>

    @PUT("api/course/{courseId}/task/{taskId}")
    suspend fun editTask(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
        @Body body: TaskEditRequestDto,
    ): Response<ApiResponseDto<TaskEditDetailsDto>>
}
