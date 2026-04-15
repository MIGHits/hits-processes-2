package com.example.hits_processes_2.feature.authorization.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (isAuthPath(request.url.encodedPath)) {
            return chain.proceed(request)
        }

        val token = tokenProvider()
        val authorizedRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(authorizedRequest)
    }

    private fun isAuthPath(path: String): Boolean {
        return path.contains("login") ||
            path.contains("register") ||
            path.contains("refresh-tokens")
    }
}
