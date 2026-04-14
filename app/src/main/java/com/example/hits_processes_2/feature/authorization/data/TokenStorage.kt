package com.example.hits_processes_2.feature.authorization.data

import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import kotlinx.coroutines.flow.StateFlow

interface TokenStorage {
    val tokens: StateFlow<TokenPair?>

    fun getTokens(): TokenPair?

    fun saveTokens(tokens: TokenPair)

    fun clearTokens()
}
