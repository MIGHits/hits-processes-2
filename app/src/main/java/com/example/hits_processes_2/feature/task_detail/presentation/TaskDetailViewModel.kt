package com.example.hits_processes_2.feature.task_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.resources.StringResourceProvider
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.draft.domain.usecase.GetDraftUseCase
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
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
    private val getDraftUseCase: GetDraftUseCase,
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
            TaskDetailUiEvent.TeamsClicked -> openTeamsOrDraft()
            TaskDetailUiEvent.CaptainSelectionClicked -> openCaptainSelection()
            TaskDetailUiEvent.EvaluateClicked -> sendMessage(R.string.task_detail_evaluate_unavailable)
            TaskDetailUiEvent.EditClicked -> sendMessage(R.string.task_detail_edit_unavailable)
        }
    }

    fun refreshCaptainSelectionAction() {
        val task = _state.value.task ?: return
        if (!task.isDraft || resolvedUserRole != CourseDetailsRole.TEACHER && resolvedUserRole != CourseDetailsRole.HEAD_TEACHER) {
            updateState { copy(showCaptainSelectionAction = false) }
            return
        }
        val draftId = task.draftId ?: return

        viewModelScope.launch {
            getDraftUseCase(draftId)
                .onSuccess { draft ->
                    updateState { copy(showCaptainSelectionAction = !draft.isStarted) }
                }
                .onFailure {
                    updateState { copy(showCaptainSelectionAction = false) }
                }
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
                            showCaptainSelectionAction = false,
                            errorMessage = null,
                        )
                    }
                    refreshCaptainSelectionAction()
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

    private fun openTeamsOrDraft() {
        val task = _state.value.task ?: return
        if (!task.isDraft) {
            sendEffect(
                TaskDetailUiEffect.OpenTeams(
                    courseId = courseId,
                    taskId = taskId,
                    teamFormationType = task.teamFormationType,
                    userRoleName = resolvedUserRole.name,
                ),
            )
            return
        }

        val draftId = task.draftId
        if (draftId == null) {
            sendMessage(R.string.task_detail_teams_unavailable)
            return
        }

        viewModelScope.launch {
            getDraftUseCase(draftId)
                .onSuccess { draft ->
                    val effect = if (draft.isEnded) {
                        TaskDetailUiEffect.OpenTeams(
                            courseId = courseId,
                            taskId = taskId,
                            teamFormationType = task.teamFormationType,
                            userRoleName = resolvedUserRole.name,
                        )
                    } else {
                        TaskDetailUiEffect.OpenDraft(
                            courseId = courseId,
                            taskId = taskId,
                            draftId = draftId,
                            userRoleName = resolvedUserRole.name,
                        )
                    }
                    sendEffect(effect)
                }
                .onFailure {
                    sendEffect(
                        TaskDetailUiEffect.OpenDraft(
                            courseId = courseId,
                            taskId = taskId,
                            draftId = draftId,
                            userRoleName = resolvedUserRole.name,
                        ),
                    )
                }
        }
    }

    private fun openCaptainSelection() {
        val task = _state.value.task ?: return
        if (!task.isDraft) return
        sendEffect(
            TaskDetailUiEffect.OpenCaptainSelection(
                courseId = courseId,
                taskId = taskId,
                draftId = task.draftId,
                userRoleName = resolvedUserRole.name,
            ),
        )
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

private val TaskDetail.isDraft: Boolean
    get() = teamFormationType.equals("DRAFT", ignoreCase = true)
