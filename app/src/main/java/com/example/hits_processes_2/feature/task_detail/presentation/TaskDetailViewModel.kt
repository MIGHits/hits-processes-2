package com.example.hits_processes_2.feature.task_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.resources.StringResourceProvider
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTaskDetailUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val courseId: String,
    private val taskId: String,
    userRoleName: String,
    private val getTaskDetailUseCase: GetTaskDetailUseCase,
    private val strings: StringResourceProvider,
) : ViewModel() {

    private val resolvedUserRole = runCatching { CourseDetailsRole.valueOf(userRoleName) }
        .getOrDefault(CourseDetailsRole.STUDENT)

    private val _state = MutableStateFlow(TaskDetailUiState(userRole = resolvedUserRole))
    val state: StateFlow<TaskDetailUiState> = _state.asStateFlow()

    private val _effects = Channel<TaskDetailUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadTaskDetail()
    }

    fun onEvent(event: TaskDetailUiEvent) {
        when (event) {
            TaskDetailUiEvent.BackClicked -> sendEffect(TaskDetailUiEffect.NavigateBack)
            TaskDetailUiEvent.RetryClicked -> loadTaskDetail()
            is TaskDetailUiEvent.FileClicked -> sendEffect(TaskDetailUiEffect.StartFileDownload(event.fileId))
            is TaskDetailUiEvent.SubmissionFilesChanged -> updateState {
                copy(submissionFiles = event.files)
            }
            is TaskDetailUiEvent.SubmissionFileRemoved -> updateState {
                copy(
                    submissionFiles = submissionFiles.toMutableList().also { files ->
                        if (event.index in files.indices) files.removeAt(event.index)
                    },
                )
            }
            TaskDetailUiEvent.SubmitClicked -> sendMessage(R.string.task_detail_submission_unavailable)
            TaskDetailUiEvent.CancelSubmissionClicked -> {
                updateState { copy(submissionFiles = emptyList()) }
                sendMessage(R.string.task_detail_submission_cleared)
            }
            TaskDetailUiEvent.TeamsClicked -> sendMessage(R.string.task_detail_teams_unavailable)
            TaskDetailUiEvent.EvaluateClicked -> sendMessage(R.string.task_detail_evaluate_unavailable)
            TaskDetailUiEvent.EditClicked -> sendMessage(R.string.task_detail_edit_unavailable)
        }
    }

    private fun loadTaskDetail() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getTaskDetailUseCase(courseId, taskId)
                .onSuccess { task ->
                    updateState {
                        copy(
                            isLoading = false,
                            task = task,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            task = null,
                            errorMessage = error.message ?: strings.getString(R.string.task_detail_error_load),
                        )
                    }
                }
        }
    }

    private fun sendMessage(resId: Int) {
        sendEffect(TaskDetailUiEffect.ShowMessage(strings.getString(resId)))
    }

    private fun updateState(transform: TaskDetailUiState.() -> TaskDetailUiState) {
        _state.value = _state.value.transform()
    }

    private fun sendEffect(effect: TaskDetailUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
