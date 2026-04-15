package com.example.hits_processes_2.feature.task_detail.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.FinalTaskAnswerWithAnswerIdDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.FileAnswerDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.MyTeamDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.TaskAnswerDto
import com.example.hits_processes_2.feature.task_detail.data.remote.dto.TaskDetailDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.FinalTaskAnswerDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskDetailApi {

    @GET("api/course/{courseId}/task/{taskId}")
    suspend fun getTask(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
    ): Response<ApiResponseDto<TaskDetailDto>>

    @GET("api/course/{courseId}/task/{taskId}/team/my")
    suspend fun getMyTeam(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
    ): Response<ApiResponseDto<MyTeamDto>>

    @POST("api/task-answer/task/{taskId}/answers")
    suspend fun attachAnswer(
        @Path("taskId") taskId: String,
        @Body files: List<FileAnswerDto>,
    ): Response<FinalTaskAnswerWithAnswerIdDto>

    @DELETE("api/task-answer/task/{taskId}/answers/{taskAnswerId}")
    suspend fun unattachAnswer(
        @Path("taskId") taskId: String,
        @Path("taskAnswerId") taskAnswerId: String,
    ): Response<FinalTaskAnswerDto>

    @GET("api/task-answer/task/{taskId}/my-attached")
    suspend fun getAllUserTaskAnswers(
        @Path("taskId") taskId: String,
    ): Response<List<TaskAnswerDto>>

    @GET("api/task-answer/task/{taskId}/team/{teamId}/final")
    suspend fun getTeamFinalAnswer(
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
    ): Response<FinalTaskAnswerDto>

    @POST("api/task-answer/task/{taskId}/submit")
    suspend fun submitAnswer(
        @Path("taskId") taskId: String,
    ): Response<Unit>

    @POST("api/task-answer/task/{taskId}/unsubmit")
    suspend fun unsubmitAnswer(
        @Path("taskId") taskId: String,
    ): Response<Unit>
}
