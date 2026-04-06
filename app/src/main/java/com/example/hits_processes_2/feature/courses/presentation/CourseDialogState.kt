package com.example.hits_processes_2.feature.courses.presentation

data class CreateCourseDialogState(
    val name: String = "",
    val description: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)

data class JoinCourseDialogState(
    val code: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)
