package com.example.hits_processes_2.feature.course_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseTaskShortDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String = "",
    @SerialName("text") val text: String = "",
    @SerialName("createdAt") val createdAt: String? = null,
)
