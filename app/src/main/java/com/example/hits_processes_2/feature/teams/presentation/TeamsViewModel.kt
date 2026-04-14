package com.example.hits_processes_2.feature.teams.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.courses.domain.usecase.GetMyProfileUseCase
import com.example.hits_processes_2.feature.teams.data.repository.TeamsException
import com.example.hits_processes_2.feature.teams.domain.model.Team
import com.example.hits_processes_2.feature.teams.domain.usecase.AddTeamMemberUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.AssignTeamCaptainUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.EvaluateTeamAnswerUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.GetFreeStudentsUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.GetTeamsUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.JoinTeamUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.LeaveTeamUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.RemoveTeamMemberUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamsViewModel(
    private val getTeamsUseCase: GetTeamsUseCase,
    private val getFreeStudentsUseCase: GetFreeStudentsUseCase,
    private val joinTeamUseCase: JoinTeamUseCase,
    private val leaveTeamUseCase: LeaveTeamUseCase,
    private val addTeamMemberUseCase: AddTeamMemberUseCase,
    private val removeTeamMemberUseCase: RemoveTeamMemberUseCase,
    private val assignTeamCaptainUseCase: AssignTeamCaptainUseCase,
    private val evaluateTeamAnswerUseCase: EvaluateTeamAnswerUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<TeamsScreenState>(TeamsScreenState.Loading)
    val state: StateFlow<TeamsScreenState> = _state.asStateFlow()

    private var lastRequest: TeamsLoadRequest? = null

    fun load(
        courseId: String,
        taskId: String,
        userRole: UserRole,
        teamFormation: TeamFormation,
        userTeamId: String?,
    ) {
        val request = TeamsLoadRequest(
            courseId = courseId,
            taskId = taskId,
            userRole = userRole,
            teamFormation = teamFormation,
            userTeamId = userTeamId,
        )
        lastRequest = request
        load(request, showFullScreenLoading = true)
    }

    fun retry() {
        lastRequest?.let { request ->
            load(request, showFullScreenLoading = true)
        }
    }

    fun joinTeam(teamId: String) {
        executeMutation(
            updateRequestOnSuccess = { copy(userTeamId = teamId) },
            mutation = { request -> joinTeamUseCase(request.courseId, request.taskId, teamId) },
        )
    }

    fun leaveTeam() {
        val teamId = (state.value as? TeamsScreenState.Content)?.userTeamId ?: return
        executeMutation(
            updateRequestOnSuccess = { copy(userTeamId = null) },
            mutation = { request -> leaveTeamUseCase(request.courseId, request.taskId, teamId) },
        )
    }

    fun addStudent(teamId: String, studentId: String) {
        executeMutation { request ->
            addTeamMemberUseCase(request.courseId, request.taskId, teamId, studentId)
        }
    }

    fun removeStudent(teamId: String, studentId: String) {
        executeMutation { request ->
            removeTeamMemberUseCase(request.courseId, request.taskId, teamId, studentId)
        }
    }

    fun setCaptain(teamId: String, studentId: String) {
        executeMutation { request ->
            assignTeamCaptainUseCase(request.courseId, request.taskId, teamId, studentId)
        }
    }

    fun onGradeInputChange(teamId: String, value: String) {
        updateContent { content ->
            content.copy(
                gradeInputs = content.gradeInputs + (teamId to value),
                errorMessage = null,
            )
        }
    }

    fun saveGrade(teamId: String, grade: Int) {
        val content = state.value as? TeamsScreenState.Content ?: return
        val taskAnswerId = content.teams.firstOrNull { it.id == teamId }?.taskAnswerId
        if (taskAnswerId == null) {
            _state.value = content.copy(errorMessage = "Нет решения, которое можно оценить")
            return
        }

        executeMutation { evaluateTeamAnswerUseCase(taskAnswerId, grade) }
    }

    fun saveGrade(teamId: String) {
        // The current spec has no grade endpoint for teams yet, so keep the UI explicit.
        updateContent { content ->
            val gradeInputs = if (teamId in content.gradeInputs) {
                content.gradeInputs
            } else {
                content.gradeInputs + (teamId to "")
            }
            content.copy(
                gradeInputs = gradeInputs,
                errorMessage = "Сохранение оценки пока не поддержано API",
            )
        }
    }

    private fun executeMutation(
        updateRequestOnSuccess: TeamsLoadRequest.() -> TeamsLoadRequest = { this },
        mutation: suspend (TeamsLoadRequest) -> Result<Unit>,
    ) {
        val request = lastRequest ?: return
        val content = state.value as? TeamsScreenState.Content ?: return

        viewModelScope.launch {
            _state.value = content.copy(isRefreshing = true, errorMessage = null)
            mutation(request)
                .onSuccess {
                    val updatedRequest = request.updateRequestOnSuccess()
                    lastRequest = updatedRequest
                    loadSuspend(updatedRequest, showFullScreenLoading = false)
                }
                .onFailure { error ->
                    _state.value = content.copy(
                        isRefreshing = false,
                        errorMessage = error.toReadableMessage(),
                    )
                }
        }
    }

    private fun load(
        request: TeamsLoadRequest,
        showFullScreenLoading: Boolean,
    ) {
        viewModelScope.launch {
            loadSuspend(request, showFullScreenLoading)
        }
    }

    private suspend fun loadSuspend(
        request: TeamsLoadRequest,
        showFullScreenLoading: Boolean,
    ) {
        val previousContent = state.value as? TeamsScreenState.Content
        if (showFullScreenLoading) {
            _state.value = TeamsScreenState.Loading
        } else if (previousContent != null) {
            _state.value = previousContent.copy(isRefreshing = true, errorMessage = null)
        }

        getTeamsUseCase(request.courseId, request.taskId)
            .mapCatching { teams ->
                val userTeamId = request.userTeamId ?: request.resolveUserTeamId(teams)
                val availableStudents = if (
                    request.isTeacher &&
                    request.teamFormation == TeamFormation.CUSTOM
                ) {
                    getFreeStudentsUseCase(request.courseId, request.taskId).getOrThrow()
                } else {
                    emptyList()
                }

                TeamsScreenState.Content(
                    teams = teams.map { it.toUi() },
                    userRole = request.userRole,
                    teamFormation = request.teamFormation,
                    userTeamId = userTeamId,
                    availableStudents = availableStudents.map { it.toUi() },
                    gradeInputs = previousContent?.gradeInputs.orEmpty(),
                    isRefreshing = false,
                    errorMessage = null,
                )
            }
            .onSuccess { content ->
                _state.value = content
            }
            .onFailure { error ->
                _state.value = if (showFullScreenLoading || previousContent == null) {
                    TeamsScreenState.Error(error.toReadableMessage())
                } else {
                    previousContent.copy(
                        isRefreshing = false,
                        errorMessage = error.toReadableMessage(),
                    )
                }
            }
    }

    private fun updateContent(
        transform: (TeamsScreenState.Content) -> TeamsScreenState.Content,
    ) {
        val content = state.value as? TeamsScreenState.Content ?: return
        _state.value = transform(content)
    }

    private suspend fun TeamsLoadRequest.resolveUserTeamId(teams: List<Team>): String? {
        if (!isStudentFreeFormation) return null

        val currentUserId = getMyProfileUseCase().getOrNull()?.id ?: return null
        return teams.firstOrNull { team ->
            team.members.any { member -> member.id == currentUserId }
        }?.id
    }
}

private data class TeamsLoadRequest(
    val courseId: String,
    val taskId: String,
    val userRole: UserRole,
    val teamFormation: TeamFormation,
    val userTeamId: String?,
) {
    val isTeacher: Boolean
        get() = userRole == UserRole.TEACHER || userRole == UserRole.MAIN_TEACHER

    val isStudentFreeFormation: Boolean
        get() = userRole == UserRole.STUDENT && teamFormation == TeamFormation.STUDENTS
}

private fun Throwable.toReadableMessage(): String {
    return when (this) {
        is TeamsException -> message
        else -> message ?: "Что-то пошло не так"
    }
}
