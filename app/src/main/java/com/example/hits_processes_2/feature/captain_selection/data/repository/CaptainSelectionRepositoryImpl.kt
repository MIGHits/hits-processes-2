package com.example.hits_processes_2.feature.captain_selection.data.repository

import com.example.hits_processes_2.feature.captain_selection.domain.model.CaptainSelection
import com.example.hits_processes_2.feature.captain_selection.domain.model.CaptainSelectionCandidate
import com.example.hits_processes_2.feature.captain_selection.domain.repository.CaptainSelectionRepository
import com.example.hits_processes_2.feature.teams.domain.model.Team
import com.example.hits_processes_2.feature.teams.domain.repository.TeamsRepository

class CaptainSelectionRepositoryImpl(
    private val teamsRepository: TeamsRepository,
) : CaptainSelectionRepository {

    override suspend fun getCaptainSelection(
        courseId: String,
        taskId: String,
    ): Result<CaptainSelection> = runCatching {
        val teams = teamsRepository.getTeams(courseId, taskId).getOrThrow()
        val freeStudents = teamsRepository.getFreeStudents(courseId, taskId).getOrThrow()
        val captains = teams.flatMap { team ->
            team.members
                .filter { member -> member.isCaptain }
                .map { member ->
                    CaptainSelectionCandidate(
                        id = member.id,
                        fullName = member.fullName,
                        isCaptain = true,
                        teamId = team.id,
                    )
                }
        }
        val candidates = (captains + freeStudents.map { student ->
            CaptainSelectionCandidate(
                id = student.id,
                fullName = student.fullName,
                isCaptain = false,
                teamId = null,
            )
        }).distinctBy(CaptainSelectionCandidate::id)

        CaptainSelection(
            candidates = candidates.sortedWith(
                compareByDescending<CaptainSelectionCandidate> { it.isCaptain }
                    .thenBy { it.fullName },
            ),
            requiredCaptainsCount = teams.size,
        )
    }

    override suspend fun assignCaptain(
        courseId: String,
        taskId: String,
        studentId: String,
    ): Result<Unit> = runCatching {
        val team = teamsRepository.getTeams(courseId, taskId)
            .getOrThrow()
            .firstWithoutCaptain()
        teamsRepository.assignTeamCaptain(courseId, taskId, team.id, studentId).getOrThrow()
    }

    override suspend fun removeCaptain(
        courseId: String,
        taskId: String,
        teamId: String,
        studentId: String,
    ): Result<Unit> {
        return teamsRepository.removeTeamMember(courseId, taskId, teamId, studentId)
    }

    private fun List<Team>.firstWithoutCaptain(): Team {
        return firstOrNull { team -> team.members.none { member -> member.isCaptain } }
            ?: throw IllegalStateException("Все капитаны уже выбраны")
    }
}
