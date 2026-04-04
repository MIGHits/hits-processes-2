package com.example.hits_processes_2.feature.authorization.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterDto(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("firstName") val firstName: String,
    @SerialName("lastName") val lastName: String,
)
