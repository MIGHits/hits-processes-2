package com.example.hits_processes_2.feature.course_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimpleUserDto(
    @SerialName("id") val id: String,
    @SerialName("firstName") val firstName: String = "",
    @SerialName("lastName") val lastName: String = "",
    @SerialName("email") val email: String = "",
)
