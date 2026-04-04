package com.example.hits_processes_2.feature.authorization.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserLoginDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)
