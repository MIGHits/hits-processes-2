package com.example.hits_processes_2.feature.authorization.domain.usecase

import com.example.hits_processes_2.feature.authorization.domain.repository.AuthRepository

class LogoutUseCase(
    private val repository: AuthRepository,
) {

    suspend operator fun invoke() {
        repository.logout()
    }
}
