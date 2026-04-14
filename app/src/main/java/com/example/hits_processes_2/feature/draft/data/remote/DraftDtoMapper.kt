package com.example.hits_processes_2.feature.draft.data.remote

import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftPickTurnDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftTeamDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftStudentJoinedTeamDto
import com.example.hits_processes_2.feature.draft.data.remote.dto.DraftUserDto
import com.example.hits_processes_2.feature.draft.domain.model.Draft
import com.example.hits_processes_2.feature.draft.domain.model.DraftPickTurn
import com.example.hits_processes_2.feature.draft.domain.model.DraftTeam
import com.example.hits_processes_2.feature.draft.domain.model.DraftUser

fun DraftDto.toDomain(): Draft {
    return Draft(
        id = id,
        currentSelectingUser = (currentSelectingCaptain ?: currentSelectingUser)?.toDomain(),
        pickTurns = draftPickTurns.map(DraftPickTurnDto::toDomain),
        teams = teams.mapIndexed { index, team -> team.toDomain(fallbackNumber = index + 1) },
        isStarted = isStarted,
        isEnded = isEnded,
    )
}

fun DraftStudentJoinedTeamDto.toDomainUser(): DraftUser = user.toDomain()

fun DraftPickTurnDto.toDomain(): DraftPickTurn {
    return DraftPickTurn(
        id = id,
        user = user.toDomain(),
    )
}

fun DraftTeamDto.toDomain(fallbackNumber: Int): DraftTeam {
    return DraftTeam(
        id = id,
        name = name,
        number = name?.extractTeamNumber() ?: fallbackNumber,
        captain = captain?.toDomain(),
        members = (members + listOfNotNull(captain))
            .distinctBy(DraftUserDto::id)
            .map(DraftUserDto::toDomain),
    )
}

fun DraftUserDto.toDomain(): DraftUser {
    return DraftUser(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
    )
}

private fun String.extractTeamNumber(): Int? {
    return filter { it.isDigit() }.toIntOrNull()
}
