package com.example.hits_processes_2.feature.course_detail.domain.model

data class CourseParticipant(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: CourseDetailsRole,
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { email }
}
