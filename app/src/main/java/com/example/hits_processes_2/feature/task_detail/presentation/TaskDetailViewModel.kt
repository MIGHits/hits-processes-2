package com.example.hits_processes_2.feature.task_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.resources.StringResourceProvider
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.draft.domain.usecase.GetDraftUseCase
import com.example.hits_processes_2.feature.file_attachment.domain.usecase.DeleteFileAttachmentUseCase
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskAnswer
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import com.example.hits_processes_2.feature.task_detail.domain.usecase.AttachTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetAllUserTaskAnswersUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetMyTeamUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTeamFinalAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTaskDetailUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.SubmitTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.UnattachTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.UnsubmitTaskAnswerUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.GetTeamsUseCase
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
    private val getMyTeamUseCase: GetMyTeamUseCase,
    private val attachTaskAnswerUseCase: AttachTaskAnswerUseCase,
    private val getAllUserTaskAnswersUseCase: GetAllUserTaskAnswersUseCase,
    private val getTeamFinalAnswerUseCase: GetTeamFinalAnswerUseCase,
    private val submitTaskAnswerUseCase: SubmitTaskAnswerUseCase,
    private val unsubmitTaskAnswerUseCase: UnsubmitTaskAnswerUseCase,
    private val unattachTaskAnswerUseCase: UnattachTaskAnswerUseCase,
    private val deleteFileAttachmentUseCase: DeleteFileAttachmentUseCase,
    private val getTeamsUseCase: GetTeamsUseCase,
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
        if (resolvedUserRole == CourseDetailsRole.STUDENT) {
            loadMyTeam()
        } else {
            loadTeamsForTeacher()
        }
    }

    fun onEvent(event: TaskDetailUiEvent) {
        when (event) {
            TaskDetailUiEvent.BackClicked -> sendEffect(TaskDetailUiEffect.NavigateBack)
            TaskDetailUiEvent.RetryClicked -> loadTaskDetail()
            is TaskDetailUiEvent.FileClicked -> sendEffect(TaskDetailUiEffect.StartFileDownload(event.fileId))
            is TaskDetailUiEvent.SubmissionFilesPicked -> {
                if (!_state.value.isInTeam) return
                if ((_state.value.teamFinalAnswer?.score ?: 0) > 0) return
                if (_state.value.myAttachedAnswers.isNotEmpty()) return
                if (event.files.isEmpty()) return
                updateState { copy(isUploadingFiles = true) }
                sendEffect(TaskDetailUiEffect.StartFileUpload(event.files.map { it.uriString }))
            }
            is TaskDetailUiEvent.UploadedSubmissionFileRemoved -> {
                val current = _state.value.uploadedSubmissionFiles
                if (event.index !in current.indices) return
                if ((_state.value.teamFinalAnswer?.score ?: 0) > 0) return
                val removed = current[event.index]
                updateState {
                    copy(
                        uploadedSubmissionFiles = uploadedSubmissionFiles.toMutableList().also { it.removeAt(event.index) },
                    )
                }
                viewModelScope.launch {
                    deleteFileAttachmentUseCase(removed.id)
                        .onFailure {
                            sendEffect(
                                TaskDetailUiEffect.ShowMessage(
                                    it.message ?: strings.getString(R.string.file_attachment_error_delete),
                                ),
                            )
                        }
                }
            }
            TaskDetailUiEvent.AttachAnswerClicked -> {
                if ((_state.value.teamFinalAnswer?.score ?: 0) > 0) return
                if (_state.value.myAttachedAnswers.isNotEmpty()) {
                    sendEffect(
                        TaskDetailUiEffect.ShowMessage(
                            strings.getString(R.string.task_detail_submission_my_files_blocked_hint),
                        ),
                    )
                    return
                }
                val files = _state.value.uploadedSubmissionFiles
                if (files.isEmpty()) {
                    sendMessage(R.string.task_detail_attach_no_files)
                    return
                }
                updateState { copy(isAttaching = true) }
                viewModelScope.launch {
                    attachTaskAnswerUseCase(taskId, files)
                        .onSuccess { result ->
                            val attachedAnswer = result.newTaskAnswerId
                                .takeIf { it.isNotBlank() }
                                ?.let { answerId ->
                                    TaskAnswer(
                                        id = answerId,
                                        files = files,
                                    )
                                }
                            updateState {
                                copy(
                                    isAttaching = false,
                                    uploadedSubmissionFiles = emptyList(),
                                    myAttachedAnswers = attachedAnswer
                                        ?.let { myAttachedAnswers + it }
                                        ?: myAttachedAnswers,
                                    teamFinalAnswer = result.teamFinalAnswer,
                                )
                            }
                            if (attachedAnswer == null) {
                                refreshMyAttachedAnswers()
                            }
                            sendMessage(R.string.task_detail_attach_success)
                        }
                        .onFailure { error ->
                            updateState { copy(isAttaching = false) }
                            sendEffect(
                                TaskDetailUiEffect.ShowMessage(
                                    error.message ?: strings.getString(R.string.task_detail_attach_error),
                                ),
                            )
                        }
                }
            }
            is TaskDetailUiEvent.FilesUploaded -> {
                updateState {
                    copy(
                        isUploadingFiles = false,
                        uploadedSubmissionFiles = (uploadedSubmissionFiles + event.files).distinctBy { it.id },
                    )
                }
            }
            TaskDetailUiEvent.CancelSubmissionClicked -> {
                val toDelete = _state.value.uploadedSubmissionFiles
                updateState { copy(uploadedSubmissionFiles = emptyList()) }
                viewModelScope.launch {
                    toDelete.forEach { file ->
                        deleteFileAttachmentUseCase(file.id)
                    }
                }
            }
            TaskDetailUiEvent.CancelMyAttachedAnswersClicked -> {
                val current = _state.value
                if (!current.isInTeam) return
                if (current.isUploadingFiles || current.isAttaching || current.isSubmitting) return
                if ((current.teamFinalAnswer?.score ?: 0) > 0) return

                updateState { copy(isAttaching = true) }
                viewModelScope.launch {
                    val answers = if (current.myAttachedAnswers.any { it.id.isBlank() }) {
                        getAllUserTaskAnswersUseCase(taskId)
                            .onSuccess { refreshedAnswers ->
                                updateState { copy(myAttachedAnswers = refreshedAnswers) }
                            }
                            .getOrElse { error ->
                                updateState { copy(isAttaching = false) }
                                sendEffect(
                                    TaskDetailUiEffect.ShowMessage(
                                        error.message ?: strings.getString(R.string.task_detail_unattach_error),
                                    ),
                                )
                                return@launch
                            }
                    } else {
                        current.myAttachedAnswers
                    }
                    val toUnattach = answers
                        .filter { it.id.isNotBlank() }
                        .distinctBy { it.id }
                    if (toUnattach.isEmpty()) {
                        updateState { copy(isAttaching = false) }
                        return@launch
                    }
                    var lastFinalAnswer = current.teamFinalAnswer
                    toUnattach.forEach { answer ->
                        unattachTaskAnswerUseCase(taskId, answer.id)
                            .onSuccess { finalAnswer ->
                                lastFinalAnswer = finalAnswer
                            }
                            .onFailure { error ->
                                updateState { copy(isAttaching = false) }
                                sendEffect(
                                    TaskDetailUiEffect.ShowMessage(
                                        error.message ?: strings.getString(R.string.task_detail_unattach_error),
                                    ),
                                )
                                return@launch
                            }
                    }
                    val removedAnswerIds = toUnattach.map { it.id }.toSet()
                    updateState {
                        copy(
                            isAttaching = false,
                            myAttachedAnswers = myAttachedAnswers.filterNot { it.id in removedAnswerIds },
                            teamFinalAnswer = lastFinalAnswer,
                        )
                    }
                    sendMessage(R.string.task_detail_unattach_success)
                }
            }
            TaskDetailUiEvent.SubmitAnswerClicked -> {
                val s = _state.value
                if (!s.isInTeam || !s.isCaptain) return
                if ((s.teamFinalAnswer?.score ?: 0) > 0) return
                if (s.isUploadingFiles || s.isAttaching || s.isSubmitting) return
                viewModelScope.launch {
                    updateState { copy(isSubmitting = true) }
                    submitTaskAnswerUseCase(taskId)
                        .onSuccess {
                            updateState { copy(isSubmitting = false) }
                            refreshTeamFinalAnswer()
                            sendMessage(R.string.task_detail_submit_success)
                        }
                        .onFailure { error ->
                            updateState { copy(isSubmitting = false) }
                            sendEffect(
                                TaskDetailUiEffect.ShowMessage(
                                    error.message ?: strings.getString(R.string.task_detail_submit_error),
                                ),
                            )
                        }
                }
            }
            TaskDetailUiEvent.UnsubmitAnswerClicked -> {
                val s = _state.value
                if (!s.isInTeam || !s.isCaptain) return
                if ((s.teamFinalAnswer?.score ?: 0) > 0) return
                if (s.isUploadingFiles || s.isAttaching || s.isSubmitting) return
                viewModelScope.launch {
                    updateState { copy(isSubmitting = true) }
                    unsubmitTaskAnswerUseCase(taskId)
                        .onSuccess {
                            updateState { copy(isSubmitting = false) }
                            refreshTeamFinalAnswer()
                            sendMessage(R.string.task_detail_unsubmit_success)
                        }
                        .onFailure { error ->
                            updateState { copy(isSubmitting = false) }
                            sendEffect(
                                TaskDetailUiEffect.ShowMessage(
                                    error.message ?: strings.getString(R.string.task_detail_unsubmit_error),
                                ),
                            )
                        }
                }
            }
            TaskDetailUiEvent.TeamsClicked -> openTeamsOrDraft()
            TaskDetailUiEvent.CaptainSelectionClicked -> openCaptainSelection()
            TaskDetailUiEvent.EvaluateClicked -> sendMessage(R.string.task_detail_evaluate_unavailable)
            TaskDetailUiEvent.EditClicked -> sendEffect(TaskDetailUiEffect.NavigateToEdit(courseId, taskId))
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

    private fun loadMyTeam() {
        viewModelScope.launch {
            getMyTeamUseCase(courseId, taskId)
                .onSuccess { team ->
                    updateState { copy(isInTeam = true, isCaptain = team.isCaptain, myTeamId = team.id) }
                    refreshMyAttachedAnswers()
                    team.id?.let { refreshTeamFinalAnswer(it) }
                }
                .onFailure {
                    updateState { copy(isInTeam = false, isCaptain = false, myTeamId = null) }
                }
        }
    }

    private fun loadTeamsForTeacher() {
        viewModelScope.launch {
            getTeamsUseCase(courseId, taskId)
                .onSuccess { teams ->
                    updateState { copy(teacherTeams = teams) }
                }
                .onFailure {
                    updateState { copy(teacherTeams = emptyList()) }
                }
        }
    }

    private fun refreshMyAttachedAnswers() {
        viewModelScope.launch {
            getAllUserTaskAnswersUseCase(taskId)
                .onSuccess { answers ->
                    updateState { copy(myAttachedAnswers = answers) }
                }
                .onFailure {
                    updateState { copy(myAttachedAnswers = emptyList()) }
                }
        }
    }

    private fun refreshTeamFinalAnswer(teamId: String? = null) {
        val resolvedTeamId = teamId ?: _state.value.myTeamId
        if (resolvedTeamId.isNullOrBlank()) return
        viewModelScope.launch {
            getTeamFinalAnswerUseCase(taskId, resolvedTeamId)
                .onSuccess { finalAnswer ->
                    updateState { copy(teamFinalAnswer = finalAnswer) }
                }
                .onFailure {
                    updateState { copy(teamFinalAnswer = null) }
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
