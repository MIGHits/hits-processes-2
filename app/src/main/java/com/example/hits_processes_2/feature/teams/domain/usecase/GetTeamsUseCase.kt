package com.example.hits_processes_2.feature.teams.domain.usecase

import com.example.hits_processes_2.feature.teams.domain.repository.TeamsRepository

class GetTeamsUseCase(
    private val repository: TeamsRepository,
) {
    suspend operator fun invoke(
        courseId: String,
        taskId: String,
    ) = repository.getTeams(courseId, taskId)
}
