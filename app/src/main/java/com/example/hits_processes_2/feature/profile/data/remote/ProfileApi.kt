package com.example.hits_processes_2.feature.profile.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.profile.data.remote.dto.ProfileUserDto
import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {

    @GET("api/user")
    suspend fun getMyProfile(): Response<ApiResponseDto<ProfileUserDto>>
}
