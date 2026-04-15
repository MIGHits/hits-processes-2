package com.example.hits_processes_2.feature.task_creation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.resources.StringResourceProvider
import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.usecase.UploadFileAttachmentUseCase
import com.example.hits_processes_2.feature.task_creation.domain.model.CreateTaskData
import com.example.hits_processes_2.feature.task_creation.domain.model.TaskAnswerFinalizationType
import com.example.hits_processes_2.feature.task_creation.domain.model.TeamFormationType
import com.example.hits_processes_2.feature.task_creation.domain.usecase.CreateTaskUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant

class TaskCreationViewModel(
    private val createTaskUseCase: CreateTaskUseCase,
    private val uploadFileAttachmentUseCase: UploadFileAttachmentUseCase,
    private val strings: StringResourceProvider,
    private val courseId: String?,
) : ViewModel() {

    private val _state = MutableStateFlow(TaskCreationUiState())
    val state: StateFlow<TaskCreationUiState> = _state.asStateFlow()

    private val _effects = Channel<TaskCreationUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: TaskCreationUiEvent) {
        when (event) {
            is TaskCreationUiEvent.TitleChanged -> updateState {
                copy(title = event.text)
            }

            is TaskCreationUiEvent.TaskTextChanged -> updateState {
                copy(taskText = event.text)
            }

            is TaskCreationUiEvent.MaxScoreChanged -> updateState {
                copy(maxScore = event.value.filter(Char::isDigit).take(3))
            }

            is TaskCreationUiEvent.DeadlineSelected -> updateState {
                copy(deadlineMillis = event.millis)
            }

            is TaskCreationUiEvent.FilesSelected -> updateState {
                copy(attachedFiles = event.files)
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

            is TaskCreationUiEvent.FinalizationRuleSelected -> updateState {
                copy(
                    finalizationRule = event.rule,
                    isFinalizationDropdownExpanded = false,
                )
            }

            is TaskCreationUiEvent.TeamCountChanged -> updateState {
                copy(teamCount = event.count.filter(Char::isDigit))
            }

            TaskCreationUiEvent.TeamFormationDropdownToggled -> updateState {
                copy(isTeamFormationDropdownExpanded = !isTeamFormationDropdownExpanded)
            }

            TaskCreationUiEvent.FinalizationDropdownToggled -> updateState {
                copy(isFinalizationDropdownExpanded = !isFinalizationDropdownExpanded)
            }

            TaskCreationUiEvent.CreateTaskClicked -> createTask()

            TaskCreationUiEvent.BackClicked -> sendEffect(TaskCreationUiEffect.NavigateBack)
        }
    }

    private fun createTask() {
        val snapshot = _state.value
        val resolvedCourseId = courseId?.takeIf(String::isNotBlank)
        val maxScore = snapshot.maxScore.toIntOrNull()
        val teamCount = snapshot.teamCount.toIntOrNull()

        when {
            resolvedCourseId == null -> showError(R.string.task_creation_error_course_not_found)
            snapshot.title.trim().length < 4 -> showError(R.string.task_creation_error_title_too_short)
            snapshot.taskText.trim().length < 4 -> showError(R.string.task_creation_error_text_too_short)
            maxScore == null || maxScore !in 1..100 -> showError(R.string.task_creation_error_invalid_max_score)
            snapshot.deadlineMillis == null -> showError(R.string.task_creation_error_deadline_required)
            snapshot.teamFormationRule == null -> showError(R.string.task_creation_error_team_rule_required)
            snapshot.finalizationRule == null -> sendEffect(TaskCreationUiEffect.ShowError("Выберите способ определения итогового решения"))
            teamCount == null || teamCount <= 0 -> showError(R.string.task_creation_error_invalid_team_count)
            else -> submitTask(
                courseId = resolvedCourseId,
                maxScore = maxScore,
                teamCount = teamCount,
                snapshot = snapshot,
            )
        }
    }

    private fun submitTask(
        courseId: String,
        maxScore: Int,
        teamCount: Int,
        snapshot: TaskCreationUiState,
    ) {
        viewModelScope.launch {
            updateState { copy(isCreating = true) }

            val uploadedFileIds = mutableListOf<String>()
            for (file in snapshot.attachedFiles) {
                val uploadedFile = uploadFileAttachmentUseCase(
                    FileAttachmentUpload(
                        fileName = file.name,
                        uriString = file.uriString,
                    ),
                ).getOrElse { exception ->
                    updateState { copy(isCreating = false) }
                    sendEffect(TaskCreationUiEffect.ShowError(exception.message ?: strings.getString(R.string.file_attachment_error_upload)))
                    return@launch
                }

                uploadedFileIds += uploadedFile.id
            }

            val createTaskResult = createTaskUseCase(
                courseId = courseId,
                data = CreateTaskData(
                    title = snapshot.title.trim(),
                    text = snapshot.taskText.trim(),
                    maxScore = maxScore,
                    deadlineTimeIso = Instant.ofEpochMilli(snapshot.deadlineMillis!!).toString(),
                    teamFormationType = snapshot.teamFormationRule!!.toDomain(),
                    taskAnswerFinalizationType = snapshot.finalizationRule!!.toDomain(),
                    teamsAmount = teamCount,
                    fileIds = uploadedFileIds,
                ),
            )

            updateState { copy(isCreating = false) }

            createTaskResult
                .onSuccess { sendEffect(TaskCreationUiEffect.TaskCreated) }
                .onFailure { exception ->
                    sendEffect(
                        TaskCreationUiEffect.ShowError(
                            exception.message ?: strings.getString(R.string.task_creation_error_create_failed),
                        ),
                    )
                }
        }
    }

    private fun showError(resId: Int) {
        sendEffect(TaskCreationUiEffect.ShowError(strings.getString(resId)))
    }

    private fun updateState(transform: TaskCreationUiState.() -> TaskCreationUiState) {
        _state.value = _state.value.transform()
    }

    private fun sendEffect(effect: TaskCreationUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}

private fun TaskAnswerFinalizationRule.toDomain(): TaskAnswerFinalizationType {
    return when (this) {
        TaskAnswerFinalizationRule.FIRST -> TaskAnswerFinalizationType.FIRST_ATTACHMENT
        TaskAnswerFinalizationRule.LAST -> TaskAnswerFinalizationType.LAST_ATTACHMENT
        TaskAnswerFinalizationRule.CAPTAIN -> TaskAnswerFinalizationType.CAPTAIN_CHOOSE
        TaskAnswerFinalizationRule.MOST_VOTES -> TaskAnswerFinalizationType.MOST_VOTES
        TaskAnswerFinalizationRule.QUALIFIED_MAJORITY -> TaskAnswerFinalizationType.QUALIFIED_MAJORITY
    }
}

private fun TeamFormationRule.toDomain(): TeamFormationType {
    return when (this) {
        TeamFormationRule.RANDOM -> TeamFormationType.RANDOM
        TeamFormationRule.STUDENTS -> TeamFormationType.FREE
        TeamFormationRule.TEACHER -> TeamFormationType.CUSTOM
        TeamFormationRule.DRAFT -> TeamFormationType.DRAFT
    }
}
