package com.example.hits_processes_2.feature.authorization.data.remote

import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.feature.authorization.data.remote.dto.RefreshTokenRequestDto
import com.example.hits_processes_2.feature.authorization.data.remote.dto.TokenResponseDto
import com.example.hits_processes_2.feature.authorization.data.remote.dto.UserLoginDto
import com.example.hits_processes_2.feature.authorization.data.remote.dto.UserRegisterDto
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/login")
    suspend fun login(
        @Body body: UserLoginDto,
    ): Response<ApiResponseDto<TokenResponseDto>>

    @POST("api/auth/register")
    suspend fun register(
        @Body body: UserRegisterDto,
    ): Response<ApiResponseDto<TokenResponseDto>>

    @POST("api/auth/refresh-tokens")
    suspend fun refreshTokens(
        @Body body: RefreshTokenRequestDto,
    ): Response<ApiResponseDto<TokenResponseDto>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponseDto<JsonElement>>
}
