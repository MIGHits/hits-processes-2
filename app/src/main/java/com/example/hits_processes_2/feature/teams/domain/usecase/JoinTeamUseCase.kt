package com.example.hits_processes_2.feature.teams.domain.usecase

import com.example.hits_processes_2.feature.teams.domain.repository.TeamsRepository

class JoinTeamUseCase(
    private val repository: TeamsRepository,
) {
    suspend operator fun invoke(
        courseId: String,
        taskId: String,
        teamId: String,
    ) = repository.joinTeam(courseId, taskId, teamId)
}
