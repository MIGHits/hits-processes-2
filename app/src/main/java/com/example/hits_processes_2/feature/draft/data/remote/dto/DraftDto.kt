package com.example.hits_processes_2.feature.draft.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class DraftDto(
    @SerialName("id") val id: String = "",
    @SerialName("currentSelectingUser") val currentSelectingUser: DraftUserDto? = null,
    @SerialName("currentSelectingCaptain") val currentSelectingCaptain: DraftUserDto? = null,
    @SerialName("draftPickTurns") val draftPickTurns: List<DraftPickTurnDto> = emptyList(),
    @SerialName("teams") val teams: List<DraftTeamDto> = emptyList(),
    @SerialName("isStarted") val isStarted: Boolean = false,
    @SerialName("isEnded") val isEnded: Boolean = false,
    @SerialName("timeToPick") val timeToPick: Int? = null,
)

@Serializable
data class DraftPickTurnDto(
    @SerialName("id") val id: String = "",
    @SerialName("user") val user: DraftUserDto = DraftUserDto(),
)

@Serializable
data class DraftTeamDto(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String? = null,
    @SerialName("captain") val captain: DraftUserDto? = null,
    @SerialName("members") val members: List<DraftUserDto> = emptyList(),
)

@Serializable
data class DraftUserDto(
    @SerialName("id") val id: String = "",
    @SerialName("firstName") val firstName: String = "",
    @SerialName("lastName") val lastName: String = "",
    @SerialName("email") val email: String? = null,
)

@Serializable
data class DraftSocketAuthMessageDto(
    @SerialName("type") val type: String,
    @SerialName("data") val data: DraftSocketAuthDataDto,
)

@Serializable
data class DraftSocketAuthDataDto(
    @SerialName("token") val token: String,
    @SerialName("observableDraftId") val observableDraftId: String,
)

@Serializable
data class DraftSocketMessageDto(
    @SerialName("type") val type: String = "",
    @SerialName("data") val data: JsonElement? = null,
)

@Serializable
data class DraftOrderOfSelectionChangedDto(
    @SerialName("draftPickTurnModels") val draftPickTurnModels: List<DraftPickTurnDto> = emptyList(),
)

@Serializable
data class DraftStudentJoinedTeamDto(
    @SerialName("teamId") val teamId: String = "",
    @SerialName("user") val user: DraftUserDto = DraftUserDto(),
)

@Serializable
data class DraftTeamStructureChangedDto(
    @SerialName("changedTeam") val changedTeam: DraftTeamDto = DraftTeamDto(),
)
