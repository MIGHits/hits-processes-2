package com.example.hits_processes_2.feature.authorization.data

import android.content.Context
import android.content.SharedPreferences
import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TokenStorageImpl(
    context: Context,
) : TokenStorage {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )
    private val _tokens = MutableStateFlow(readTokens())
    override val tokens: StateFlow<TokenPair?> = _tokens

    override fun getTokens(): TokenPair? {
        return _tokens.value
    }

    private fun readTokens(): TokenPair? {
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
        _tokens.value = tokens
    }

    override fun clearTokens() {
        preferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
        _tokens.value = null
    }

    private companion object {
        const val PREFERENCES_NAME = "auth_tokens"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
