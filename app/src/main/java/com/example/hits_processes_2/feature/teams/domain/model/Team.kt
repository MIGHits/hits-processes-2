package com.example.hits_processes_2.feature.teams.domain.model

data class Team(
    val id: String,
    val number: Int,
    val members: List<TeamMember>,
    val finalAnswerId: String? = null,
    val taskAnswerId: String? = null,
    val submissionFileId: String? = null,
    val submission: String? = null,
    val submittedAt: String? = null,
    val status: TeamSubmissionStatus = TeamSubmissionStatus.NOT_SUBMITTED,
    val grade: Int? = null,
)

data class TeamMember(
    val id: String,
    val fullName: String,
    val isCaptain: Boolean = false,
)

enum class TeamSubmissionStatus {
    SUBMITTED,
    LATE,
    NOT_SUBMITTED,
}
