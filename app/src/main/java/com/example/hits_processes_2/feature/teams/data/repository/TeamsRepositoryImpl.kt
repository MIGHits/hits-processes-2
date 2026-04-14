package com.example.hits_processes_2.feature.teams.data.repository

import com.example.hits_processes_2.common.network.ApiException
import com.example.hits_processes_2.common.network.ApiResponseDto
import com.example.hits_processes_2.common.network.safeApiCall
import com.example.hits_processes_2.feature.teams.data.remote.TeamsApi
import com.example.hits_processes_2.feature.teams.data.remote.dto.FinalTaskAnswerDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.TeamDto
import com.example.hits_processes_2.feature.teams.data.remote.dto.TaskRateRequestDto
import com.example.hits_processes_2.feature.teams.data.remote.toDomain
import com.example.hits_processes_2.feature.teams.data.remote.toTeamMember
import com.example.hits_processes_2.feature.teams.data.remote.withFinalAnswer
import com.example.hits_processes_2.feature.teams.domain.model.Team
import com.example.hits_processes_2.feature.teams.domain.model.TeamMember
import com.example.hits_processes_2.feature.teams.domain.repository.TeamsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class TeamsRepositoryImpl(
    private val api: TeamsApi,
) : TeamsRepository {

    override suspend fun getTeams(
        courseId: String,
        taskId: String,
    ): Result<List<Team>> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.getTeams(courseId, taskId) },
            converter = { it.teams },
        ).mapCatching { shortTeams ->
            shortTeams.mapIndexed { index, shortTeam ->
                val team = safeApiCall(
                    apiCall = { api.getTeam(courseId, taskId, shortTeam.id) },
                    converter = { teamDto ->
                        teamDto.toDomain(
                            shortInfo = shortTeam,
                            fallbackNumber = index + 1,
                        )
                    },
                ).getOrThrow()
                team.withFinalAnswer(getTeamFinalAnswer(taskId, shortTeam.id))
            }
        }.recoverCatching { exception ->
            throw exception.toTeamsException("Не удалось загрузить команды")
        }
    }

    override suspend fun getFreeStudents(
        courseId: String,
        taskId: String,
    ): Result<List<TeamMember>> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = { api.getFreeStudents(courseId, taskId) },
            converter = { userCourseList ->
                userCourseList.userCourseList.mapNotNull { it.userModel?.toTeamMember() }
            },
        ).recoverCatching { exception ->
            throw exception.toTeamsException("Не удалось загрузить свободных студентов")
        }
    }

    override suspend fun joinTeam(
        courseId: String,
        taskId: String,
        teamId: String,
    ): Result<Unit> = executeTeamMutation("Не удалось вступить в команду") {
        api.joinTeam(courseId, taskId, teamId)
    }

    override suspend fun leaveTeam(
        courseId: String,
        taskId: String,
        teamId: String,
    ): Result<Unit> = executeTeamMutation("Не удалось выйти из команды") {
        api.leaveTeam(courseId, taskId, teamId)
    }

    override suspend fun addTeamMember(
        courseId: String,
        taskId: String,
        teamId: String,
        studentId: String,
    ): Result<Unit> = executeTeamMutation("Не удалось добавить участника") {
        api.addTeamMember(courseId, taskId, teamId, studentId)
    }

    override suspend fun removeTeamMember(
        courseId: String,
        taskId: String,
        teamId: String,
        teamMemberId: String,
    ): Result<Unit> = executeTeamMutation("Не удалось удалить участника") {
        api.removeTeamMember(courseId, taskId, teamId, teamMemberId)
    }

    override suspend fun assignTeamCaptain(
        courseId: String,
        taskId: String,
        teamId: String,
        studentId: String,
    ): Result<Unit> = executeTeamMutation("Не удалось назначить капитана") {
        api.assignTeamCaptain(courseId, taskId, teamId, studentId)
    }

    override suspend fun evaluateTeamAnswer(
        taskAnswerId: String,
        grade: Int,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.evaluateTaskAnswer(
                taskAnswerId = taskAnswerId,
                request = TaskRateRequestDto(rate = grade),
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(response.toTeamsException("Не удалось сохранить оценку"))
            }
        } catch (exception: Exception) {
            Result.failure(exception.toTeamsException("Не удалось сохранить оценку"))
        }
    }

    private suspend fun getTeamFinalAnswer(
        taskId: String,
        teamId: String,
    ): FinalTaskAnswerDto? {
        return try {
            val response = api.getTeamFinalAnswer(taskId, teamId)
            if (response.isSuccessful) response.body() else null
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun executeTeamMutation(
        defaultMessage: String,
        apiCall: suspend () -> Response<ApiResponseDto<TeamDto>>,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        safeApiCall(
            apiCall = apiCall,
            converter = { },
        ).recoverCatching { exception ->
            throw exception.toTeamsException(defaultMessage)
        }
    }
}

class TeamsException(
    val code: Int,
    override val message: String,
) : Exception(message)

private fun Throwable.toTeamsException(defaultMessage: String): TeamsException {
    return when (this) {
        is TeamsException -> this
        is ApiException -> TeamsException(code, message)
        else -> TeamsException(-1, message ?: defaultMessage)
    }
}

private fun Response<*>.toTeamsException(defaultMessage: String): TeamsException {
    val errorMessage = errorBody()?.string()
    return TeamsException(code(), errorMessage?.ifBlank { null } ?: message().ifBlank { defaultMessage })
}
