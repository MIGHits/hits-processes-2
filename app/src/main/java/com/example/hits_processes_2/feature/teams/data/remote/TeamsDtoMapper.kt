package com.example.hits_processes_2.feature.teams.data.remote

import com.example.hits_processes_2.feature.teams.data.remote.dto.FinalTaskAnswerDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.TeamDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.TeamShortDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.UserDto
import com.example.hits_processes_2.feature.teams.domain.model.Team
import com.example.hits_processes_2.feature.teams.domain.model.TeamMember
import com.example.hits_processes_2.feature.teams.domain.model.TeamSubmissionStatus

fun TeamDto.toDomain(
    shortInfo: TeamShortDto? = null,
    fallbackNumber: Int,
): Team {
    val captainId = captain?.id
    val uniqueMembers = (members + listOfNotNull(captain)).distinctBy(UserDto::id)
    val embeddedFinalAnswer = finalAnswer ?: finalTaskAnswer
    val embeddedFinalAnswerFile = embeddedFinalAnswer?.taskAnswer?.files?.firstOrNull()

    return Team(
        id = id,
        number = shortInfo?.name?.extractTeamNumber() ?: name?.extractTeamNumber() ?: fallbackNumber,
        members = uniqueMembers.map { member ->
            TeamMember(
                id = member.id,
                fullName = member.fullName,
                isCaptain = member.id == captainId,
            )
        },
        finalAnswerId = embeddedFinalAnswer?.id
            ?: finalAnswerId
            ?: finalTaskAnswerId
            ?: teamFinalAnswerId
            ?: teamFinalTaskAnswerId,
        taskAnswerId = embeddedFinalAnswer?.taskAnswer?.id ?: taskAnswerId,
        submissionFileId = submission?.id ?: embeddedFinalAnswerFile?.id,
        submission = submission?.fileName ?: embeddedFinalAnswerFile?.fileName,
        submittedAt = submittedAt ?: embeddedFinalAnswer?.submittedAt,
        status = embeddedFinalAnswer?.status.toFinalAnswerStatus() ?: status.toSubmissionStatus(),
        grade = grade ?: score ?: embeddedFinalAnswer?.score,
    )
}

fun UserDto.toTeamMember(): TeamMember {
    return TeamMember(
        id = id,
        fullName = fullName,
    )
}

private val UserDto.fullName: String
    get() = listOfNotNull(lastName, firstName)
        .joinToString(separator = " ")
        .ifBlank { email ?: id }

private fun String.extractTeamNumber(): Int? {
    return filter { it.isDigit() }.toIntOrNull()
}

private fun String?.toSubmissionStatus(): TeamSubmissionStatus {
    return when (this) {
        "SUBMITTED" -> TeamSubmissionStatus.SUBMITTED
        "LATE" -> TeamSubmissionStatus.LATE
        "COMPLETED" -> TeamSubmissionStatus.SUBMITTED
        "COMPLETED_AFTER_DEADLINE", "COMPETED_AFTER_DEADLINE" -> TeamSubmissionStatus.LATE
        "NOT_COMPLETED" -> TeamSubmissionStatus.NOT_SUBMITTED
        else -> TeamSubmissionStatus.NOT_SUBMITTED
    }
}

private fun String?.toFinalAnswerStatus(): TeamSubmissionStatus? {
    return when (this) {
        "COMPLETED" -> TeamSubmissionStatus.SUBMITTED
        "COMPLETED_AFTER_DEADLINE", "COMPETED_AFTER_DEADLINE" -> TeamSubmissionStatus.LATE
        "NOT_COMPLETED" -> TeamSubmissionStatus.NOT_SUBMITTED
        else -> null
    }
}

fun Team.withFinalAnswer(finalAnswer: FinalTaskAnswerDto?): Team {
    val file = finalAnswer?.taskAnswer?.files?.firstOrNull()
    return copy(
        finalAnswerId = finalAnswer?.id ?: finalAnswerId,
        taskAnswerId = finalAnswer?.taskAnswer?.id ?: taskAnswerId,
        submissionFileId = file?.id ?: submissionFileId,
        submission = file?.fileName ?: submission,
        submittedAt = finalAnswer?.submittedAt ?: submittedAt,
        status = finalAnswer?.status.toFinalAnswerStatus() ?: status,
        grade = finalAnswer?.score ?: grade,
    )
}
