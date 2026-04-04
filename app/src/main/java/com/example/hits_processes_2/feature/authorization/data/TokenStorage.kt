package com.example.hits_processes_2.feature.authorization.data

import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair

interface TokenStorage {

    fun getTokens(): TokenPair?

    fun saveTokens(tokens: TokenPair)

    fun clearTokens()
}
