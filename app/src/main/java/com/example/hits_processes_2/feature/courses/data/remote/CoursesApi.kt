package com.example.hits_processes_2.feature.courses.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.courses.data.remote.dto.CourseCreateRequestDto
import com.example.hits_processes_2.feature.courses.data.remote.dto.CourseShortDto
import com.example.hits_processes_2.feature.courses.data.remote.dto.CourseShortListDto
import com.example.hits_processes_2.feature.courses.data.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CoursesApi {

    @GET("api/course/my")
    suspend fun getMyCourses(): Response<ApiResponseDto<CourseShortListDto>>

    @POST("api/course")
    suspend fun createCourse(
        @Body body: CourseCreateRequestDto,
    ): Response<ApiResponseDto<CourseShortDto>>

    @POST("api/course/join/{joinCode}")
    suspend fun joinCourse(
        @Path("joinCode") joinCode: String,
    ): Response<ApiResponseDto<CourseShortDto>>

    @GET("api/user")
    suspend fun getMyProfile(): Response<ApiResponseDto<UserProfileDto>>
}
