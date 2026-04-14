package com.example.hits_processes_2.feature.draft.presentation

data class DraftTeam(
    val id: String,
    val number: Int,
    val members: List<DraftTeamMember>,
)

data class DraftTeamMember(
    val id: String,
    val fullName: String,
    val isCaptain: Boolean = false,
)

data class DraftStudent(
    val id: String,
    val fullName: String,
)
