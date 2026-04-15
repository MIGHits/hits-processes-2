package com.example.hits_processes_2.feature.draft.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.draft.data.repository.DraftException
import com.example.hits_processes_2.feature.draft.domain.model.DraftRealtimeEvent
import com.example.hits_processes_2.feature.draft.domain.usecase.GetDraftUseCase
import com.example.hits_processes_2.feature.draft.domain.usecase.ObserveDraftUseCase
import com.example.hits_processes_2.feature.courses.domain.usecase.GetMyProfileUseCase
import com.example.hits_processes_2.feature.draft.domain.model.Draft
import com.example.hits_processes_2.feature.draft.domain.model.DraftPickTurn
import com.example.hits_processes_2.feature.draft.domain.model.DraftTeam
import com.example.hits_processes_2.feature.teams.domain.usecase.AddTeamMemberUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.GetFreeStudentsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class DraftViewModel(
    private val getDraftUseCase: GetDraftUseCase,
    private val observeDraftUseCase: ObserveDraftUseCase,
    private val getFreeStudentsUseCase: GetFreeStudentsUseCase,
    private val addTeamMemberUseCase: AddTeamMemberUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<DraftScreenState>(DraftScreenState.Loading)
    val state: StateFlow<DraftScreenState> = _state.asStateFlow()

    private val _effects = Channel<DraftUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var observeJob: Job? = null
    private var courseId: String = ""
    private var taskId: String = ""
    private var draftId: String = ""

    fun load(
        courseId: String,
        taskId: String,
        draftId: String,
        currentUserId: String?,
    ) {
        this.courseId = courseId
        this.taskId = taskId
        this.draftId = draftId
        observeJob?.cancel()
        viewModelScope.launch {
            val resolvedCurrentUserId = currentUserId ?: getMyProfileUseCase().getOrNull()?.id
            _state.value = DraftScreenState.Loading
            getDraftUseCase(draftId)
                .onSuccess { draft ->
                    val isCurrentUserTurn = draft.isCaptainTurn(resolvedCurrentUserId)
                    _state.value = DraftScreenState.Content(
                        draft = draft,
                        currentUserId = resolvedCurrentUserId,
                        isPickDialogVisible = isCurrentUserTurn,
                        pickDialogGeneration = if (isCurrentUserTurn) 1 else 0,
                    )
                    if (isCurrentUserTurn) {
                        loadFreeStudents()
                    }
                    if (!draft.isEnded) {
                        observeDraft(draftId)
                    } else {
                        sendEffect(DraftUiEffect.OpenTeams)
                    }
                }
                .onFailure { error ->
                    _state.value = DraftScreenState.Error(error.toReadableMessage())
                }
        }
    }

    fun retry(courseId: String, taskId: String, draftId: String, currentUserId: String?) {
        load(courseId, taskId, draftId, currentUserId)
    }

    fun dismissPickDialog() {
        updateContent { copy(isPickDialogVisible = false) }
    }

    fun selectStudent(studentId: String) {
        val content = state.value as? DraftScreenState.Content ?: return
        val teamId = content.draft.currentCaptainTeamId(content.currentUserId)
        if (teamId == null) {
            updateContent { copy(errorMessage = "Не удалось определить команду капитана") }
            return
        }

        viewModelScope.launch {
            updateContent { copy(isRefreshing = true, errorMessage = null) }
            addTeamMemberUseCase(courseId, taskId, teamId, studentId)
                .onSuccess {
                    refreshDraftAfterPick()
                    updateContent {
                        copy(
                            isRefreshing = false,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    updateContent {
                        copy(
                            isRefreshing = false,
                            errorMessage = error.message ?: "Не удалось выбрать студента",
                        )
                    }
                }
        }
    }

    private fun observeDraft(draftId: String) {
        observeJob = viewModelScope.launch {
            observeDraftUseCase(draftId).collect { event ->
                reduceRealtimeEvent(event)
            }
        }
    }

    private fun reduceRealtimeEvent(event: DraftRealtimeEvent) {
        val content = state.value as? DraftScreenState.Content ?: return
        val updatedState = when (event) {
            is DraftRealtimeEvent.DraftStarted -> content.withDraftUpdate(event.draft, event)
            is DraftRealtimeEvent.OrderOfSelectionChanged -> content.withPickTurnsUpdate(event.pickTurns, event)
            is DraftRealtimeEvent.DraftEnded -> {
                observeJob?.cancel()
                sendEffect(DraftUiEffect.OpenTeams)
                content.copy(
                    draft = event.draft ?: content.draft.copy(isEnded = true),
                    isPickDialogVisible = false,
                    lastRealtimeEvent = event,
                    errorMessage = null,
                )
            }
            is DraftRealtimeEvent.Error -> content.copy(
                lastRealtimeEvent = event,
                errorMessage = event.message,
            )
            is DraftRealtimeEvent.StudentJoinedTeam -> content.copy(
                draft = content.draft.copy(
                    teams = content.draft.teams.map { team ->
                        if (team.id == event.teamId) {
                            team.copy(members = (team.members + event.user).distinctBy { it.id })
                        } else {
                            team
                        }
                    },
                ),
                availableStudents = content.availableStudents.filterNot { it.id == event.user.id },
                lastRealtimeEvent = event,
                errorMessage = null,
            )
            is DraftRealtimeEvent.TeamStructureChanged -> content.withTeamUpdate(event.changedTeam, event)
            DraftRealtimeEvent.AutoSelectionPerformed -> content.copy(
                isPickDialogVisible = false,
                lastRealtimeEvent = event,
                errorMessage = null,
            )
            DraftRealtimeEvent.TimeToChooseStudent,
            is DraftRealtimeEvent.Unknown -> content.copy(
                lastRealtimeEvent = event,
                errorMessage = null,
            )
        }
        _state.value = updatedState
        if (event is DraftRealtimeEvent.TimeToChooseStudent) {
            loadFreeStudents()
            updateContent { withPickDialogVisibility(isVisible = true, resetTimer = true) }
        } else if (event is DraftRealtimeEvent.AutoSelectionPerformed) {
            refreshDraftAfterRealtimeUpdate(openPickDialogIfCurrentTurn = false)
        } else if (event is DraftRealtimeEvent.StudentJoinedTeam) {
            refreshDraftAfterRealtimeUpdate()
        } else if (event is DraftRealtimeEvent.TeamStructureChanged) {
            refreshDraftAfterRealtimeUpdate()
        } else if (
            event is DraftRealtimeEvent.DraftStarted ||
            event is DraftRealtimeEvent.OrderOfSelectionChanged
        ) {
            val currentContent = updatedState as? DraftScreenState.Content
            if (currentContent?.isPickDialogVisible == true) {
                loadFreeStudents()
            }
        }
    }

    private fun DraftScreenState.Content.withDraftUpdate(
        draft: Draft,
        event: DraftRealtimeEvent,
    ): DraftScreenState.Content {
        return copy(
            draft = draft,
            lastRealtimeEvent = event,
            errorMessage = null,
        ).withPickDialogVisibility(draft.isCaptainTurn(currentUserId))
    }

    private fun DraftScreenState.Content.withPickTurnsUpdate(
        pickTurns: List<DraftPickTurn>,
        event: DraftRealtimeEvent,
    ): DraftScreenState.Content {
        val draft = draft.copy(
            currentSelectingUser = pickTurns.firstOrNull()?.user,
            pickTurns = pickTurns,
            isStarted = true,
        )
        return copy(
            draft = draft,
            lastRealtimeEvent = event,
            errorMessage = null,
        ).withPickDialogVisibility(draft.isCaptainTurn(currentUserId))
    }

    private fun DraftScreenState.Content.withTeamUpdate(
        changedTeam: DraftTeam,
        event: DraftRealtimeEvent,
    ): DraftScreenState.Content {
        val draft = draft.copy(
            teams = draft.teams.mapIndexed { index, team ->
                if (team.id == changedTeam.id) {
                    changedTeam.copy(number = team.number.takeIf { it > 0 } ?: index + 1)
                } else {
                    team
                }
            },
        )
        return copy(
            draft = draft,
            lastRealtimeEvent = event,
            errorMessage = null,
        )
    }

    private fun loadFreeStudents() {
        if (courseId.isBlank() || taskId.isBlank()) return
        viewModelScope.launch {
            getFreeStudentsUseCase(courseId, taskId)
                .onSuccess { students ->
                    updateContent {
                        copy(
                            availableStudents = students.map { it.toDraftStudent() },
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    updateContent {
                        copy(errorMessage = error.message ?: "Не удалось загрузить свободных студентов")
                    }
                }
        }
    }

    private suspend fun refreshDraftAfterPick(openPickDialogIfCurrentTurn: Boolean = true) {
        if (draftId.isBlank()) return
        getDraftUseCase(draftId)
            .onSuccess { draft ->
                var shouldLoadFreeStudents = false
                updateContent {
                    shouldLoadFreeStudents = openPickDialogIfCurrentTurn && draft.isCaptainTurn(currentUserId)
                    copy(
                        draft = draft,
                        errorMessage = null,
                    ).withPickDialogVisibility(
                        isVisible = shouldLoadFreeStudents,
                        resetTimer = shouldLoadFreeStudents,
                    )
                }
                if (shouldLoadFreeStudents) {
                    loadFreeStudents()
                }
            }
    }

    private fun refreshDraftAfterRealtimeUpdate(openPickDialogIfCurrentTurn: Boolean = true) {
        if (draftId.isBlank()) return
        viewModelScope.launch {
            refreshDraftAfterPick(openPickDialogIfCurrentTurn)
        }
    }

    private fun DraftScreenState.Content.withPickDialogVisibility(
        isVisible: Boolean,
        resetTimer: Boolean = false,
    ): DraftScreenState.Content {
        return copy(
            isPickDialogVisible = isVisible,
            pickDialogGeneration = if (isVisible && (!isPickDialogVisible || resetTimer)) {
                pickDialogGeneration + 1
            } else {
                pickDialogGeneration
            },
        )
    }

    private fun updateContent(transform: DraftScreenState.Content.() -> DraftScreenState.Content) {
        val content = state.value as? DraftScreenState.Content ?: return
        _state.value = content.transform()
    }

    private fun sendEffect(effect: DraftUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}

private fun Throwable.toReadableMessage(): String {
    return when (this) {
        is DraftException -> message
        else -> message ?: "Что-то пошло не так"
    }
}
