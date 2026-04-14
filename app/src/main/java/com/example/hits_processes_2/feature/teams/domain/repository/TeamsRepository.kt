package com.example.hits_processes_2.feature.teams.domain.repository

import com.example.hits_processes_2.feature.teams.domain.model.Team
import com.example.hits_processes_2.feature.teams.domain.model.TeamMember

interface TeamsRepository {
    suspend fun getTeams(
        courseId: String,
        taskId: String,
    ): Result<List<Team>>

    suspend fun getFreeStudents(
        courseId: String,
        taskId: String,
    ): Result<List<TeamMember>>

    suspend fun joinTeam(
        courseId: String,
        taskId: String,
        teamId: String,
    ): Result<Unit>

    suspend fun leaveTeam(
        courseId: String,
        taskId: String,
        teamId: String,
    ): Result<Unit>

    suspend fun addTeamMember(
        courseId: String,
        taskId: String,
        teamId: String,
        studentId: String,
    ): Result<Unit>

    suspend fun removeTeamMember(
        courseId: String,
        taskId: String,
        teamId: String,
        teamMemberId: String,
    ): Result<Unit>

    suspend fun assignTeamCaptain(
        courseId: String,
        taskId: String,
        teamId: String,
        studentId: String,
    ): Result<Unit>

    suspend fun evaluateTeamAnswer(
        taskAnswerId: String,
        grade: Int,
    ): Result<Unit>
}
