package com.example.hits_processes_2.feature.authorization.data.remote

import com.example.hits_processes_2.feature.authorization.data.TokenStorage
import com.example.hits_processes_2.feature.authorization.data.remote.dto.RefreshTokenRequestDto
import com.example.hits_processes_2.feature.authorization.domain.SessionExpiredNotifier
import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val authApi: AuthApi,
    private val sessionExpiredNotifier: SessionExpiredNotifier,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.isRefreshRequest() || response.responseCount >= MAX_AUTH_RETRIES) {
            return null
        }

        val requestAccessToken = response.request.bearerToken()

        synchronized(this) {
            val currentTokens = tokenStorage.getTokens() ?: run {
                expireSession()
                return null
            }

            if (requestAccessToken != null && requestAccessToken != currentTokens.accessToken) {
                return response.request.withBearerToken(currentTokens.accessToken)
            }

            val newTokens = runBlocking { refreshTokens(currentTokens.refreshToken) } ?: return null
            tokenStorage.saveTokens(newTokens)
            return response.request.withBearerToken(newTokens.accessToken)
        }
    }

    private suspend fun refreshTokens(refreshToken: String): TokenPair? {
        val response = runCatching {
            authApi.refreshTokens(RefreshTokenRequestDto(refreshToken))
        }.getOrNull() ?: return null

        if (!response.isSuccessful) {
            expireSession()
            return null
        }

        return response.body()?.data?.toDomain() ?: run {
            expireSession()
            null
        }
    }

    private fun expireSession() {
        tokenStorage.clearTokens()
        sessionExpiredNotifier.notifySessionExpired()
    }
}

private fun Response.isRefreshRequest(): Boolean =
    request.url.encodedPath.contains("refresh-tokens")

private fun Request.bearerToken(): String? {
    return header("Authorization")
        ?.removePrefix("Bearer ")
        ?.takeIf { it.isNotBlank() }
}

private fun Request.withBearerToken(token: String): Request {
    return newBuilder()
        .header("Authorization", "Bearer $token")
        .build()
}

private val Response.responseCount: Int
    get() = generateSequence(this) { it.priorResponse }.count()

private const val MAX_AUTH_RETRIES = 2
