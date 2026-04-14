package com.example.hits_processes_2.feature.task_edit.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.resources.StringResourceProvider
import com.example.hits_processes_2.feature.file_attachment.domain.model.FileAttachmentUpload
import com.example.hits_processes_2.feature.file_attachment.domain.usecase.UploadFileAttachmentUseCase
import com.example.hits_processes_2.feature.task_edit.domain.model.UpdateTaskData
import com.example.hits_processes_2.feature.task_edit.domain.usecase.EditTaskUseCase
import com.example.hits_processes_2.feature.task_edit.domain.usecase.GetTaskEditDetailsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeParseException

class TaskEditViewModel(
    private val courseId: String,
    private val taskId: String,
    private val getTaskEditDetailsUseCase: GetTaskEditDetailsUseCase,
    private val editTaskUseCase: EditTaskUseCase,
    private val uploadFileAttachmentUseCase: UploadFileAttachmentUseCase,
    private val strings: StringResourceProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(TaskEditUiState())
    val state: StateFlow<TaskEditUiState> = _state.asStateFlow()

    private val _effects = Channel<TaskEditUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        loadTask()
    }

    fun onEvent(event: TaskEditUiEvent) {
        when (event) {
            TaskEditUiEvent.BackClicked -> sendEffect(TaskEditUiEffect.NavigateBack)
            TaskEditUiEvent.CancelClicked -> sendEffect(TaskEditUiEffect.NavigateBack)
            TaskEditUiEvent.SaveClicked -> saveTask()
            TaskEditUiEvent.RetryClicked -> loadTask()
            TaskEditUiEvent.InvalidDeadlineSelected -> {
                showMessage(R.string.task_edit_error_deadline_before_allowed)
            }
            is TaskEditUiEvent.TitleChanged -> updateState { copy(title = event.value) }
            is TaskEditUiEvent.TextChanged -> updateState { copy(text = event.value) }
            is TaskEditUiEvent.MaxScoreChanged -> updateState {
                copy(maxScore = event.value.filter(Char::isDigit).take(3))
            }
            is TaskEditUiEvent.DeadlineChanged -> {
                val minimumDeadline = resolveMinAllowedDeadline(state.value.initialDeadlineMillis)
                if (minimumDeadline != null && event.millis < minimumDeadline) {
                    showMessage(R.string.task_edit_error_deadline_before_allowed)
                } else {
                    updateState { copy(deadlineMillis = event.millis) }
                }
            }
            is TaskEditUiEvent.NewFilesSelected -> updateState {
                copy(newFiles = event.files)
            }
            is TaskEditUiEvent.ExistingFileRemoved -> updateState {
                copy(existingFiles = existingFiles.filterNot { it.id == event.fileId })
            }
            is TaskEditUiEvent.NewFileRemoved -> updateState {
                copy(
                    newFiles = newFiles.toMutableList().also { files ->
                        if (event.index in files.indices) files.removeAt(event.index)
                    },
                )
            }
        }
    }

    private fun loadTask() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }
            getTaskEditDetailsUseCase(courseId, taskId)
                .onSuccess { task ->
                    val parsedDeadline = task.deadlineIso?.toEpochMillis()
                    updateState {
                        copy(
                            isLoading = false,
                            isSaving = false,
                            title = task.title,
                            text = task.text,
                            maxScore = task.maxScore.toString(),
                            initialDeadlineMillis = parsedDeadline,
                            deadlineMillis = parsedDeadline,
                            existingFiles = task.files,
                            newFiles = emptyList(),
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: strings.getString(R.string.task_edit_error_load),
                        )
                    }
                }
        }
    }

    private fun saveTask() {
        val snapshot = state.value
        val maxScore = snapshot.maxScore.toIntOrNull()
        val deadlineMillis = snapshot.deadlineMillis

        when {
            snapshot.title.trim().length < 4 -> showMessage(R.string.task_edit_error_title)
            snapshot.text.trim().length < 4 -> showMessage(R.string.task_edit_error_text)
            maxScore == null || maxScore !in 1..100 -> showMessage(R.string.task_edit_error_max_score)
            deadlineMillis == null -> showMessage(R.string.task_edit_error_deadline)
            resolveMinAllowedDeadline(snapshot.initialDeadlineMillis)?.let { deadlineMillis < it } == true ->
                showMessage(R.string.task_edit_error_deadline_before_allowed)
            else -> submitChanges(snapshot, maxScore, deadlineMillis)
        }
    }

    private fun submitChanges(
        snapshot: TaskEditUiState,
        maxScore: Int,
        deadlineMillis: Long,
    ) {
        viewModelScope.launch {
            updateState { copy(isSaving = true) }

            val uploadedFileIds = mutableListOf<String>()
            for (file in snapshot.newFiles) {
                val uploadedFile = uploadFileAttachmentUseCase(
                    FileAttachmentUpload(
                        fileName = file.name,
                        uriString = file.uriString,
                    ),
                ).getOrElse { error ->
                    updateState { copy(isSaving = false) }
                    sendEffect(
                        TaskEditUiEffect.ShowMessage(
                            error.message ?: strings.getString(R.string.file_attachment_error_upload),
                        ),
                    )
                    return@launch
                }
                uploadedFileIds += uploadedFile.id
            }

            editTaskUseCase(
                courseId = courseId,
                taskId = taskId,
                data = UpdateTaskData(
                    title = snapshot.title.trim(),
                    text = snapshot.text.trim(),
                    maxScore = maxScore,
                    deadlineTimeIso = Instant.ofEpochMilli(deadlineMillis).toString(),
                    fileIds = snapshot.existingFiles.map { it.id } + uploadedFileIds,
                ),
            ).onSuccess {
                updateState { copy(isSaving = false) }
                sendEffect(TaskEditUiEffect.NavigateBack)
            }.onFailure { error ->
                updateState { copy(isSaving = false) }
                sendEffect(
                    TaskEditUiEffect.ShowMessage(
                        error.message ?: strings.getString(R.string.task_edit_error_save),
                    ),
                )
            }
        }
    }

    private fun showMessage(resId: Int) {
        sendEffect(TaskEditUiEffect.ShowMessage(strings.getString(resId)))
    }

    private fun updateState(transform: TaskEditUiState.() -> TaskEditUiState) {
        _state.value = _state.value.transform()
    }

    private fun sendEffect(effect: TaskEditUiEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }

    private fun resolveMinAllowedDeadline(initialDeadlineMillis: Long?): Long? {
        val currentTime = System.currentTimeMillis()
        return initialDeadlineMillis?.coerceAtLeast(currentTime) ?: currentTime
    }
}

private fun String.toEpochMillis(): Long? {
    return parseOffsetDateTime()
        ?: parseInstant()
        ?: parseLocalDateTime()
}

private fun String.parseOffsetDateTime(): Long? {
    return runCatching { OffsetDateTime.parse(this).toInstant().toEpochMilli() }.getOrNull()
}

private fun String.parseInstant(): Long? {
    return runCatching { Instant.parse(this).toEpochMilli() }.getOrNull()
}

private fun String.parseLocalDateTime(): Long? {
    return try {
        LocalDateTime.parse(this)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (_: DateTimeParseException) {
        null
    }
}
