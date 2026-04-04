package com.example.hits_processes_2.feature.authorization.domain.model

data class RegisterData(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
)
