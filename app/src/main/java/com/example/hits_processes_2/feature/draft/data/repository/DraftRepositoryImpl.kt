package com.example.hits_processes_2.feature.draft.data.repository

import android.util.Log
import com.example.hits_processes_2.common.network.ApiException
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.authorization.data.TokenStorage
import com.example.hits_processes_2.feature.draft.data.remote.DraftApi
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftOrderOfSelectionChangedDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftSocketAuthDataDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftSocketAuthMessageDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftSocketMessageDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftStudentJoinedTeamDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftTeamStructureChangedDto
import com.example.hits_processes_2.feature.draft.data.remote.toDomain
import com.example.hits_processes_2.feature.draft.data.remote.toDomainUser
import com.example.hits_processes_2.feature.draft.domain.model.Draft
import com.example.hits_processes_2.feature.draft.domain.model.DraftRealtimeEvent
import com.example.hits_processes_2.feature.draft.domain.repository.DraftRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.HttpException

class DraftRepositoryImpl(
    private val api: DraftApi,
    private val tokenStorage: TokenStorage,
    private val okHttpClient: OkHttpClient,
    private val json: Json,
) : DraftRepository {

    override suspend fun getDraft(draftId: String): Result<Draft> {
        return safeApiCall(
            apiCall = { api.getDraft(draftId) },
            converter = { it.toDomain() },
        ).recoverCatching { error ->
            throw error.toDraftException("Не удалось загрузить драфт")
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeDraft(draftId: String): Flow<DraftRealtimeEvent> {
        return tokenStorage.tokens.flatMapLatest { tokens ->
            val token = tokens?.accessToken
            if (token.isNullOrBlank()) {
                flowOf(DraftRealtimeEvent.Error("Нет токена для подключения к драфту"))
            } else {
                connectToDraft(draftId = draftId, token = token)
            }
        }
    }

    private fun connectToDraft(
        draftId: String,
        token: String,
    ): Flow<DraftRealtimeEvent> = callbackFlow {
        Log.d(TAG, "Connecting: url=$DRAFT_WS_URL, draftId=$draftId")
        val request = Request.Builder()
            .url(DRAFT_WS_URL)
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "Opened: code=${response.code}, draftId=$draftId")
                val authMessage = DraftSocketAuthMessageDto(
                    type = TYPE_AUTH,
                    data = DraftSocketAuthDataDto(
                        token = token,
                        observableDraftId = draftId,
                    ),
                )
                val authPayload = json.encodeToString(authMessage)
                Log.d(TAG, "Sending AUTH: draftId=$draftId, token=${token.maskForLog()}")
                webSocket.send(authPayload)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message: $text")
                trySend(text.toRealtimeEvent())
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "Failure: code=${response?.code}, draftId=$draftId, message=${t.message}", t)
                trySend(DraftRealtimeEvent.Error(t.message ?: "Ошибка websocket драфта"))
                close()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "Closing: code=$code, reason=$reason, draftId=$draftId")
                webSocket.close(code, reason)
                close()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "Closed: code=$code, reason=$reason, draftId=$draftId")
                close()
            }
        }

        val webSocket = okHttpClient.newWebSocket(request, listener)
        awaitClose {
            Log.d(TAG, "Flow closed: draftId=$draftId")
            webSocket.close(NORMAL_CLOSURE_STATUS, "Draft flow closed")
        }
    }

    private fun String.toRealtimeEvent(): DraftRealtimeEvent {
        return runCatching {
            val message = json.decodeFromString<DraftSocketMessageDto>(this)
            when (message.type) {
                TYPE_DRAFT_STARTED -> DraftRealtimeEvent.DraftStarted(message.data.decodeDraft())
                TYPE_ORDER_OF_SELECTION_CHANGED -> message.data.decodeOrderOfSelectionChanged()
                TYPE_STUDENT_JOINED_TEAM -> message.data.decodeStudentJoinedTeam()
                TYPE_TEAM_STRUCTURE_CHANGED -> message.data.decodeTeamStructureChanged()
                TYPE_TIME_TO_CHOOSE_STUDENT -> DraftRealtimeEvent.TimeToChooseStudent
                TYPE_AUTO_SELECTION_PERFORMED -> DraftRealtimeEvent.AutoSelectionPerformed
                TYPE_DRAFT_ENDED -> DraftRealtimeEvent.DraftEnded(message.data.decodeDraftOrNull())
                TYPE_ERROR -> DraftRealtimeEvent.Error(message.data.extractErrorMessage())
                else -> DraftRealtimeEvent.Unknown(
                    type = message.type,
                    rawData = message.data?.toString(),
                )
            }
        }.getOrElse { error ->
            DraftRealtimeEvent.Error(error.message ?: "Не удалось прочитать websocket сообщение")
        }
    }

    private fun JsonElement?.decodeDraft(): Draft {
        return this?.let { json.decodeFromJsonElement<DraftDto>(it).toDomain() }
            ?: throw SerializationException("Draft message data is empty")
    }

    private fun JsonElement?.decodeDraftOrNull(): Draft? {
        return this?.let { runCatching { json.decodeFromJsonElement<DraftDto>(it).toDomain() }.getOrNull() }
    }

    private fun JsonElement?.decodeStudentJoinedTeam(): DraftRealtimeEvent.StudentJoinedTeam {
        val data = this?.let { json.decodeFromJsonElement<DraftStudentJoinedTeamDto>(it) }
            ?: throw SerializationException("Student joined team message data is empty")
        return DraftRealtimeEvent.StudentJoinedTeam(
            teamId = data.teamId,
            user = data.toDomainUser(),
        )
    }

    private fun JsonElement?.decodeOrderOfSelectionChanged(): DraftRealtimeEvent.OrderOfSelectionChanged {
        val data = this?.let { json.decodeFromJsonElement<DraftOrderOfSelectionChangedDto>(it) }
            ?: throw SerializationException("Order of selection message data is empty")
        return DraftRealtimeEvent.OrderOfSelectionChanged(
            pickTurns = data.draftPickTurnModels.map { it.toDomain() },
        )
    }

    private fun JsonElement?.decodeTeamStructureChanged(): DraftRealtimeEvent.TeamStructureChanged {
        val data = this?.let { json.decodeFromJsonElement<DraftTeamStructureChangedDto>(it) }
            ?: throw SerializationException("Team structure message data is empty")
        return DraftRealtimeEvent.TeamStructureChanged(
            changedTeam = data.changedTeam.toDomain(fallbackNumber = 0),
        )
    }

    private fun JsonElement?.extractErrorMessage(): String {
        return this?.let { element ->
            runCatching { element.jsonPrimitive.contentOrNull }.getOrNull()
        }
            ?: this?.toString()
            ?: "Ошибка драфта"
    }

    private companion object {
        const val TAG = "DraftWebSocket"
        const val DRAFT_WS_URL = "ws://91.227.18.176:8024/ws"
        const val NORMAL_CLOSURE_STATUS = 1000
        const val TYPE_AUTH = "AUTH"
        const val TYPE_DRAFT_STARTED = "DRAFT_STARTED"
        const val TYPE_ORDER_OF_SELECTION_CHANGED = "ORDER_OF_SELECTION_CHANGED"
        const val TYPE_STUDENT_JOINED_TEAM = "STUDENT_JOINED_TEAM"
        const val TYPE_TEAM_STRUCTURE_CHANGED = "TEAM_STRUCTURE_CHANGED"
        const val TYPE_TIME_TO_CHOOSE_STUDENT = "TIME_TO_CHOOSE_STUDENT"
        const val TYPE_AUTO_SELECTION_PERFORMED = "AUTO_SELECTION_PERFORMED"
        const val TYPE_DRAFT_ENDED = "DRAFT_ENDED"
        const val TYPE_ERROR = "ERROR"
    }
}

private fun String.maskForLog(): String {
    if (length <= 12) return "***"
    return "${take(6)}...${takeLast(4)}"
}

class DraftException(
    val code: Int,
    override val message: String,
) : Exception(message)

private fun Throwable.toDraftException(defaultMessage: String): DraftException {
    return when (this) {
        is DraftException -> this
        is ApiException -> DraftException(code = code, message = message)
        is HttpException -> DraftException(code = code(), message = message())
        else -> DraftException(code = -1, message = message ?: defaultMessage)
    }
}
