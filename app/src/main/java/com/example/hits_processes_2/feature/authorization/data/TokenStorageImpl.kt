package com.example.hits_processes_2.feature.authorization.data

import android.content.Context
import android.content.SharedPreferences
import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair

class TokenStorageImpl(
    context: Context,
) : TokenStorage {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun getTokens(): TokenPair? {
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null)
        val refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null)

        return if (accessToken != null && refreshToken != null) {
            TokenPair(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } else {
            null
        }
    }

    override fun saveTokens(tokens: TokenPair) {
        preferences.edit()
            .putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            .putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
            .apply()
    }

    override fun clearTokens() {
        preferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "auth_tokens"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
