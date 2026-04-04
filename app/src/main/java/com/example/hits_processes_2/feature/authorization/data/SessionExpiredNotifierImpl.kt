package com.example.hits_processes_2.feature.authorization.data

import com.example.hits_processes_2.feature.authorization.domain.SessionExpiredNotifier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class SessionExpiredNotifierImpl : SessionExpiredNotifier {

    private val channel = Channel<Unit>(Channel.CONFLATED)

    override val sessionExpiredEvents = channel.receiveAsFlow()

    override fun notifySessionExpired() {
        channel.trySend(Unit)
    }
}
