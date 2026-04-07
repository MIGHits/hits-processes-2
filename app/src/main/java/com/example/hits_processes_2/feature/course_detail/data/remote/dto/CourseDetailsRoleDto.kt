package com.example.hits_processes_2.feature.course_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CourseDetailsRoleDto {
    @SerialName("STUDENT")
    STUDENT,

    @SerialName("TEACHER")
    TEACHER,

    @SerialName("HEAD_TEACHER")
    HEAD_TEACHER,
}
