package com.example.hits_processes_2.feature.task_edit.presentation

import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment
import com.example.hits_processes_2.feature.task_edit.domain.model.EditTaskExistingFile

data class TaskEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val title: String = "",
    val text: String = "",
    val maxScore: String = "",
    val initialDeadlineMillis: Long? = null,
    val deadlineMillis: Long? = null,
    val existingFiles: List<EditTaskExistingFile> = emptyList(),
    val newFiles: List<SelectedFileAttachment> = emptyList(),
    val errorMessage: String? = null,
)
