package com.example.hits_processes_2.feature.course_detail.domain.model

data class CourseDetails(
    val id: String,
    val name: String,
    val description: String,
    val joinCode: String?,
    val currentUserRole: CourseDetailsRole,
)
