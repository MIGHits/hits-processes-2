package com.example.hits_processes_2.feature.teams.domain.usecase

import com.example.hits_processes_2.feature.teams.domain.repository.TeamsRepository

class EvaluateTeamAnswerUseCase(
    private val repository: TeamsRepository,
) {
    suspend operator fun invoke(
        taskAnswerId: String,
        grade: Int,
    ) = repository.evaluateTeamAnswer(taskAnswerId, grade)
}
