package com.example.hits_processes_2.feature.courses.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseShortListDto(
    @SerialName("courseShortList") val courseShortList: List<CourseShortDto> = emptyList(),
)
