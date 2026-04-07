package com.example.hits_processes_2.feature.course_detail.presentation

import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetails
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseParticipant
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseTask

data class CourseEditDialogState(
    val name: String = "",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

data class CourseDetailsUiState(
    val isLoading: Boolean = false,
    val course: CourseDetails? = null,
    val tasks: List<CourseTask> = emptyList(),
    val teachers: List<CourseParticipant> = emptyList(),
    val students: List<CourseParticipant> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshingRoles: Boolean = false,
)
