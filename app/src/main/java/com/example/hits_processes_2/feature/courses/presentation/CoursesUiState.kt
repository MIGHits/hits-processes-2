package com.example.hits_processes_2.feature.courses.presentation

import com.example.hits_processes_2.feature.courses.domain.model.CourseShort

data class CoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseShort> = emptyList(),
    val userName: String = "",
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false,
)
