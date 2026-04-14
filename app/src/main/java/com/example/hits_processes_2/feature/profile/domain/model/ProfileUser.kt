package com.example.hits_processes_2.feature.profile.domain.model

data class ProfileUser(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String? = null,
    val birthDate: String? = null,
) {
    val fullName: String
        get() = listOf(lastName, firstName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { email }

    val initials: String
        get() = listOf(firstName, lastName)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifBlank { "?" }
}
