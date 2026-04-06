package com.example.hits_processes_2.feature.courses.domain.model

data class UserProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
) {
    val fullName: String
        get() = listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { email }
}
