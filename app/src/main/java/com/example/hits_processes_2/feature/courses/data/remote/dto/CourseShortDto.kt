package com.example.hits_processes_2.feature.courses.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseShortDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("currentUserCourseRole") val currentUserCourseRole: CourseRoleDto? = null,
)
