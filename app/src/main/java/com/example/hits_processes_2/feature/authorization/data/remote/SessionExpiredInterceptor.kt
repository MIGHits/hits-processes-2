package com.example.hits_processes_2.feature.authorization.data.remote

import com.example.hits_processes_2.feature.authorization.data.TokenStorage
import com.example.hits_processes_2.feature.authorization.domain.SessionExpiredNotifier
import okhttp3.Interceptor
import okhttp3.Response

class SessionExpiredInterceptor(
    private val tokenStorage: TokenStorage,
    private val sessionExpiredNotifier: SessionExpiredNotifier,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (!request.url.encodedPath.isAuthPath() && response.code in SESSION_EXPIRED_CODES) {
            if (tokenStorage.getTokens() != null) {
                tokenStorage.clearTokens()
                sessionExpiredNotifier.notifySessionExpired()
            }
        }

        return response
    }
}

private fun String.isAuthPath(): Boolean {
    return contains("login") ||
        contains("register") ||
        contains("refresh-tokens")
}

private val SESSION_EXPIRED_CODES = setOf(401, 403)
