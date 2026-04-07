package com.example.hits_processes_2.feature.course_detail.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseDetailsDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseEditRequestDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseTaskShortListDto
import com.example.hits_processes_2.feature.course_detail.data.remote.dto.CourseUserListDto
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CourseDetailsApi {

    @GET("api/course/{courseId}")
    suspend fun getCourse(
        @Path("courseId") courseId: String,
    ): Response<ApiResponseDto<CourseDetailsDto>>

    @GET("api/course/{courseId}/task/list")
    suspend fun getTasks(
        @Path("courseId") courseId: String,
    ): Response<ApiResponseDto<CourseTaskShortListDto>>

    @GET("api/course/{courseId}/user/list")
    suspend fun getUsers(
        @Path("courseId") courseId: String,
    ): Response<ApiResponseDto<CourseUserListDto>>

    @PATCH("api/course/{courseId}")
    suspend fun editCourse(
        @Path("courseId") courseId: String,
        @Body body: CourseEditRequestDto,
    ): Response<ApiResponseDto<CourseDetailsDto>>

    @POST("api/course/{courseId}/user/{userId}/role/{newRole}")
    suspend fun changeUserRole(
        @Path("courseId") courseId: String,
        @Path("userId") userId: String,
        @Path("newRole") newRole: String,
    ): Response<ApiResponseDto<JsonElement>>
}
