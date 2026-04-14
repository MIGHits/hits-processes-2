package com.example.hits_processes_2.feature.captain_selection.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.captain_selection.domain.usecase.AssignDraftCaptainUseCase
import com.example.hits_processes_2.feature.captain_selection.domain.usecase.GetCaptainSelectionUseCase
import com.example.hits_processes_2.feature.captain_selection.domain.usecase.RemoveDraftCaptainUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CaptainSelectionViewModel(
    private val getCaptainSelectionUseCase: GetCaptainSelectionUseCase,
    private val assignDraftCaptainUseCase: AssignDraftCaptainUseCase,
    private val removeDraftCaptainUseCase: RemoveDraftCaptainUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<CaptainSelectionScreenState>(CaptainSelectionScreenState.Loading)
    val state: StateFlow<CaptainSelectionScreenState> = _state.asStateFlow()

    private val _effects = Channel<CaptainSelectionUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    private var courseId: String = ""
    private var taskId: String = ""

    fun load(courseId: String, taskId: String) {
        this.courseId = courseId
        this.taskId = taskId
        load(showFullScreenLoading = true)
    }

    fun retry() {
        load(showFullScreenLoading = true)
    }

    fun assignCaptain(studentId: String) {
        executeMutation {
            assignDraftCaptainUseCase(courseId, taskId, studentId)
        }
    }

    fun removeCaptain(candidate: CaptainCandidate) {
        val teamId = candidate.teamId ?: return
        executeMutation {
            removeDraftCaptainUseCase(courseId, taskId, teamId, candidate.id)
        }
    }

    private fun executeMutation(
        mutation: suspend () -> Result<Unit>,
    ) {
        val content = state.value as? CaptainSelectionScreenState.Content ?: return

        viewModelScope.launch {
            _state.value = content.copy(isRefreshing = true, errorMessage = null)
            mutation()
                .onSuccess {
                    loadSuspend(showFullScreenLoading = false)
                }
                .onFailure { error ->
                    _state.value = content.copy(
                        isRefreshing = false,
                        errorMessage = error.toReadableMessage(),
                    )
                }
        }
    }

    private fun load(showFullScreenLoading: Boolean) {
        viewModelScope.launch {
            loadSuspend(showFullScreenLoading)
        }
    }

    private suspend fun loadSuspend(showFullScreenLoading: Boolean) {
        val previousContent = state.value as? CaptainSelectionScreenState.Content
        if (showFullScreenLoading) {
            _state.value = CaptainSelectionScreenState.Loading
        } else if (previousContent != null) {
            _state.value = previousContent.copy(isRefreshing = true, errorMessage = null)
        }

        getCaptainSelectionUseCase(courseId, taskId)
            .map { it.toUi() }
            .onSuccess { content ->
                _state.value = content
                if (content.isSelectionComplete) {
                    _effects.send(CaptainSelectionUiEffect.CaptainsSelected)
                }
            }
            .onFailure { error ->
                _state.value = if (showFullScreenLoading || previousContent == null) {
                    CaptainSelectionScreenState.Error(error.toReadableMessage())
                } else {
                    previousContent.copy(
                        isRefreshing = false,
                        errorMessage = error.toReadableMessage(),
                    )
                }
            }
    }
}

private val CaptainSelectionScreenState.Content.isSelectionComplete: Boolean
    get() = requiredCaptainsCount > 0 && candidates.count(CaptainCandidate::isCaptain) >= requiredCaptainsCount

private fun Throwable.toReadableMessage(): String = message ?: "Что-то пошло не так"
