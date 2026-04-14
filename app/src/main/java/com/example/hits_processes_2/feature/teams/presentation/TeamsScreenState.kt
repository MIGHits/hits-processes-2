package com.example.hits_processes_2.feature.teams.presentation

sealed class TeamsScreenState {
    data object Loading : TeamsScreenState()

    data class Content(
        val teams: List<Team>,
        val userRole: UserRole,
        val teamFormation: TeamFormation,
        val userTeamId: String?,
        val availableStudents: List<TeamMember>,
        val gradeInputs: Map<String, String> = emptyMap(),
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null,
    ) : TeamsScreenState()

    data class Error(
        val message: String,
    ) : TeamsScreenState()
}
