package com.example.hits_processes_2.feature.course_detail.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseUserListDto(
    @SerialName("userCourseList") val userCourseList: List<CourseUserDto> = emptyList(),
)
