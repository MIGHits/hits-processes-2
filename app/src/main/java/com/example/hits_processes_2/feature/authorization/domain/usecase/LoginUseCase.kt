package com.example.hits_processes_2.feature.authorization.domain.usecase

import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import com.example.hits_processes_2.feature.authorization.domain.model.UserCredentials
import com.example.hits_processes_2.feature.authorization.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository,
) {

    suspend operator fun invoke(credentials: UserCredentials): Result<TokenPair> =
        repository.login(credentials)
}
