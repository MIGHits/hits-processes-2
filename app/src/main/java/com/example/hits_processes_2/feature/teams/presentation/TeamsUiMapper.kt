package com.example.hits_processes_2.feature.teams.presentation

import com.example.hits_processes_2.feature.teams.domain.model.TeamSubmissionStatus
import com.example.hits_processes_2.feature.teams.domain.model.Team as DomainTeam
import com.example.hits_processes_2.feature.teams.domain.model.TeamMember as DomainTeamMember

fun DomainTeam.toUi(): Team {
    return Team(
        id = id,
        number = number,
        members = members.map(DomainTeamMember::toUi),
        finalAnswerId = finalAnswerId,
        taskAnswerId = taskAnswerId,
        submissionFileId = submissionFileId,
        submission = submission,
        submittedAt = submittedAt,
        status = status.toUi(),
        grade = grade,
    )
}

fun DomainTeamMember.toUi(): TeamMember {
    return TeamMember(
        id = id,
        fullName = fullName,
        isCaptain = isCaptain,
    )
}

private fun TeamSubmissionStatus.toUi(): SubmissionStatus {
    return when (this) {
        TeamSubmissionStatus.SUBMITTED -> SubmissionStatus.SUBMITTED
        TeamSubmissionStatus.LATE -> SubmissionStatus.LATE
        TeamSubmissionStatus.NOT_SUBMITTED -> SubmissionStatus.NOT_SUBMITTED
    }
}
