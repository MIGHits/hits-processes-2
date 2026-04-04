package com.example.hits_processes_2.feature.authorization.domain

import kotlinx.coroutines.flow.Flow

interface SessionExpiredNotifier {

    val sessionExpiredEvents: Flow<Unit>

    fun notifySessionExpired()
}
