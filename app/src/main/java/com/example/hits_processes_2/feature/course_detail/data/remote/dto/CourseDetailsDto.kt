package com.example.hits_processes_2.feature.course_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("joinCode") val joinCode: String? = null,
    @SerialName("description") val description: String = "",
    @SerialName("currentUserCourseRole") val currentUserCourseRole: CourseDetailsRoleDto? = null,
)
