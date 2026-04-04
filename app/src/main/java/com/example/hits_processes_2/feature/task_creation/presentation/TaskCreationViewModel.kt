package com.example.hits_processes_2.feature.task_creation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskCreationViewModel : ViewModel() {

    private val _state = MutableStateFlow(TaskCreationUiState())
    val state: StateFlow<TaskCreationUiState> = _state.asStateFlow()

    private val _effects = Channel<TaskCreationUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: TaskCreationUiEvent) {
        when (event) {
            is TaskCreationUiEvent.TaskTextChanged -> updateState {
                copy(taskText = event.text)
            }

            is TaskCreationUiEvent.DeadlineSelected -> updateState {
                copy(deadlineMillis = event.millis)
            }

            is TaskCreationUiEvent.FilesSelected -> updateState {
                copy(attachedFiles = attachedFiles + event.files)
            }

            is TaskCreationUiEvent.FileRemoved -> updateState {
                copy(attachedFiles = attachedFiles.toMutableList().also { it.removeAt(event.index) })
            }

            is TaskCreationUiEvent.TeamFormationRuleSelected -> updateState {
                copy(
                    teamFormationRule = event.rule,
                    isTeamFormationDropdownExpanded = false,
                )
            }

            is TaskCreationUiEvent.TeamCountChanged -> updateState {
                copy(teamCount = event.count.coerceAtLeast(1))
            }

            is TaskCreationUiEvent.SubmissionStrategySelected -> updateState {
                copy(
                    submissionStrategy = event.strategy,
                    isSubmissionStrategyDropdownExpanded = false,
                )
            }

            TaskCreationUiEvent.TeamFormationDropdownToggled -> updateState {
                copy(isTeamFormationDropdownExpanded = !isTeamFormationDropdownExpanded)
            }

            TaskCreationUiEvent.SubmissionStrategyDropdownToggled -> updateState {
                copy(isSubmissionStrategyDropdownExpanded = !isSubmissionStrategyDropdownExpanded)
            }

            TaskCreationUiEvent.CreateTaskClicked -> createTask()

            TaskCreationUiEvent.BackClicked -> sendEffect(TaskCreationUiEffect.NavigateBack)
        }
    }

    private fun createTask() {
        val snapshot = _state.value

        if (snapshot.taskText.isBlank()) {
            sendEffect(TaskCreationUiEffect.ShowError("Введите текст задания"))
            return
        }
        if (snapshot.deadlineMillis == null) {
            sendEffect(TaskCreationUiEffect.ShowError("Укажите дедлайн"))
            return
        }
        if (snapshot.teamFormationRule == null) {
            sendEffect(TaskCreationUiEffect.ShowError("Выберите правило формирования команд"))
            return
        }
        if (snapshot.submissionStrategy == null) {
            sendEffect(TaskCreationUiEffect.ShowError("Выберите стратегию сдачи"))
            return
        }

        viewModelScope.launch {
            updateState { copy(isCreating = true) }
            // TODO: вызов use case для создания задания
            updateState { copy(isCreating = false) }
            sendEffect(TaskCreationUiEffect.TaskCreated)
        }
    }

    private fun updateState(transform: TaskCreationUiState.() -> TaskCreationUiState) {
        _state.value = _state.value.transform()
    }

    private fun sendEffect(effect: TaskCreationUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
