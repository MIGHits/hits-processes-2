package com.example.hits_processes_2.common.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponseDto<T>(
    @SerialName("statusCode") val statusCode: Int? = null,
    @SerialName("errorMessage") val errorMessage: String? = null,
    @SerialName("data") val data: T? = null,
)
