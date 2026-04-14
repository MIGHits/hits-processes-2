package com.example.hits_processes_2.feature.task_detail.presentation

import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

sealed interface TaskDetailUiEvent {
    data object BackClicked : TaskDetailUiEvent
    data object RetryClicked : TaskDetailUiEvent
    data class FileClicked(val fileId: String) : TaskDetailUiEvent
    data class SubmissionFilesChanged(val files: List<SelectedFileAttachment>) : TaskDetailUiEvent
    data class SubmissionFileRemoved(val index: Int) : TaskDetailUiEvent
    data object SubmitClicked : TaskDetailUiEvent
    data object CancelSubmissionClicked : TaskDetailUiEvent
    data object TeamsClicked : TaskDetailUiEvent
    data object CaptainSelectionClicked : TaskDetailUiEvent
    data object EvaluateClicked : TaskDetailUiEvent
    data object EditClicked : TaskDetailUiEvent
}
