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
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetAllTeamTaskAnswersUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.AttachTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetAllUserTaskAnswersUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetAllUserVotedTaskAnswersUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetMyTeamUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTeamFinalAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.GetTaskDetailUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.SelectTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.SubmitTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.UnattachTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.UnsubmitTaskAnswerUseCase
import com.example.hits_processes_2.feature.task_detail.domain.usecase.VoteForTaskAnswerUseCase
import com.example.hits_processes_2.feature.teams.domain.usecase.GetTeamsUseCase
import com.example.hits_processes_2.feature.voting.presentation.VotingOption
import com.example.hits_processes_2.feature.voting.presentation.VotingSolutionFile
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
    private val getAllTeamTaskAnswersUseCase: GetAllTeamTaskAnswersUseCase,
    private val getAllUserVotedTaskAnswersUseCase: GetAllUserVotedTaskAnswersUseCase,
    private val getTeamFinalAnswerUseCase: GetTeamFinalAnswerUseCase,
    private val voteForTaskAnswerUseCase: VoteForTaskAnswerUseCase,
    private val selectTaskAnswerUseCase: SelectTaskAnswerUseCase,
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
                if (!_state.value.canAttachStudentAnswer) return
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
                if (!_state.value.canAttachStudentAnswer) return
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
            TaskDetailUiEvent.VotingClicked -> openVotingDialog()
            TaskDetailUiEvent.VotingDismissed -> updateState {
                copy(isVotingDialogVisible = false, selectedVotingAnswerId = null)
            }
            is TaskDetailUiEvent.VotingOptionSelected -> submitVote(event.answerId)
            TaskDetailUiEvent.CaptainChoiceClicked -> openCaptainChoiceDialog()
            TaskDetailUiEvent.CaptainChoiceDismissed -> updateState {
                copy(isCaptainChoiceDialogVisible = false, selectedCaptainChoiceAnswerId = null)
            }
            is TaskDetailUiEvent.CaptainChoiceOptionSelected -> submitCaptainChoice(event.answerId)
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

    private fun refreshDraftEnded(task: TaskDetail) {
        val draftId = task.draftId
        if (!task.isDraft || draftId.isNullOrBlank()) {
            updateState { copy(isDraftEnded = false) }
            return
        }

        viewModelScope.launch {
            getDraftUseCase(draftId)
                .onSuccess { draft ->
                    updateState { copy(isDraftEnded = draft.isEnded) }
                }
                .onFailure {
                    updateState { copy(isDraftEnded = false) }
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
                            isDraftEnded = false,
                            showCaptainSelectionAction = false,
                            errorMessage = null,
                        )
                    }
                    refreshCaptainSelectionAction()
                    refreshDraftEnded(task)
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

    private fun openVotingDialog() {
        val current = _state.value
        val task = current.task ?: return
        val teamId = current.myTeamId ?: return
        if (!task.isVotingFinalization || !current.isInTeam) return

        viewModelScope.launch {
            updateState { copy(isVotingLoading = true, isVotingDialogVisible = true) }
            val teamAnswers = getAllTeamTaskAnswersUseCase(taskId, teamId)
                .getOrElse { error ->
                    updateState { copy(isVotingLoading = false, isVotingDialogVisible = false) }
                    sendEffect(TaskDetailUiEffect.ShowMessage(error.message ?: "РќРµ СѓРґР°Р»РѕСЃСЊ Р·Р°РіСЂСѓР·РёС‚СЊ РІР°СЂРёР°РЅС‚С‹ РіРѕР»РѕСЃРѕРІР°РЅРёСЏ"))
                    return@launch
                }
            val votedAnswers = getAllUserVotedTaskAnswersUseCase(taskId)
                .getOrElse { emptyList() }

            updateState {
                copy(
                    votingOptions = teamAnswers.map(TaskAnswer::toVotingOption),
                    selectedVotingAnswerId = votedAnswers.firstOrNull()?.id,
                    isVotingLoading = false,
                    isVotingDialogVisible = true,
                )
            }
        }
    }

    private fun submitVote(answerId: String) {
        val previousSelectedAnswerId = _state.value.selectedVotingAnswerId
        val nextSelectedAnswerId = if (previousSelectedAnswerId == answerId) null else answerId
        viewModelScope.launch {
            updateState {
                copy(
                    isVotingLoading = true,
                    selectedVotingAnswerId = nextSelectedAnswerId,
                )
            }
            voteForTaskAnswerUseCase(taskId, answerId)
                .onSuccess { finalAnswer ->
                    updateState {
                        copy(
                            teamFinalAnswer = finalAnswer,
                            isVotingLoading = false,
                        )
                    }
                    refreshVotingOptions()
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isVotingLoading = false,
                            selectedVotingAnswerId = previousSelectedAnswerId,
                        )
                    }
                    sendEffect(TaskDetailUiEffect.ShowMessage(error.message ?: "РќРµ СѓРґР°Р»РѕСЃСЊ РѕС‚РґР°С‚СЊ РіРѕР»РѕСЃ"))
                }
        }
    }

    private fun openCaptainChoiceDialog() {
        val current = _state.value
        val task = current.task ?: return
        val teamId = current.myTeamId ?: return
        if (!task.isCaptainChoiceFinalization || !current.isInTeam || !current.isCaptain) return

        viewModelScope.launch {
            updateState { copy(isCaptainChoiceLoading = true, isCaptainChoiceDialogVisible = true) }
            val teamAnswers = getAllTeamTaskAnswersUseCase(taskId, teamId)
                .getOrElse { error ->
                    updateState { copy(isCaptainChoiceLoading = false, isCaptainChoiceDialogVisible = false) }
                    sendEffect(
                        TaskDetailUiEffect.ShowMessage(
                            error.message ?: "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0440\u0435\u0448\u0435\u043d\u0438\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b",
                        ),
                    )
                    return@launch
                }

            updateState {
                copy(
                    votingOptions = teamAnswers.map(TaskAnswer::toVotingOption),
                    selectedCaptainChoiceAnswerId = teamAnswers.firstOrNull(TaskAnswer::finalDecision)?.id,
                    isCaptainChoiceLoading = false,
                    isCaptainChoiceDialogVisible = true,
                )
            }
        }
    }

    private fun submitCaptainChoice(answerId: String) {
        val previousSelectedAnswerId = _state.value.selectedCaptainChoiceAnswerId
        if (previousSelectedAnswerId == answerId) return

        viewModelScope.launch {
            updateState {
                copy(
                    isCaptainChoiceLoading = true,
                    selectedCaptainChoiceAnswerId = answerId,
                )
            }
            selectTaskAnswerUseCase(taskId, answerId)
                .onSuccess {
                    refreshCaptainChoiceOptions()
                    refreshTeamFinalAnswer()
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isCaptainChoiceLoading = false,
                            selectedCaptainChoiceAnswerId = previousSelectedAnswerId,
                        )
                    }
                    sendEffect(
                        TaskDetailUiEffect.ShowMessage(
                            error.message ?: "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u0440\u0435\u0448\u0435\u043d\u0438\u0435",
                        ),
                    )
                }
        }
    }

    private fun refreshVotingOptions() {
        val current = _state.value
        val teamId = current.myTeamId ?: return
        viewModelScope.launch {
            getAllTeamTaskAnswersUseCase(taskId, teamId)
                .onSuccess { answers ->
                    val votedAnswers = getAllUserVotedTaskAnswersUseCase(taskId).getOrElse { emptyList() }
                    updateState {
                        copy(
                            votingOptions = answers.map(TaskAnswer::toVotingOption),
                            selectedVotingAnswerId = votedAnswers.firstOrNull()?.id,
                        )
                    }
                }
        }
    }

    private fun refreshCaptainChoiceOptions() {
        val current = _state.value
        val teamId = current.myTeamId ?: return
        viewModelScope.launch {
            getAllTeamTaskAnswersUseCase(taskId, teamId)
                .onSuccess { answers ->
                    updateState {
                        copy(
                            votingOptions = answers.map(TaskAnswer::toVotingOption),
                            selectedCaptainChoiceAnswerId = answers.firstOrNull(TaskAnswer::finalDecision)?.id,
                            isCaptainChoiceLoading = false,
                        )
                    }
                }
                .onFailure {
                    updateState { copy(isCaptainChoiceLoading = false) }
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

private val TaskDetail.isVotingFinalization: Boolean
    get() = taskAnswerFinalizationType in setOf("MOST_VOTES", "QUALIFIED_MAJORITY")

private val TaskDetail.isCaptainChoiceFinalization: Boolean
    get() = taskAnswerFinalizationType == "CAPTAIN_CHOOSE"

private val TaskDetailUiState.canAttachStudentAnswer: Boolean
    get() {
        val task = task ?: return false
        return isInTeam && (!task.isDraft || isDraftEnded)
    }

private fun TaskAnswer.toVotingOption(): VotingOption {
    val author = user
    return VotingOption(
        id = id,
        firstName = author?.firstName.orEmpty(),
        lastName = author?.lastName.orEmpty(),
        solutionFiles = files.map { file ->
            VotingSolutionFile(
                id = file.id,
                name = file.fileName.ifBlank { file.id },
            )
        },
        votesCount = votesCount,
    )
}

