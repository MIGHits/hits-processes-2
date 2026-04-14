package com.example.hits_processes_2.feature.draft.domain.model

data class Draft(
    val id: String,
    val currentSelectingUser: DraftUser?,
    val pickTurns: List<DraftPickTurn>,
    val teams: List<DraftTeam>,
    val isStarted: Boolean,
    val isEnded: Boolean,
)

data class DraftPickTurn(
    val id: String,
    val user: DraftUser,
)

data class DraftTeam(
    val id: String,
    val name: String?,
    val number: Int,
    val captain: DraftUser?,
    val members: List<DraftUser>,
)

data class DraftUser(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String?,
) {
    val fullName: String
        get() = listOf(lastName, firstName)
            .filter(String::isNotBlank)
            .joinToString(separator = " ")
            .ifBlank { email ?: id }
}
