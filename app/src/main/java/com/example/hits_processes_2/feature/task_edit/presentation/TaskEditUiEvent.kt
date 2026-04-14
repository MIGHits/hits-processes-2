package com.example.hits_processes_2.feature.task_edit.presentation

import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

sealed interface TaskEditUiEvent {
    data object BackClicked : TaskEditUiEvent
    data object CancelClicked : TaskEditUiEvent
    data object SaveClicked : TaskEditUiEvent
    data object RetryClicked : TaskEditUiEvent
    data object InvalidDeadlineSelected : TaskEditUiEvent
    data class TitleChanged(val value: String) : TaskEditUiEvent
    data class TextChanged(val value: String) : TaskEditUiEvent
    data class MaxScoreChanged(val value: String) : TaskEditUiEvent
    data class DeadlineChanged(val millis: Long) : TaskEditUiEvent
    data class NewFilesSelected(val files: List<SelectedFileAttachment>) : TaskEditUiEvent
    data class ExistingFileRemoved(val fileId: String) : TaskEditUiEvent
    data class NewFileRemoved(val index: Int) : TaskEditUiEvent
}
