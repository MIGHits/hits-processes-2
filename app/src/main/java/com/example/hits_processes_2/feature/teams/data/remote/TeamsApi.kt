package com.example.hits_processes_2.feature.teams.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.FinalTaskAnswerDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.TeamDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.TeamShortListDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.TaskRateRequestDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.UserCourseListDto
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TeamsApi {

    @GET("api/course/{courseId}/task/{taskId}/team/list")
    suspend fun getTeams(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
    ): Response<ApiResponseDto<TeamShortListDto>>

    @GET("api/course/{courseId}/task/{taskId}/team/{teamId}")
    suspend fun getTeam(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
    ): Response<ApiResponseDto<TeamDto>>

    @GET("api/task-answer/task/{taskId}/team/{teamId}/final")
    suspend fun getTeamFinalAnswer(
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
    ): Response<FinalTaskAnswerDto>

    @GET("api/course/{courseId}/task/{taskId}/team/free-students")
    suspend fun getFreeStudents(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
    ): Response<ApiResponseDto<UserCourseListDto>>

    @POST("api/course/{courseId}/task/{taskId}/team/{teamId}/join")
    suspend fun joinTeam(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
    ): Response<ApiResponseDto<TeamDto>>

    @DELETE("api/course/{courseId}/task/{taskId}/team/{teamId}/leave")
    suspend fun leaveTeam(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
    ): Response<ApiResponseDto<TeamDto>>

    @POST("api/course/{courseId}/task/{taskId}/team/{teamId}/member/{studentId}")
    suspend fun addTeamMember(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
        @Path("studentId") studentId: String,
    ): Response<ApiResponseDto<TeamDto>>

    @DELETE("api/course/{courseId}/task/{taskId}/team/{teamId}/member/{teamMemberId}")
    suspend fun removeTeamMember(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
        @Path("teamMemberId") teamMemberId: String,
    ): Response<ApiResponseDto<TeamDto>>

    @POST("api/course/{courseId}/task/{taskId}/team/{teamId}/captain/{studentId}")
    suspend fun assignTeamCaptain(
        @Path("courseId") courseId: String,
        @Path("taskId") taskId: String,
        @Path("teamId") teamId: String,
        @Path("studentId") studentId: String,
    ): Response<ApiResponseDto<TeamDto>>

    @POST("api/task-answer/final/{teamFinalTaskAnswerId}/grade")
    suspend fun evaluateTaskAnswer(
        @Path("teamFinalTaskAnswerId") teamFinalTaskAnswerId: String,
        @Body request: TaskRateRequestDto,
    ): Response<JsonElement>
}
