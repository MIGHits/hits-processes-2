package com.example.hits_processes_2.feature.task_detail.domain.model

data class TaskAuthor(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
) {

    val fullName: String
        get() = listOf(firstName, lastName)
            .filter(String::isNotBlank)
            .joinToString(" ")
}
