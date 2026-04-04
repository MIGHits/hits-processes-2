package com.example.hits_processes_2.feature.authorization.domain.usecase

import com.example.hits_processes_2.feature.authorization.domain.model.RegisterData
import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import com.example.hits_processes_2.feature.authorization.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository,
) {

    suspend operator fun invoke(data: RegisterData): Result<TokenPair> =
        repository.register(data)
}
