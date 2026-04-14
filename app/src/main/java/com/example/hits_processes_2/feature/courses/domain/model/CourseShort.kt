package com.example.hits_processes_2.feature.courses.domain.model

data class CourseShort(
    val id: String,
    val name: String,
    val description: String,
    val currentUserRole: CourseRole?,
)
