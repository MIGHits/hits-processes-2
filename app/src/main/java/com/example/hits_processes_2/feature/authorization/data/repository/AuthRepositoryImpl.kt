package com.example.hits_processes_2.feature.authorization.data.repository

import com.example.hits_processes_2.common.network.ApiException
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.common.network.safeApiCallUnit
import com.example.hits_processes_2.feature.authorization.data.TokenStorage
import com.example.hits_processes_2.feature.authorization.data.remote.AuthApi
import com.example.hits_processes_2.feature.authorization.data.remote.dto.RefreshTokenRequestDto
import com.example.hits_processes_2.feature.authorization.data.remote.toDomain
import com.example.hits_processes_2.feature.authorization.data.remote.toLoginDto
import com.example.hits_processes_2.feature.authorization.data.remote.toRegisterDto
import com.example.hits_processes_2.feature.authorization.domain.model.RegisterData
import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import com.example.hits_processes_2.feature.authorization.domain.model.UserCredentials
import com.example.hits_processes_2.feature.authorization.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val tokenStorage: TokenStorage,
) : AuthRepository {

    override suspend fun login(credentials: UserCredentials): Result<TokenPair> =
        executeTokenRequest("Login failed") {
            safeApiCall(
                apiCall = { api.login(credentials.toLoginDto()) },
                converter = { it.toDomain() },
            )
        }

    override suspend fun register(data: RegisterData): Result<TokenPair> =
        executeTokenRequest("Registration failed") {
            safeApiCall(
                apiCall = { api.register(data.toRegisterDto()) },
                converter = { it.toDomain() },
            )
        }

    override suspend fun refreshTokens(): Result<TokenPair> {
        val currentTokens = tokenStorage.getTokens()
            ?: return Result.failure(AuthException(-1, "Refresh token is missing"))

        return executeTokenRequest("Token refresh failed") {
            safeApiCall(
                apiCall = { api.refreshTokens(RefreshTokenRequestDto(currentTokens.refreshToken)) },
                converter = { it.toDomain() },
            )
        }
    }

    override suspend fun logout() = withContext(Dispatchers.IO) {
        safeApiCallUnit(api::logout)
        tokenStorage.clearTokens()
    }

    private suspend fun executeTokenRequest(
        defaultMessage: String,
        request: suspend () -> Result<TokenPair>,
    ): Result<TokenPair> = withContext(Dispatchers.IO) {
        request()
            .mapCatching { tokens ->
                tokenStorage.saveTokens(tokens)
                tokens
            }
            .recoverCatching { exception ->
                throw exception.toAuthException(defaultMessage)
            }
    }
}

class AuthException(
    val code: Int,
    override val message: String,
) : Exception(message)

private fun Throwable.toAuthException(defaultMessage: String): AuthException {
    return when (this) {
        is AuthException -> this
        is ApiException -> AuthException(code, message)
        else -> AuthException(-1, message ?: defaultMessage)
    }
}
