package com.example.hits_processes_2.feature.draft.presentation

import com.example.hits_processes_2.feature.draft.domain.model.Draft
import com.example.hits_processes_2.feature.draft.domain.model.DraftTeam as DomainDraftTeam
import com.example.hits_processes_2.feature.draft.domain.model.DraftUser
import com.example.hits_processes_2.feature.teams.domain.model.TeamMember

fun Draft.toUiTeams(): List<DraftTeam> {
    return teams.map(DomainDraftTeam::toUi)
}

fun Draft.currentPickerName(): String? {
    return currentSelectingUser?.fullName ?: pickTurns.firstOrNull()?.user?.fullName
}

fun Draft.isCaptain(currentUserId: String?): Boolean {
    return currentUserId != null && teams.any { it.captain?.id == currentUserId }
}

fun Draft.isCaptainTurn(currentUserId: String?): Boolean {
    return currentUserId != null && currentPickerId() == currentUserId
}

fun Draft.currentCaptainTeamId(currentUserId: String?): String? {
    val captainId = currentUserId ?: currentPickerId()
    return captainId?.let { userId -> teams.firstOrNull { it.captain?.id == userId }?.id }
}

private fun Draft.currentPickerId(): String? {
    return currentSelectingUser?.id ?: pickTurns.firstOrNull()?.user?.id
}

fun TeamMember.toDraftStudent(): DraftStudent {
    return DraftStudent(
        id = id,
        fullName = fullName,
    )
}

private fun DomainDraftTeam.toUi(): DraftTeam {
    return DraftTeam(
        id = id,
        number = number,
        members = members.map { member ->
            member.toUi(isCaptain = member.id == captain?.id)
        },
    )
}

private fun DraftUser.toUi(isCaptain: Boolean): DraftTeamMember {
    return DraftTeamMember(
        id = id,
        fullName = fullName,
        isCaptain = isCaptain,
    )
}
