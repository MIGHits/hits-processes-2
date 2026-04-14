package com.example.hits_processes_2.feature.profile.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileUserDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String = "",
    @SerialName("lastName") val lastName: String = "",
    @SerialName("email") val email: String = "",
)
