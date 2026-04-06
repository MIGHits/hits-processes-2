package com.example.hits_processes_2.feature.courses.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseCreateRequestDto(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
)
