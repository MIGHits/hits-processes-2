package com.example.hits_processes_2.feature.authorization.domain.model

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
