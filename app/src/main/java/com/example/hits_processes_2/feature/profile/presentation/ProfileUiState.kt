package com.example.hits_processes_2.feature.profile.presentation

import com.example.hits_processes_2.feature.profile.domain.model.ProfileUser

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: ProfileUser? = null,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false,
)
